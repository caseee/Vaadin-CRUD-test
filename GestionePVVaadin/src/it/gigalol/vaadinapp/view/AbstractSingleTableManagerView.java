package it.gigalol.vaadinapp.view;

import it.gigalol.vaadinapp.Controller;
import it.gigalol.vaadinapp.data.LinkedComboBox;
import it.gigalol.vaadinapp.data.LinkedProperty;
import it.gigalol.vaadinapp.data.PropertyIdSearch;
import it.gigalol.vaadinapp.data.PropertyIdVisibility;
import it.gigalol.vaadinapp.data.ViewPropertyId;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import de.steinwedel.messagebox.ButtonId;
import de.steinwedel.messagebox.Icon;
import de.steinwedel.messagebox.MessageBox;
import de.steinwedel.messagebox.MessageBoxListener;

/**
 * Abstract class for a vaadin view  to select, search, edit, add, delete rows of a generic table.
 * @author Marco Casella
 *
 */
public abstract class AbstractSingleTableManagerView extends CustomComponent implements View, Serializable, ClickListener, ValueChangeListener {
	private static final long serialVersionUID = 2869411776027184262L;
	protected final Controller controller = VaadinSession.getCurrent().getAttribute(Controller.class);
	protected boolean allowSelection = false; 
	private final SQLContainer sc = getSQLContainer();
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
	private final HorizontalLayout leftTopLayout = new HorizontalLayout();
	private final VerticalLayout leftLayout = new VerticalLayout();
	private final VerticalLayout rightLayout = new VerticalLayout();
	private final HorizontalSplitPanel rootLayout = new HorizontalSplitPanel();
	private final List<LinkedComboBox> linkedComboBoxes = new Vector<LinkedComboBox>();
	private Object lastId ;

	/**
	 * Get the SQLContainer of the table
	 * @return SQLContainer of the table
	 */
	protected abstract SQLContainer getSQLContainer();


	/**
	 * Initialize child class
	 * @return true if success
	 */
	protected abstract boolean initChild();
	
	/**
	 * Get the name of the previous view
	 * @return the name of the previous view
	 */
	protected abstract String getBackViewName();

	/**
	 * Get the name of the view
	 * @return the name of the view
	 */
	protected abstract String getViewName();
	
	/**
	 * Get the minimum user level to use the view
	 * @return minimum user level
	 */
	protected abstract int getMinimunUserLevel();

	/**
	 * Get the ids of the columns where the search is performed
	 * @return the ids
	 */
	protected abstract List < ViewPropertyId> getViewPropertyId();

	/**
	 * Sets the layouts of components
	 */
	private void initLayout() {
		this.setSizeFull();
		this.setCompositionRoot(rootLayout);
		leftLayout.setHeight("" );
		rootLayout.setSplitPosition(50f);
		rootLayout.setSizeFull();
		leftTopLayout.addComponents(back,newItem,searchField,searchButton,cancelSearchButton,deleteItem,saveItem,discardItem);
		leftLayout.addComponents(leftTopLayout,rightLayout);
		rootLayout.addComponents(leftLayout,table);
	
		leftTopLayout.setWidth(100,Unit.PERCENTAGE);

		searchField.setWidth("100%");
		table.setSizeFull();
		rightLayout.setMargin(true);
		rightLayout.setVisible(false);
	}

	/**
	 * Initialize fields component
	 */
	private void initFields() {
		
		for (ViewPropertyId vpi : getViewPropertyId()) {

			// For every external table
			if (vpi.getLinked() != null) {
				final LinkedProperty lt = vpi.getLinked();
				// Add references to the external container
				sc.addReference(lt.getSqlContainer(),lt.getIdName() , lt.getExternalIdName());
				// Add a custom column generator 
				table.addGeneratedColumn(lt.getIdName(), new ColumnGenerator() {
					private static final long serialVersionUID = -5277036849741964362L;
					public Component generateCell(Table source, Object itemId, Object columnId) {
						if (sc.getItem(itemId).getItemProperty(lt.getIdName()).getValue() == null) 
							return null;
						// Convert internal id property to external show property 
						Label l = new Label();
						// Retrieve the item in the external table
						Item item = sc.getReferencedItem(itemId, lt.getSqlContainer());
						// Get the property used to show item
						Property<?> property = item.getItemProperty(lt.getShowName()); 
						l.setValue(property.getValue().toString());
						l.setSizeUndefined();
						return l;
					}
				});

			}


			// For each Id marked as editable add a elements in the editorFields
			if ( vpi.getVisibility() != PropertyIdVisibility.Hidden) {

				// If the field is local add the textbox field
				if (vpi.getLinked() == null) {
					TextField field = new TextField(vpi.getName());
					rightLayout.addComponent(field);
					field.setWidth("100%");
					editorFields.bind(field, vpi.getName());
				} 
				else  {
					// If the field is in a linked table
					// add a combobox with external table data
					// show in the combobox the showId
					// create a hidden textfield update with the id
					// when the combobox selected item is changed
					// the combobox is added to the view
					// the textfield is bind to the data source
					LinkedProperty ltf=vpi.getLinked();
					HorizontalLayout hl = new HorizontalLayout();
					hl.setSizeFull();
					final ComboBox cbfield = new ComboBox(vpi.getName());
					final LinkedComboBox lcb = new LinkedComboBox(cbfield,ltf );
					cbfield.setWidth("100%");
					cbfield.setNullSelectionAllowed(false);
					cbfield.setContainerDataSource(ltf.getSqlContainer());
					cbfield.setItemCaptionPropertyId(ltf.getShowName());
					cbfield.setImmediate(true);
					cbfield.setNewItemsAllowed(false);
					TextField field = new TextField(vpi.getName());				
					cbfield.addValueChangeListener(new ComboboxChangeListener(field));					
					hl.addComponent(cbfield);
//					Button btn = new Button("Edit");
//					btn.setWidth("50px");
//					btn.setHeight("100%");
//					hl.addComponent(btn);
					rightLayout.addComponent(hl);
					cbfield.setWidth("100%");
					editorFields.bind(field, vpi.getName());
					linkedComboBoxes.add(lcb);
				}

			}
		
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
		
		List <String> visible = new ArrayList<String>();
		for (ViewPropertyId vpi : getViewPropertyId())
			if (vpi.getVisibility() == PropertyIdVisibility.Always)
				visible.add(vpi.getName());
		table.setVisibleColumns(visible.toArray());
		
		table.setEditable(false);		
		table.setSelectable(true);
		table.setImmediate(true);
		table.addValueChangeListener(this);
	}

	private void authTest() {
		// FIXME
		if (controller.getLoggedUser().getLevel() < getMinimunUserLevel())
			getUI().getNavigator().navigateTo(getBackViewName());
		
	}
	
	public AbstractSingleTableManagerView() {

		initChild();
		//authTest();
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
	private void switchToItemId(Object itemId) {
		if (itemId == lastId)
			return;
		lastId = itemId;
		if (itemId != null) {
			editorFields.setItemDataSource(table.getItem(itemId));

			for (LinkedComboBox lcb : linkedComboBoxes) {
				//String externalIdName = lcb.getLinkedTable().getIdName();
				String internalName = lcb.getLinkedTable().getIdName();
				Item internalItem = sc.getItem(itemId);
				Property<?> internalId = internalItem.getItemProperty(internalName);
				RowId irw = new RowId(internalId.getValue());
				Item externalItem = lcb.getLinkedTable().getSqlContainer().getItem(irw);
				if (externalItem==null)
					continue;
				Property<?> externalId = externalItem.getItemProperty(lcb.getLinkedTable().getExternalIdName());
				RowId erw = new RowId(externalId.getValue());
				lcb.getCombobox().select(erw);

			}

		}
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

		int nsearch=0;
		for (ViewPropertyId vpi : getViewPropertyId())
			if (vpi.getSearchable() == PropertyIdSearch.Searchable)
				nsearch++;

		if (nsearch == 0)
			return;

		Filter [] filters = new Filter[nsearch];

		nsearch=0;
		for (ViewPropertyId vpi : getViewPropertyId())
			if (vpi.getSearchable() == PropertyIdSearch.Searchable)
				filters[nsearch++] = new SimpleStringFilter(vpi.getName(),searchTerm,true,false);
		

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

			switchToItemId(table.getValue());

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
	 

	/**
	 * Class implementing the ValueChangeListener used to change the textfield value with the key
	 * of the selected external row in the combobox
	 */
	private class ComboboxChangeListener implements ValueChangeListener {

		private TextField field;
		private static final long serialVersionUID = 90074393261185094L;

		/**
		 * @param hiddend field binded to the actual data
		 */
		ComboboxChangeListener(TextField field) {
			this.field = field;
		}
		
		/* (non-Javadoc)
		 * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
		 */
		@Override
		public void valueChange(ValueChangeEvent event) {
			field.setValue(event.getProperty().getValue().toString());
			
		}
		
	}
	
	
}