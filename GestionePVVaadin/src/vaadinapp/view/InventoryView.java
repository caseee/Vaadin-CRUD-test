/**
 * Inventory.java
 */
package vaadinapp.view;

import java.io.Serializable;

import vaadinapp.Controller;
import vaadinapp.data.ExternalColumnGenerator;
import vaadinapp.data.UserBean;
import vaadinapp.data.UserLevel;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Marco Casella
 *
 */
public class InventoryView extends CustomComponent implements View, Serializable, ClickListener {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout mainLayout;
	@AutoGenerated
	private Table table;
	@AutoGenerated
	private HorizontalLayout menuLayout;
	@AutoGenerated
	private Button searchButton;
	@AutoGenerated
	private ComboBox siteCB;
	@AutoGenerated
	private Button backButton;
	private static final long serialVersionUID = -1062300231707169028L;
	public static final String NAME = "Inventory";
	private VaadinSession session = VaadinSession.getCurrent();
	private Controller controller = session.getAttribute(Controller.class);
	private SQLContainer art = controller.getArticlesContainer();
	private SQLContainer site = controller.getSitesContainer();	
	private SQLContainer inv = controller.getInventoryContainer();
	
	public InventoryView() {
		buildMainLayout();
		setCompositionRoot(mainLayout);

		// user code here
		if (!Controller.validSession()) {
			System.err.println("Invalid session.");
			return;
		}
		
		UserBean ub = controller.getLoggedUser();
		
		if ( ub.getLevel() <= UserLevel.StoreManager.getValue())
			siteCB.setReadOnly(true);
		
		siteCB.setImmediate(true);
		siteCB.setNullSelectionAllowed(false);
		siteCB.setNewItemsAllowed(false);
		siteCB.setItemCaptionPropertyId("NAME");
		siteCB.setContainerDataSource(site);
				
		RowId defaultSiteRowId = new RowId(new Integer (ub.getSite()));
		Item defaultSite = siteCB.getItem(defaultSiteRowId );
		if (defaultSite == null) {
			siteCB.select(siteCB.getItemIds().iterator().next());
		} else 
			siteCB.select(defaultSiteRowId);
			
		
		table.setContainerDataSource(inv);
		
		table.setColumnAlignment("QUANTIY", Align.RIGHT);
		
		inv.addReference(art, "ARTICLE", "ID");
		table.addGeneratedColumn("ARTICLE NAME", new ExternalColumnGenerator(table, "ARTICLE", inv, "NAME", art));
		
		inv.addReference(site, "SITE", "ID");
		table.addGeneratedColumn("SITE NAME", new ExternalColumnGenerator(table, "SITE", inv, "NAME", site));
		
		table.setVisibleColumns(new Object[] {"SITE NAME","ARTICLE NAME", "QUANTITY"});
		
		searchButton.addClickListener(this);
		backButton.addClickListener(this);
		
		
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	@Override
	public void buttonClick(ClickEvent event) {
		final Button source = event.getButton();

		if (source == searchButton) 
			search();
		else if (source == backButton)
			getUI().getNavigator().navigateTo(MainView.NAME);

	}

	private void search() {
		
		RowId siteItemRowId = (RowId) siteCB.getValue();
		
		Integer siteItemId = (Integer) siteItemRowId.getId()[0];
		
		if (siteItemId==null)
			return;
		
		inv.removeAllContainerFilters();
		
		Filter siteFilter = new Compare.Equal("SITE",siteItemId);
		
		inv.addContainerFilter(siteFilter);
		
	}

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		// nothing to do
		
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
		
		// menuLayout
		menuLayout = buildMenuLayout();
		mainLayout.addComponent(menuLayout);
		
		// table
		table = new Table();
		table.setImmediate(false);
		table.setWidth("100.0%");
		table.setHeight("100.0%");
		mainLayout.addComponent(table);
		mainLayout.setExpandRatio(table, 100.0f);
		mainLayout.setComponentAlignment(table, new Alignment(20));
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildMenuLayout() {
		// common part: create layout
		menuLayout = new HorizontalLayout();
		menuLayout.setImmediate(false);
		menuLayout.setWidth("100.0%");
		menuLayout.setHeight("30px");
		menuLayout.setMargin(false);
		
		// backButton
		backButton = new Button();
		backButton.setCaption("Back");
		backButton.setImmediate(true);
		backButton.setWidth("100.0%");
		backButton.setHeight("-1px");
		menuLayout.addComponent(backButton);
		menuLayout.setExpandRatio(backButton, 100.0f);
		
		// siteCB
		siteCB = new ComboBox();
		siteCB.setImmediate(false);
		siteCB.setWidth("100.0%");
		siteCB.setHeight("-1px");
		menuLayout.addComponent(siteCB);
		menuLayout.setExpandRatio(siteCB, 100.0f);
		
		// searchButton
		searchButton = new Button();
		searchButton.setCaption("Search");
		searchButton.setImmediate(true);
		searchButton.setWidth("100.0%");
		searchButton.setHeight("-1px");
		menuLayout.addComponent(searchButton);
		menuLayout.setExpandRatio(searchButton, 100.0f);
		
		return menuLayout;
	}

}
