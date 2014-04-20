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

public class ArticlesView extends CustomComponent implements View, Serializable, ClickListener,ValueChangeListener {
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

	private boolean canChange(MessageBoxListener mbl,  ButtonId ... btnids  ) {

		if (editorFields.getItemDataSource()==null)
			return true;

		if (! editorFields.isModified()) 
			return true;

		MessageBox.showPlain(Icon.QUESTION, "Changed", "Commit changes?", mbl, btnids);
		return false;

	}

	@Override
	public void enter(ViewChangeEvent event) {


	}

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
		fieldsLayout.setVisible(true);
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
			askForSave();
		else if (source==discardItem)
			discard();
		else if (source==deleteItem)
			askForDelete();
		else if (source==back)
			askForExit();
		else if (source==newItem)
			askForAdd();

	}

	private void askForAdd() {
		MessageBoxListener mbl = new AskBeforeAddListener();
		
		if (!canChange(mbl, ButtonId.YES, ButtonId.NO, ButtonId.CANCEL)) 
			return;
		
		mbl.buttonClicked(ButtonId.IGNORE);
	}
	
	private void askForSave() {
		MessageBoxListener mbl = new AskBeforeSaveListener();
		
		if (!canChange(mbl, ButtonId.YES, ButtonId.NO)) 
			return;
		
		mbl.buttonClicked(ButtonId.IGNORE);
	}
	
	private void askForExit() {
		
		MessageBoxListener mbl = new AskBeforeExitListener();
				
		if (!canChange(mbl, ButtonId.YES, ButtonId.NO, ButtonId.CANCEL)) 
			return;
		
		mbl.buttonClicked(ButtonId.IGNORE);
	}
	
	private void askForDelete() {
		if (table.getValue() == null)
			return;
		
		MessageBoxListener mbl = new AskBeforeDeleteListener();
		
		MessageBox.showPlain(Icon.QUESTION, "Confirm", "Delete?", mbl, ButtonId.YES, ButtonId.NO);
	
	}
	
	private void delete() {
		fieldsLayout.setVisible(false);	
		table.removeItem(table.getValue());
		editorFields.setItemDataSource(null);
		commit();
	}

	@Override
	public void valueChange(ValueChangeEvent event) {

		if (table.getValue() == lastContactId)
			return;

		MessageBoxListener mbl = new AskBeforeChangeListener();
		
		if (!canChange(mbl, ButtonId.YES, ButtonId.NO, ButtonId.CANCEL)) 
			return;

		mbl.buttonClicked(ButtonId.IGNORE);

	}

	private void changeTo(Object contactId) {
		if (contactId == lastContactId)
			return;
		lastContactId = contactId;
		if (contactId != null)
			editorFields.setItemDataSource(table.getItem(contactId));
		fieldsLayout.setVisible(contactId != null);		
	}

	private class AskBeforeChangeListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = -3673592681475973325L;

		@Override
		public void buttonClicked(ButtonId buttonId) {

			if (buttonId.equals(ButtonId.YES))  
				commit();		
			if (buttonId.equals(ButtonId.NO))
				discard();
			if (buttonId.equals(ButtonId.CANCEL))
				table.setValue(lastContactId);

			changeTo(table.getValue());

		}

	}
		
	private class AskBeforeSaveListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = 5959274722049449150L;

		@Override
		public void buttonClicked(ButtonId buttonId) {

			if (buttonId.equals(ButtonId.YES))  
				commit();		

		}

	}
	
	private class AskBeforeExitListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = 5959271122049449150L;

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
	
	private class AskBeforeAddListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = 5959414722049449111L;

		@Override
		public void buttonClicked(ButtonId buttonId) {

			if (buttonId.equals(ButtonId.CANCEL))
				return;	
			if (buttonId.equals(ButtonId.YES))  
				commit();		
			if (buttonId.equals(ButtonId.NO))
				discard();
			
			addContact();


		}

	}

	private class AskBeforeDeleteListener implements MessageBoxListener, Serializable {
		private static final long serialVersionUID = 5959414722049449111L;

		@Override
		public void buttonClicked(ButtonId buttonId) {

			if (buttonId.equals(ButtonId.YES))
				delete();		

		}

	}
	
}


