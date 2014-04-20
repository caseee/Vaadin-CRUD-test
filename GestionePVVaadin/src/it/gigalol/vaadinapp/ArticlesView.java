package it.gigalol.vaadinapp;

import java.io.Serializable;
import java.sql.SQLException;

import com.vaadin.data.Container.*;
import com.vaadin.data.*;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.*;
import com.vaadin.data.fieldgroup.*;
import com.vaadin.data.util.sqlcontainer.*;
import com.vaadin.event.FieldEvents.*;
import com.vaadin.navigator.*;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.*;

import de.steinwedel.messagebox.*;

public class ArticlesView extends CustomComponent implements View, Serializable, ClickListener,ValueChangeListener,MessageBoxListener {
	private static final long serialVersionUID = 2869411776027184262L;
	public static final String NAME = "articles";
	private final String [] searchable = new String [] { "NAME" };
	private final String [] visible = new String [] { "NAME", "PRICE" };
	private final String [] editable = new String [] { "NAME", "GROUP_ID", "DESCRIPTION","PRICE" };
	private final VerticalLayout fieldsLayout = new VerticalLayout();
	private final FieldGroup editorFields = new FieldGroup();
	private final Table table = new Table();
	private final SQLContainer sc;
	private final TextField searchField = new TextField();
	private final Button back = new Button("Back", this );
	private final Button newItem = new Button("New",this);
	private final Button deleteItem = new Button("Delete",this);
	private final Button saveItem = new Button("Save", this);
	private final Button discardItem = new Button("Discard",this);
	private Object lastContactId ;

	public ArticlesView() {
		Controller controller = VaadinSession.getCurrent().getAttribute(Controller.class);
		sc = controller.getArticlesContainer();

		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();

		this.setSizeFull();

		table.setContainerDataSource(sc);
		
		searchField.setInputPrompt("Search contacts");
		searchField.setWidth("100%");
		searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);
		searchField.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = 7120106518466783986L;
			@Override
			public void textChange(final TextChangeEvent event) {
				sc.removeAllContainerFilters();
				sc.addContainerFilter(new ListFilter(event.getText()));

			}
		});
		
		HorizontalLayout mainButtons = new HorizontalLayout(back,newItem,searchField);
		VerticalLayout bottomLayout = new VerticalLayout(table);	

		
		final HorizontalLayout fieldsButton = new HorizontalLayout();
		fieldsButton.addComponents(deleteItem,saveItem,discardItem);
		fieldsLayout.addComponents(fieldsButton);
		for (String s : editable) {

			TextField field = new TextField(s);
			fieldsLayout.addComponent(field);
			field.setWidth("100%");
			editorFields.bind(field, s);
			
		}
		
		fieldsLayout.setMargin(true);
		fieldsLayout.setVisible(false);
		editorFields.setBuffered(true);
			
		table.setVisibleColumns((Object[])visible);
		table.setSizeFull();
		table.setEditable(false);		
		table.setSelectable(true);
		table.setImmediate(true);
		table.addValueChangeListener(this);
		
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

	private boolean canChange() {
		
		if (editorFields.getItemDataSource()==null)
			return true;
		
		if (! editorFields.isModified()) 
			return true;
			
		MessageBox.showPlain(Icon.QUESTION, "Changed", "Commit changes?", this, ButtonId.YES, ButtonId.NO, ButtonId.CANCEL);
		return false;
		
	}
	
	@Override
	public void enter(ViewChangeEvent event) {


	}
	
	private void commit() {
		try {
			sc.commit();
			table.commit();
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
	
	private void discard() {
		  try {
			  editorFields.discard();
		      sc.rollback();
		  } catch (SQLException ignored) {
		  }
		  fieldsLayout.setVisible(false);
		  editorFields.setItemDataSource(null);
		  setReadOnly(false);
	}
	
	private void addContact() {
		  /* Roll back changes just in case */
		  try {
		      sc.rollback();
		  } catch (SQLException ignored) {
		  }
		  Object tempItemId = sc.addItem();
		  editorFields.setItemDataSource(sc.getItem(tempItemId));
		  setReadOnly(false);
		}

	private class ListFilter implements Filter {
		private static final long serialVersionUID = 1772636966694615094L;
		private String needle;

		public ListFilter(String needle) {
			this.needle = needle.toLowerCase();
		}

		@Override
		public boolean passesFilter(Object itemId, Item item) {

			StringBuffer sb = new StringBuffer("");

			for (String s : searchable) 
				sb.append(item.getItemProperty(s).getValue().toString().toLowerCase());

			return sb.toString().contains(needle);
		}
		
		@Override
		public boolean appliesToProperty(Object id) {
			return true;
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();
		
		if (source==saveItem) 
			commit();
		else if (source==discardItem)
			discard();
		else if (source==deleteItem)
			delete();
		else if (source==back)
			getUI().getNavigator().navigateTo(MainView.NAME);
		else if (source==newItem)
			addContact();
		
	}

	private void delete() {
		// TODO 
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
						
		Object contactId = table.getValue();
		
		if (contactId == lastContactId)
			return;
		
		if (!canChange()) 
			return;
			
		buttonClicked(ButtonId.YES);

	}
	
	private void changeTo(Object contactId) {
		if (contactId == lastContactId)
			return;
		lastContactId = contactId;
		if (contactId != null)
			editorFields.setItemDataSource(table.getItem(contactId));
		fieldsLayout.setVisible(contactId != null);		
	}

	@Override
	public void buttonClicked(ButtonId buttonId) {
		
		if (buttonId.equals(ButtonId.YES))  
			commit();		
		else if (buttonId.equals(ButtonId.NO))
			discard();
		
		changeTo(table.getValue());
		
	}
	

}
