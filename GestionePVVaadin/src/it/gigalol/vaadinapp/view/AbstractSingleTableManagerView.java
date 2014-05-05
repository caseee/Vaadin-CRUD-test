package it.gigalol.vaadinapp.view;

import it.gigalol.vaadinapp.Controller;

import java.io.Serializable;
import java.sql.SQLException;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;
import de.steinwedel.messagebox.MessageBoxListener;

public abstract class AbstractSingleTableManagerView extends CustomComponent implements View, Serializable, ClickListener, ValueChangeListener {
	private static final long serialVersionUID = 2869411776027184262L;

	private final Controller controller = VaadinSession.getCurrent().getAttribute(Controller.class);
	private final SQLContainer sc = controller.getArticlesContainer();
	
	private final FieldGroup editorFields = new FieldGroup();
	private final Table table = new Table();
	private final TextField searchField = new TextField();
	private final Button back = new Button("Back", this );
	private final Button newItem = new Button("New",this);
	private final Button deleteItem = new Button("Delete",this);
	private final Button saveItem = new Button("Save", this);
	private final Button discardItem = new Button("Discard",this);
	private final Button searchButton = new Button("Search",this);
	private final Button cancelSearchButton = new Button("Cancel search",this);
		
	private final HorizontalLayout leftTopLayout = new HorizontalLayout(back,newItem,searchField,searchButton,cancelSearchButton);
	private final VerticalLayout leftLayout = new VerticalLayout(leftTopLayout,table);
	private final HorizontalLayout rightTopLayout = new HorizontalLayout(deleteItem,saveItem,discardItem);
	private final VerticalLayout rightLayout = new VerticalLayout(rightTopLayout);
	private final HorizontalSplitPanel rootLayout = new HorizontalSplitPanel(leftLayout,rightLayout);
	
	private Object lastId ;
	
	protected abstract String getBackViewName();
	protected abstract String getViewName();
	protected abstract String[] getSearchableIds();
	protected abstract String[] getEditableIds();
	protected abstract String[] getVisibleIds();
		
	
	/**
	 * Sets the layouts of components
	 */
	private void initLayout() {
		this.setSizeFull();
		this.setCompositionRoot(rootLayout);
		rootLayout.setSplitPosition(50f);
		rootLayout.setSizeFull();
		leftLayout.setSizeFull();
		leftTopLayout.setHeight(null);
		searchField.setWidth("100%");
		table.setSizeFull();
		rightLayout.setMargin(true);
		rightLayout.setVisible(false);
	}
	
	
	/**
	 * Initialize fields component
	 */
	private void initFields() {
		for (String s : getEditableIds()) {
			TextField field = new TextField(s);
			rightLayout.addComponent(field);
			field.setWidth("100%");
			editorFields.bind(field, s);
		}
	}
	
	/**
	 * Sets property of components
	 */
	private void initProperty() {
		table.setContainerDataSource(sc);
		searchField.setInputPrompt("Search");
		searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);
		editorFields.setBuffered(true);
		table.setVisibleColumns((Object[])getSearchableIds());
		table.setEditable(false);		
		table.setSelectable(true);
		table.setImmediate(true);
		table.addValueChangeListener(this);
	}
	
	public AbstractSingleTableManagerView() {

		initLayout();
		initFields();
		initProperty();
		
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
		rightLayout.setVisible(false);
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
		rightLayout.setVisible(true);
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
			searchAction();
		else if (source==cancelSearchButton)
			cancelSearchAction();
	}

	/**
	 * Cancel searchField value and remove any filter
	 */
	private void cancelSearchAction() {
		searchField.setValue("");
		search(null);
		
	}
		
	/**
	 *  Delete selected item from the table.
	 */
	private void delete() {
		rightLayout.setVisible(false);	
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
		rightLayout.setVisible(itemId != null);		
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
	 * Read value from the SearchField and execute a search
	 */
	private void searchAction() {
		
		String searchTerm = (String) searchField.getValue();
		search(searchTerm);
		
	}
	
	/**
	 * Apply filter to the sql container
	 * @param searchTerm String filter to add, if null cancel all filter
	 */
	private void search(String searchTerm) {
		sc.removeAllContainerFilters();
		if (searchTerm == null || searchTerm.equals("")) {
			return;
		}
			
		
		if (getSearchableIds().length == 0)
			return;
		
		Filter [] filters = new Filter[getSearchableIds().length];
				
		for ( int i = 0; i < getSearchableIds().length; i++) {
			filters[i] = new SimpleStringFilter(getSearchableIds()[i],searchTerm,true,false);
		}
		
		sc.addContainerFilter(new Or(filters));
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
			
			getUI().getNavigator().navigateTo(getBackViewName());


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
	
}