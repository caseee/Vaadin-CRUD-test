package it.gigalol.vaadinapp;

import java.io.Serializable;
import java.sql.SQLException;

import com.vaadin.data.Container.*;
import com.vaadin.data.*;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.*;
import com.vaadin.data.fieldgroup.*;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.sqlcontainer.*;
import com.vaadin.event.FieldEvents.*;
import com.vaadin.navigator.*;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.*;

import de.steinwedel.messagebox.*;

/**
 * @author Marco Casella
 *
 */
public class ArticlesView extends CustomComponent implements View, Serializable, ClickListener, ValueChangeListener {
	private static final long serialVersionUID = 2869411776027184262L;
	public static final String NAME = "articles";
	public static final String BACK = MainView.NAME;
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
	private final Button searchButton = new Button("Search",this);
	private Object lastId ;

	public ArticlesView() {
		Controller controller = VaadinSession.getCurrent().getAttribute(Controller.class);
		sc = controller.getArticlesContainer();

		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();

		this.setSizeFull();

		table.setContainerDataSource(sc);

		searchField.setInputPrompt("Search contacts");
		searchField.setWidth("100%");
		searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);


		HorizontalLayout mainButtons = new HorizontalLayout(back,newItem,searchField,searchButton);
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

	/**
	 * If changes has been made in the fields show a messagebox and return true, return false otherwise
	 * 
	 * @param title title of the messagebox
	 * @param message message of the messagebox
	 * @param mbl listener for the messagebox
	 * @param btnids ids of buttons
	 * @return if action must be confirmed before commit
	 */
	private boolean needConfirm(String title, String message, MessageBoxListener mbl,  ButtonId ... btnids  ) {

		if (editorFields.getItemDataSource()==null)
			return false;

		if (! editorFields.isModified()) 
			return false;

		MessageBox.showPlain(Icon.QUESTION, title, message, mbl, btnids);
		return true;

	}

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {


	}

	/**
	 * Save changes to the SQLContainer
	 */
	private void commit() {
		try {
			editorFields.commit();
			table.commit();
			sc.commit();			
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
		} catch (CommitException e) {
			Notification.show("Error",
					"Error saving. Commit Exception.",
					Notification.Type.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Discard changes not yet saved.
	 */
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

	
	/**
	 *  Add item to the table.
	 */
	private void addItem() {
		/* Roll back changes just in case */
		try {
			sc.rollback();
		} catch (SQLException ignored) {
		}
		Object tempItemId = sc.addItem();
		editorFields.setItemDataSource(sc.getItem(tempItemId));
		fieldsLayout.setVisible(true);
		setReadOnly(false);
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();

		if (source==saveItem) 
			askForSave();
		else if (source==discardItem)
			askForDiscard();
		else if (source==deleteItem)
			askForDelete();
		else if (source==back)
			askForExit();
		else if (source==newItem)
			askForAdd();
		else if (source==searchButton)
			performSearch();

	}

	/**
	 *  Delete selected item from the table.
	 */
	private void delete() {
		fieldsLayout.setVisible(false);	
		table.removeItem(table.getValue());
		editorFields.setItemDataSource(null);
		commit();
	}

	/**
	 * Switch to the itemId
	 * @param itemId item to switch
	 */
	private void changeTo(Object itemId) {
		if (itemId == lastId)
			return;
		lastId = itemId;
		if (itemId != null)
			editorFields.setItemDataSource(table.getItem(itemId));
		fieldsLayout.setVisible(itemId != null);		
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
	 */
	@Override
	public void valueChange(ValueChangeEvent event) {

		if (table.getValue() == lastId)
			return;

		MessageBoxListener mbl = new AskBeforeChangeListener();
		
		if (needConfirm("Save changes?", "Save changes?",mbl, ButtonId.YES, ButtonId.NO, ButtonId.CANCEL)) 
			return;

		mbl.buttonClicked(ButtonId.IGNORE);

	}
	
	/**
	 * Execute a search
	 */
	private void performSearch() {
		  String searchTerm = (String) searchField.getValue();
		  if (searchTerm == null || searchTerm.equals("")) {
			  Notification.show("Search term cannot be empty!",Notification.Type.WARNING_MESSAGE);
		      return;
		  }
		  sc.removeAllContainerFilters();
		  sc.addContainerFilter(new ListFilter(searchTerm));
		  
	}
	
	/**
	 *  Create a messagebox listener, if needed shows it, otherwise call the listener with a dummy button.
	 */
	private void askForAdd() {
		MessageBoxListener mbl = new AskBeforeAddListener();
		
		if (needConfirm("Confirm", "Confirm add item?",mbl, ButtonId.YES, ButtonId.NO, ButtonId.CANCEL)) 
			return;
		
		mbl.buttonClicked(ButtonId.IGNORE);
	}
	
	/**
	 *  Create a messagebox listener, if needed shows it, otherwise call the listener with a dummy button.
	 */
	private void askForDiscard() {
		MessageBoxListener mbl = new AskBeforeDiscardListener();
		
		if (needConfirm("Confirm","Discard changes?",mbl, ButtonId.YES, ButtonId.NO)) 
			return;
		
		mbl.buttonClicked(ButtonId.IGNORE);
	}
	
	/**
	 *  Create a messagebox listener, if needed shows it, otherwise call the listener with a dummy button.
	 */
	private void askForSave() {
		MessageBoxListener mbl = new AskBeforeSaveListener();
		
		if (needConfirm("Confirm", "Save changes?", mbl, ButtonId.YES, ButtonId.NO)) 
			return;
		
		mbl.buttonClicked(ButtonId.IGNORE);
	}
	
	/**
	 *  Create a messagebox listener, if needed shows it, otherwise call the listener with a dummy button.
	 */
	private void askForExit() {
		
		MessageBoxListener mbl = new AskBeforeExitListener();
				
		if (needConfirm("Confirm", "Save changes before exit?",mbl, ButtonId.YES, ButtonId.NO, ButtonId.CANCEL)) 
			return;
		
		mbl.buttonClicked(ButtonId.IGNORE);
	}
	
	/**
	 *  Create a messagebox listener, if needed shows it, otherwise call the listener with a dummy button.
	 */
	private void askForDelete() {
		if (table.getValue() == null)
			return;
		
		MessageBoxListener mbl = new AskBeforeDeleteListener();
		
		MessageBox.showPlain(Icon.QUESTION, "Confirm", "Delete?", mbl, ButtonId.YES, ButtonId.NO);
	
	}
	
	/**
	 *  Class implementing MessageBoxListener interface, discard change if needed 
	 */
	private class AskBeforeDiscardListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = -3673592681475973325L;

		/* (non-Javadoc)
		 * @see de.steinwedel.messagebox.MessageBoxListener#buttonClicked(de.steinwedel.messagebox.ButtonId)
		 */
		@Override
		public void buttonClicked(ButtonId buttonId) {

			if (buttonId.equals(ButtonId.YES))  
				discard();


		}

	}
	
	/**
	 *  Class implementing MessageBoxListener interface, change selected item if needed 
	 */
	private class AskBeforeChangeListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = -3673592681475973325L;

		@Override
		public void buttonClicked(ButtonId buttonId) {

			if (buttonId.equals(ButtonId.YES))  
				commit();		
			if (buttonId.equals(ButtonId.NO))
				discard();
			if (buttonId.equals(ButtonId.CANCEL))
				table.setValue(lastId);

			changeTo(table.getValue());

		}

	}
		
	/**
	 *  Class implementing MessageBoxListener interface, save change if needed 
	 */
	private class AskBeforeSaveListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = 5959274722049449150L;

		/* (non-Javadoc)
		 * @see de.steinwedel.messagebox.MessageBoxListener#buttonClicked(de.steinwedel.messagebox.ButtonId)
		 */
		@Override
		public void buttonClicked(ButtonId buttonId) {

			if (buttonId.equals(ButtonId.YES))  
				commit();		

		}

	}
	
	/**
	 *  Class implementing MessageBoxListener interface, change back view if needed 
	 */
	private class AskBeforeExitListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = 5959271122049449150L;

		/* (non-Javadoc)
		 * @see de.steinwedel.messagebox.MessageBoxListener#buttonClicked(de.steinwedel.messagebox.ButtonId)
		 */
		@Override
		public void buttonClicked(ButtonId buttonId) {

			if (buttonId.equals(ButtonId.CANCEL))
				return;	
			if (buttonId.equals(ButtonId.YES))  
				commit();		
			if (buttonId.equals(ButtonId.NO))
				discard();
			
			getUI().getNavigator().navigateTo(BACK);


		}

	}
	
	/**
	 *  Class implementing MessageBoxListener interface, add item if needed 
	 */
	private class AskBeforeAddListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = 5959414722049449111L;

		/* (non-Javadoc)
		 * @see de.steinwedel.messagebox.MessageBoxListener#buttonClicked(de.steinwedel.messagebox.ButtonId)
		 */
		@Override
		public void buttonClicked(ButtonId buttonId) {

			if (buttonId.equals(ButtonId.CANCEL))
				return;	
			if (buttonId.equals(ButtonId.YES))  
				commit();		
			if (buttonId.equals(ButtonId.NO))
				discard();
			
			addItem();


		}

	}

	/**
	 *  Class implementing MessageBoxListener interface, delete item if needed 
	 */
	private class AskBeforeDeleteListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = 5959414722049449111L;

		/* (non-Javadoc)
		 * @see de.steinwedel.messagebox.MessageBoxListener#buttonClicked(de.steinwedel.messagebox.ButtonId)
		 */
		@Override
		public void buttonClicked(ButtonId buttonId) {

			if (buttonId.equals(ButtonId.YES))
				delete();		

		}

	}
	
	/**
	 *  Class implementing Filter interface 
	 */
	private class ListFilter implements Filter, Serializable {
		private static final long serialVersionUID = 1772636966694615094L;
		private String needle;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return needle.hashCode();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			return needle.equals(obj);
		}
		
		/**
		 * @param needle string to search
		 */
		public ListFilter(String needle) {
			this.needle = needle.toLowerCase();
		}

		/* (non-Javadoc)
		 * @see com.vaadin.data.Container.Filter#passesFilter(java.lang.Object, com.vaadin.data.Item)
		 */
		@Override
		public boolean passesFilter(Object itemId, Item item) {

			StringBuffer sb = new StringBuffer("");

			for (String s : searchable) 
				sb.append(item.getItemProperty(s).getValue().toString().toLowerCase());

			return sb.toString().contains(needle);
		}

		/* (non-Javadoc)
		 * @see com.vaadin.data.Container.Filter#appliesToProperty(java.lang.Object)
		 */
		@Override
		public boolean appliesToProperty(Object id) {
			return false;
		}
	}
	
}


