/**
 * MovimentationsView.java
 */
package vaadinapp.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import vaadinapp.Controller;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeEvent;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author Marco Casella
 *
 */
public class MovimentationsView extends CustomComponent implements Serializable, View, ClickListener {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout mainLayout;

	@AutoGenerated
	private Table table;

	@AutoGenerated
	private GridLayout gridLayout_2;

	@AutoGenerated
	private Button changeQuantityBtn;

	@AutoGenerated
	private Button removeBTN;

	@AutoGenerated
	private Button addArticleBTN;

	@AutoGenerated
	private TextField totalTextBox;

	@AutoGenerated
	private ComboBox destinationCB;

	@AutoGenerated
	private TextField fieldID;

	@AutoGenerated
	private ComboBox fieldTYPE;

	@AutoGenerated
	private ComboBox fieldSITE;

	@AutoGenerated
	private PopupDateField fieldOPDATE;

	@AutoGenerated
	private Button backBTN;

	@AutoGenerated
	private HorizontalLayout horizontalLayout_1;

	@AutoGenerated
	private Button saveButton;

	private static final long serialVersionUID = -1062391299907169028L;
	// nome view
	public static final String NAME = "Movimentations";
	// controller di sessione
	private Controller controller = VaadinSession.getCurrent().getAttribute(Controller.class);
	// SQLContainer con i dati
	private SQLContainer head = controller.getMovimentationsContainer();
	private SQLContainer row = controller.getMovimentation_SpecsContainer();
	private SQLContainer type = controller.getMovimentation_TypesContainer();
	private SQLContainer art = controller.getArticlesContainer();
	private SQLContainer dest = controller.getRegistryContainer();
	private SQLContainer site = controller.getSitesContainer();
	// textfield per groupbox
	private TextField hiddenTypeField = new TextField();
	private TextField hiddenSiteField = new TextField();
	private TextField hiddenDestinationField = new TextField();	
	// finestra per la selezione articoli
	private ArticleSelectWindow artWin;
	// id movimentazione e id tipo collegati
	private Integer headID = null;
	private Integer headTypeId = null;
	private Integer headSiteId = null;
	// fieldgroup della testa movimentazione
	private final FieldGroup editorFields = new FieldGroup();
	// finestra per il cambio quantit�
	private final QuantityChangeWindow quantityWindows = new QuantityChangeWindow();
	// indica se la movimentazione � nuova o si sta modificando 
	private boolean moviementazioneNuova = false;
	

	public MovimentationsView() {

		buildMainLayout();
		setCompositionRoot(mainLayout);

		// user code here
		fieldID.setReadOnly(true);
		
		fieldOPDATE.setValue(new Date());

		destinationCB.setNullSelectionAllowed(true);
		destinationCB.setNewItemsAllowed(true);
		destinationCB.setContainerDataSource(dest);
		destinationCB.setItemCaptionPropertyId("NAME");

		fieldSITE.setNullSelectionAllowed(false);
		fieldSITE.setNewItemsAllowed(false);
		fieldSITE.setContainerDataSource(site);
		fieldSITE.setItemCaptionPropertyId("NAME");

		fieldTYPE.setNullSelectionAllowed(false);
		fieldTYPE.setNewItemsAllowed(false);
		fieldTYPE.setContainerDataSource(type);
		fieldTYPE.setItemCaptionPropertyId("DESCRIPTION");
				
		saveButton.addClickListener(this);
		backBTN.addClickListener(this);		
		addArticleBTN.addClickListener(this);		
		removeBTN.addClickListener(this);		
		changeQuantityBtn.addClickListener(this);
		
		table.setImmediate(true);
		table.setContainerDataSource(row);
		table.setSelectable(true);
				
		artWin = new ArticleSelectWindow();
		artWin.setWidth(400, Unit.PIXELS);
		artWin.setHeight(400, Unit.PIXELS);

		quantityWindows.setWidth(300, Unit.PIXELS );
		quantityWindows.setHeight(200, Unit.PIXELS );

		editorFields.bind(fieldID, "ID");
		editorFields.bind(hiddenSiteField, "SITE");
		editorFields.bind(totalTextBox, "TOTAL");		
		editorFields.bind(fieldOPDATE, "OPDATE");
		editorFields.bind(hiddenDestinationField, "DESTINATION");
		editorFields.bind(hiddenTypeField, "MOVIMENTATION_TYPE");
					

		totalTextBox.setLocale(Locale.US);				
		
		
	}
	

	

	/**
	 *  Evaluate total and commit to the db
	 */
	@SuppressWarnings("unchecked")
	synchronized private void commit() {

		BigDecimal total = new BigDecimal(0);
		for ( Iterator<?> itemIdIteraor =  row.getItemIds().iterator(); itemIdIteraor.hasNext(); ) {
			Object itemId = itemIdIteraor.next();
			Item rowItem = row.getItem(itemId);
			Property <Integer> quantity = rowItem.getItemProperty("QUANTITY");
			BigDecimal dec_quantity = new BigDecimal( quantity.getValue());
			Property <BigDecimal> propPrice = rowItem.getItemProperty("PRICE");
			BigDecimal price = propPrice.getValue();
			Property <BigDecimal> propDisconunt = rowItem.getItemProperty("DISCOUNT");
			BigDecimal discount = propDisconunt.getValue();
			Property <BigDecimal> row_total = rowItem.getItemProperty("TOTAL");					
			BigDecimal no_disc = dec_quantity.multiply(price);
			BigDecimal with_disc = no_disc.subtract(discount);
			row_total.setValue(with_disc);
			total = total.add(row_total.getValue());
		}

		
		//FIXME WRONG LOCALE
		totalTextBox.setValue(total.toString());
		totalTextBox.commit();
		
		try {			
			row.commit();
			editorFields.commit();
			head.commit();
			
			RowId headRowId = new RowId(new Integer (headID));
			Item it = head.getItem(headRowId);
			editorFields.setItemDataSource(it);			
			
		} catch (CommitException e) {
			Notification.show("Error", "Error saving. Commit Exception. ", Notification.Type.ERROR_MESSAGE);			
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			Notification.show("Error", "Error saving. Unsupported Operation Exception. ", Notification.Type.ERROR_MESSAGE);	
			e.printStackTrace();
		} catch (SQLException e) {
			Notification.show("Error", "Error saving. SQL Exception. ", Notification.Type.ERROR_MESSAGE);	
			e.printStackTrace();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {

		String parameters = event.getParameters();
		RowId headRowId = null;
				
		// controlla se � un edit di una movimentazione o una movimentazione nuova
		if(parameters != null){
						
			// split at "/", add each part as a label
			String[] msgs = event.getParameters().split("/");
			
			// if number of param is incorrect message and exit
			if (msgs.length < 1 || msgs.length > 3) {
				Notification.show("Error", "Parameter error. ", Notification.Type.ERROR_MESSAGE);
				getUI().getNavigator().navigateTo(MovimentationsListView.NAME);
			}
			
			try { 
				headTypeId = Integer.parseInt(msgs[0]);
				headSiteId = Integer.parseInt(msgs[1]);

				if (msgs.length == 3) {
					headID=Integer.parseInt(msgs[2]);
					headRowId = new RowId(headID);
				}
				
				
			} catch (Exception ex) {
				ex.printStackTrace();
				Notification.show("Error", "Parameter error. ", Notification.Type.ERROR_MESSAGE);
				getUI().getNavigator().navigateTo(MovimentationsListView.NAME);
					
			}

		} 

		if (headID==null)
			moviementazioneNuova = true;

		if (moviementazioneNuova)
			createNewHead();
		else
			loadHead(headRowId);

	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		Button source = event.getButton();

		if (source == backBTN)
			getUI().getNavigator().navigateTo(MovimentationsListView.NAME);
		else if (source == addArticleBTN)
			showAddRowWindow();
		else if (source == removeBTN)
			removeSelectedRow();
		else if (source == changeQuantityBtn)
			doChangeQuantity();

	}

	@SuppressWarnings("unchecked")
	private synchronized void add (Item artItem) {

		Property <?> artID =  artItem.  getItemProperty("ID");
		Property <?> artPrice = artItem.getItemProperty("PRICE");
	

		Object tempItemId = row.addItem();

		if (tempItemId==null)
			return;

		Item itt = row.getItemUnfiltered(tempItemId);

		RowId siteRowid = (RowId) fieldSITE.getValue();
		RowId typeRowid = (RowId) fieldTYPE.getValue();
		
		itt.getItemProperty("ID_ART").setValue(artID.getValue());		
		itt.getItemProperty("SITE").setValue(siteRowid.getId()[0]);
		itt.getItemProperty("MOVIMENTATION_TYPE").setValue(typeRowid.getId()[0]);		
		itt.getItemProperty("ID_HEAD").setValue(new Integer(headID));
		itt.getItemProperty("QUANTITY").setValue(new Integer(1));
		itt.getItemProperty("PRICE").setValue(artPrice.getValue());
		itt.getItemProperty("DISCOUNT").setValue(new BigDecimal(0));
		itt.getItemProperty("TOTAL").setValue(artPrice.getValue());
		
		commit();
	}

	private synchronized void doChangeQuantity() {
		Object artiid = table.getValue();

		if ( artiid == null)
			return;

		quantityWindows.itemId = artiid;

		if (!quantityWindows.isAttached()) {
			getUI();
			UI.getCurrent().addWindow(quantityWindows);
		}


	}

	private synchronized void removeSelectedRow() {
		Object artiid = table.getValue();

		if ( artiid == null)
			return;

		table.removeItem(artiid);
		
		commit();

	}

	private void showAddRowWindow() {

		if (!artWin.isAttached()) {
			getUI();
			UI.getCurrent().addWindow(artWin);
		}

	}

	public class ArticleSelectWindow extends Window implements ClickListener   {

		private static final long serialVersionUID = -3604196350264435567L;

		Table artTable = new Table();
		Button searchBtn = new Button("Search");
		Button addBtn = new Button("Add");
		TextField search = new TextField();

		public ArticleSelectWindow() {
			super("Article"); 
			center();
			artTable.setContainerDataSource(art);
			artTable.setVisibleColumns("NAME", "PRICE");
			VerticalLayout content = new VerticalLayout();
			HorizontalLayout horiz = new HorizontalLayout();
			content.setSizeFull();			
			search.setWidth(100, Unit.PERCENTAGE);
			addBtn.setWidth(100, Unit.PERCENTAGE);
			addBtn.addClickListener(this);
			searchBtn.addClickListener(this);
			searchBtn.setWidth(100, Unit.PERCENTAGE);
			search.setWidth(100, Unit.PERCENTAGE);
			content.addComponents(horiz, artTable);
			horiz.addComponents(addBtn, search,searchBtn);
			horiz.setExpandRatio(addBtn, 100f);
			horiz.setWidth(100, Unit.PERCENTAGE);
			horiz.setExpandRatio(search, 100f);
			horiz.setExpandRatio(searchBtn, 100f);
			artTable.setSizeFull();
			content.setExpandRatio(artTable, 100f);
			artTable.setSelectable(true);
			content.setExpandRatio(artTable, 100f);
			setContent(content);
			searchBtn.addClickListener(this);
		}

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {
			Button source = event.getButton();

			if (source==addBtn) 
				add();
			if (source==searchBtn)
				search();


		}

		/**
		 * 
		 */
		private void search() {
			art.removeAllContainerFilters();
			art.addContainerFilter(new SimpleStringFilter("NAME",search.getValue(),true,false));
		}

		@SuppressWarnings("unchecked")
		private void add() {

			Object artiid = artTable.getValue();

			if ( artiid == null)
				return;

			MovimentationsView.this.add(artTable.getItem(artiid));
			
		}

	}

	/**
	 * Create a new movimentation head and commit it to the database
	 */
	private void createNewHead() {
						
		// this listener will be called when the new row will be saved to the database
		// only after we can read the real row id generated
		head.addRowIdChangeListener(new RowIdChangeListener(){

			private static final long serialVersionUID = 1128167464731496937L;

			@Override
			public void rowIdChange(RowIdChangeEvent event) {
				
				RowId ri = event.getNewRowId();
				Integer in = (Integer) ri.getId()[0];
				headID=new Integer(in);
				loadHead(ri);

			}

		});	

		// add a item to the head SQLContainter		
		Object tempItemId = head.addItem();
		
		// bind the editor field to the new row
		editorFields.setItemDataSource(head.getItem(tempItemId));

		fieldOPDATE.setValue(new Date());

		// set type value
		RowId movTypeRowId = new RowId(new Integer (headTypeId));
		fieldTYPE.select(movTypeRowId);
		hiddenTypeField.setValue(headTypeId.toString());
		
		// set destination
		// hiddenDestinationField.setValue(new Integer (headTypeId).toString());
		
		// set site value
		RowId movSiteRowId = new RowId(new Integer(headSiteId));
		fieldSITE.select(movSiteRowId);
		hiddenSiteField.setValue(new Integer (headSiteId).toString());
		
		totalTextBox.setValue("0");
		
		try {
			editorFields.commit();
			head.commit();
		} catch (UnsupportedOperationException e) {
			Notification.show("Error", "Error creating new item. Unsupported Operation Exception. ", Notification.Type.ERROR_MESSAGE);	
			e.printStackTrace();
			getUI().getNavigator().navigateTo(MovimentationsListView.NAME);
		} catch (SQLException e) {
			Notification.show("Error", "Error creating new item. SQL Exception. ", Notification.Type.ERROR_MESSAGE);		
			e.printStackTrace();
			getUI().getNavigator().navigateTo(MovimentationsListView.NAME);
		} catch (CommitException e) {
			Notification.show("Error", "Error creating new item. Notification Exception. ", Notification.Type.ERROR_MESSAGE);	
			e.printStackTrace();
			getUI().getNavigator().navigateTo(MovimentationsListView.NAME);
		}

		
	}

	/**
	 * Load a head row and all its rows
	 * @param headRowId id of the head row
	 */
	private void loadHead(RowId headRowId){
	
		//TODO Check if user is allowed to see and edit this mov
		
		try {
			head.rollback();
			row.rollback();
		} catch (UnsupportedOperationException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		editorFields.discard();
		
		Item it = head.getItem(headRowId);

		if (it == null) {
			Notification.show("Error", "Error loading.",  Notification.Type.WARNING_MESSAGE);
		}
		
		editorFields.setItemDataSource(it);
		row.removeAllContainerFilters();
		row.addContainerFilter(new Compare.Equal("ID_HEAD",headID));
		
		destinationCB.addValueChangeListener(new ComboboxChangeListener(hiddenDestinationField));
		
		// Set the SITE combobox
		Property <?> siteProperty = it.getItemProperty("SITE");
		RowId headSiteRowId = new RowId(siteProperty.getValue());
		fieldSITE.select(headSiteRowId);
		fieldSITE.setReadOnly(true);
		
		// Set the TYPE combobox
		Property <?> typeProperty = it.getItemProperty("MOVIMENTATION_TYPE");
		RowId headTypeRowId = new RowId(typeProperty.getValue());
		fieldTYPE.select(headTypeRowId);
		fieldTYPE.setReadOnly(true);		
		
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
			field.commit();
			
			commit();
		
		}

	}

	public class QuantityChangeWindow extends Window implements ClickListener {

		private Button okBtn = new Button("OK");
		private TextField quantityTextBox = new TextField();
		public Object itemId = null;

		public QuantityChangeWindow() {
			super("Insert Quantity:"); 
			center();
			VerticalLayout content = new VerticalLayout(quantityTextBox,okBtn);
			okBtn.setWidth(100, Unit.PERCENTAGE);
			quantityTextBox.setWidth(100, Unit.PERCENTAGE);
			content.setExpandRatio(okBtn, 100);
			content.setExpandRatio(quantityTextBox, 100);
			setContent(content);
			okBtn.addClickListener(this);
			setModal(true);

		}

		private static final long serialVersionUID = 1143182941221234597L;

		/* (non-Javadoc)
		 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
		 */
		@Override
		public void buttonClick(ClickEvent event) {

			if (itemId==null)
				return;

			Item item = table.getItem(itemId);

			if (item==null)
				return;

			String qtext = quantityTextBox.getValue();

			//TODO Avoid exception
			try {
				Integer qint = new Integer(Integer.parseInt(qtext));

				@SuppressWarnings("unchecked")
				Property <Integer> qprop = item.getItemProperty("QUANTITY");

				qprop.setValue(qint);

				commit();

				this.close();
			} catch (NumberFormatException ex) {

			}


		}

	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// horizontalLayout_1
		horizontalLayout_1 = buildHorizontalLayout_1();
		mainLayout.addComponent(horizontalLayout_1);
		
		// gridLayout_2
		gridLayout_2 = buildGridLayout_2();
		mainLayout.addComponent(gridLayout_2);
		
		// table
		table = new Table();
		table.setImmediate(false);
		table.setWidth("100.0%");
		table.setHeight("100.0%");
		mainLayout.addComponent(table);
		mainLayout.setExpandRatio(table, 100.0f);
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_1() {
		// common part: create layout
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setWidth("-1px");
		horizontalLayout_1.setHeight("-1px");
		horizontalLayout_1.setMargin(false);
		
		// saveButton
		saveButton = new Button();
		saveButton.setCaption("Save");
		saveButton.setEnabled(false);
		saveButton.setImmediate(true);
		saveButton.setVisible(false);
		saveButton.setWidth("100.0%");
		saveButton.setHeight("-1px");
		horizontalLayout_1.addComponent(saveButton);
		horizontalLayout_1.setComponentAlignment(saveButton, new Alignment(6));
		
		return horizontalLayout_1;
	}

	@AutoGenerated
	private GridLayout buildGridLayout_2() {
		// common part: create layout
		gridLayout_2 = new GridLayout();
		gridLayout_2.setImmediate(false);
		gridLayout_2.setWidth("100.0%");
		gridLayout_2.setHeight("130px");
		gridLayout_2.setMargin(false);
		gridLayout_2.setColumns(4);
		gridLayout_2.setRows(4);
		
		// backBTN
		backBTN = new Button();
		backBTN.setCaption("Back");
		backBTN.setImmediate(true);
		backBTN.setWidth("100.0%");
		backBTN.setHeight("-1px");
		gridLayout_2.addComponent(backBTN, 0, 0);
		gridLayout_2.setComponentAlignment(backBTN, new Alignment(48));
		
		// fieldOPDATE
		fieldOPDATE = new PopupDateField();
		fieldOPDATE.setCaption("Date:");
		fieldOPDATE.setImmediate(false);
		fieldOPDATE.setWidth("100.0%");
		fieldOPDATE.setHeight("23px");
		gridLayout_2.addComponent(fieldOPDATE, 1, 0);
		
		// fieldSITE
		fieldSITE = new ComboBox();
		fieldSITE.setCaption("Site:");
		fieldSITE.setImmediate(false);
		fieldSITE.setWidth("100.0%");
		fieldSITE.setHeight("-1px");
		gridLayout_2.addComponent(fieldSITE, 2, 0);
		
		// fieldTYPE
		fieldTYPE = new ComboBox();
		fieldTYPE.setCaption("Type:");
		fieldTYPE.setImmediate(false);
		fieldTYPE.setWidth("100.0%");
		fieldTYPE.setHeight("-1px");
		gridLayout_2.addComponent(fieldTYPE, 3, 0);
		
		// fieldID
		fieldID = new TextField();
		fieldID.setEnabled(false);
		fieldID.setImmediate(false);
		fieldID.setVisible(false);
		fieldID.setWidth("-1px");
		fieldID.setHeight("-1px");
		gridLayout_2.addComponent(fieldID, 0, 1);
		
		// destinationCB
		destinationCB = new ComboBox();
		destinationCB.setCaption("Destination:");
		destinationCB.setImmediate(false);
		destinationCB.setWidth("100.0%");
		destinationCB.setHeight("-1px");
		gridLayout_2.addComponent(destinationCB, 2, 1);
		
		// totalTextBox
		totalTextBox = new TextField();
		totalTextBox.setCaption("Total:");
		totalTextBox.setImmediate(false);
		totalTextBox.setWidth("100.0%");
		totalTextBox.setHeight("-1px");
		gridLayout_2.addComponent(totalTextBox, 3, 1);
		gridLayout_2.setComponentAlignment(totalTextBox, new Alignment(6));
		
		// addArticleBTN
		addArticleBTN = new Button();
		addArticleBTN.setCaption("Add");
		addArticleBTN.setImmediate(true);
		addArticleBTN.setWidth("100.0%");
		addArticleBTN.setHeight("-1px");
		gridLayout_2.addComponent(addArticleBTN, 0, 3);
		
		// removeBTN
		removeBTN = new Button();
		removeBTN.setCaption("Remove");
		removeBTN.setImmediate(true);
		removeBTN.setWidth("100.0%");
		removeBTN.setHeight("-1px");
		gridLayout_2.addComponent(removeBTN, 1, 3);
		
		// changeQuantityBtn
		changeQuantityBtn = new Button();
		changeQuantityBtn.setCaption("Change Quantity");
		changeQuantityBtn.setImmediate(true);
		changeQuantityBtn.setWidth("100.0%");
		changeQuantityBtn.setHeight("-1px");
		gridLayout_2.addComponent(changeQuantityBtn, 2, 3);
		
		return gridLayout_2;
	}

}


