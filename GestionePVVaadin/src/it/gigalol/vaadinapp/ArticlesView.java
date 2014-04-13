package it.gigalol.vaadinapp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeEvent;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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
	private String [] editable = new String [] { "NAME", "GROUP_ID", "DESCRIPTION","PRICE" };


	Button back = new Button("Back", new Button.ClickListener() {
		private static final long serialVersionUID = 8200131706333299060L;
		@Override
		public void buttonClick(ClickEvent event) {
			getUI().getNavigator().navigateTo(MainView.NAME);
		}
	});

	Button newItem = new Button("New");
	Button deleteItem = new Button("Delete");
	
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
			
		
		sc.addRowIdChangeListener(new RowIdChangeListener() {
			private static final long serialVersionUID = 3639203092384566508L;

			@Override
			public void rowIdChange(RowIdChangeEvent event) {
				// TODO Auto-generated method stub
				System.out.println("ROWID CHANGED");
			}
			
		});
		
		sc.addItemSetChangeListener(new ItemSetChangeListener() {
			private static final long serialVersionUID = 6014864348363390864L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				System.out.println("SET ITEM CHANGED");
				
			}
			
		});
		
		newItem.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				addContact();
				
			}
			
		});
		
		//Collection<?> c =  sc.getContainerPropertyIds();

		VerticalSplitPanel splitPanel = new VerticalSplitPanel();

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
		
		HorizontalLayout mainButtons = new HorizontalLayout(back,save,newItem,searchField);
		VerticalLayout bottomLayout = new VerticalLayout(table);	

		final VerticalLayout fieldsLayout = new VerticalLayout();
		fieldsLayout.addComponent(deleteItem);
		for (String s : editable) {

			TextField field = new TextField(s);
			fieldsLayout.addComponent(field);
			field.setWidth("100%");
			editorFields.bind(field, s);
			
		}
		fieldsLayout.setMargin(true);
		fieldsLayout.setVisible(false);;
		
		editorFields.setBuffered(false);
		editorFields.addCommitHandler(new CommitHandler() {
			private static final long serialVersionUID = 7921054396062333143L;

			public void preCommit(CommitEvent commitEvent)
					throws CommitException {
				System.out.println("PRE COMMIT");
				
			}

			@Override
			public void postCommit(CommitEvent commitEvent)
					throws CommitException {
				
				System.out.println("POST COMMIT");
			}
			
		});		
		table.setVisibleColumns((Object[])visible);
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
				fieldsLayout.setVisible(contactId != null);
			}
		});

		VerticalLayout topLayout = new VerticalLayout(mainButtons,fieldsLayout);
		topLayout.setWidth("100%");
		topLayout.setHeight("100px");

		bottomLayout.setHeight("100%");
		bottomLayout.setWidth("100%");

		splitPanel.setFirstComponent(topLayout);
		splitPanel.setSecondComponent(table);
		splitPanel.setSplitPosition(40f);
		splitPanel.setSizeFull();
		setCompositionRoot(splitPanel);
		
	}

	@Override
	public void enter(ViewChangeEvent event) {


	}
	
	public void addContact() {
		  /* Roll back changes just in case */
		  try {
		      sc.rollback();
		  } catch (SQLException ignored) {
		  }
		  Object tempItemId = sc.addItem();
		  editorFields.setItemDataSource(sc.getItem(tempItemId));
		  setReadOnly(false);
		}



	private class ContactFilter implements Filter {
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
