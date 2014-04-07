package it.gigalol.vaadinapp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class ArticlesView extends CustomComponent implements View{
	private static final long serialVersionUID = 2869411776027184262L;
	public static final String NAME = "articles";
	private FieldGroup editorFields = new FieldGroup();
	Table table = new Table();
	SQLContainer sc;
	TextField searchField = new TextField();

	private String [] searchable = new String [] { "NAME" };
	private String [] visible = new String [] { "NAME", "PRICE" };
	private String [] editable = new String [] { "NAME", "DESCRIPRION","PRICE" };


	Button back = new Button("Back", new Button.ClickListener() {
		private static final long serialVersionUID = 8200131706333299060L;
		@Override
		public void buttonClick(ClickEvent event) {
			getUI().getNavigator().navigateTo(MainView.NAME);
		}
	});

	Button newItem = new Button("New");

	Button save = new Button("Save", new Button.ClickListener() {
		private static final long serialVersionUID = 8200131706333299060L;
		@Override
		public void buttonClick(ClickEvent event) {
			try {
				getSession().getAttribute(Controller.class).getArticlesContainer().commit();
			} catch (UnsupportedOperationException e) {
				Notification.show("Error",
						"Error saving. Unsupported Operation Exception. ",
						Notification.Type.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (SQLException e) {
				Notification.show("Error",
						"Error saving. SQLException.",
						Notification.Type.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	});

	public ArticlesView() {
		Controller controller = VaadinSession.getCurrent().getAttribute(Controller.class);
		sc = controller.getArticlesContainer();

		Collection<?> c =  sc.getContainerPropertyIds();


		VerticalSplitPanel vsp = new VerticalSplitPanel();

		this.setSizeFull();

		table.setContainerDataSource(sc);
		
		searchField.setInputPrompt("Search contacts");
		searchField.setWidth("100%");
		searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);
		searchField.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = 7120106518466783986L;
			public void textChange(final TextChangeEvent event) {
				sc.removeAllContainerFilters();
				sc.addContainerFilter(new ContactFilter(event.getText()));

			}
		});

		HorizontalLayout main_btns = new HorizontalLayout(back,save,searchField);
		VerticalLayout bottom = new VerticalLayout(table);	

		final VerticalLayout row_field = new VerticalLayout();
		row_field.addComponent(newItem);
		for (Object fieldName : c) {

			TextField field = new TextField((String) fieldName);
			row_field.addComponent(field);
			field.setWidth("100%");

			editorFields.bind(field, fieldName);
		}
		//row_field.setVisible(false);

		editorFields.setBuffered(false);

		table.setSizeFull();
		table.setEditable(false);		
		table.setSelectable(true);
		table.setImmediate(true);
		table.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = -3610099658368767058L;
			public void valueChange(ValueChangeEvent event) {
				Object contactId = table.getValue();
				if (contactId != null)
					editorFields.setItemDataSource(table.getItem(contactId));
				row_field.setVisible(contactId != null);
			}
		});

		VerticalLayout top = new VerticalLayout(main_btns,row_field);
		top.setWidth("100%");
		top.setHeight("100px");

		bottom.setHeight("100%");
		bottom.setWidth("100%");

		vsp.setFirstComponent(top);
		vsp.setSecondComponent(table);
		vsp.setSplitPosition(40f);
		vsp.setSizeFull();
		setCompositionRoot(vsp);
	}

	@Override
	public void enter(ViewChangeEvent event) {


	}

	private class ContactFilter implements Filter {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1772636966694615094L;
		private String needle;

		public ContactFilter(String needle) {
			this.needle = needle.toLowerCase();
		}

		public boolean passesFilter(Object itemId, Item item) {

			StringBuffer sb = new StringBuffer();

			for (String s : searchable) 
				sb.append(item.getItemProperty(s).getValue().toString().toLowerCase());

			return sb.toString().contains(needle);
		}

		public boolean appliesToProperty(Object id) {
			return true;
		}
	}

}
