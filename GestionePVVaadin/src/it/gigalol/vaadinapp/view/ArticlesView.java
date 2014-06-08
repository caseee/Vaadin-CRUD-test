package it.gigalol.vaadinapp.view;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.VaadinSession;

import it.gigalol.vaadinapp.Controller;
import it.gigalol.vaadinapp.data.PropertyIdBehavior;
import it.gigalol.vaadinapp.data.PropertyIdSearch;
import it.gigalol.vaadinapp.data.PropertyIdVisibility;
import it.gigalol.vaadinapp.data.ViewPropertyId;
import it.gigalol.vaadinapp.sql.LinkedTable;


/**
 * Extends AbstractSingleTableManagerView to show, search, edit, add and delete articles.
 * @author Marco Casella
 *
 */
public class ArticlesView extends AbstractSingleTableManagerView {

	public static String NAME = "articles";
	private List<ViewPropertyId> ListOfViewPropertyId;
	private static final long serialVersionUID = -2762624119626051272L;
		
	
	public ArticlesView() {
				
		ListOfViewPropertyId = new ArrayList<ViewPropertyId> ();
		LinkedTable categoryLink = new LinkedTable("CATEGORY", 	VaadinSession.getCurrent().getAttribute(Controller.class).getCategoriesContainer(), 
				"CATEGORY", "ID", Integer.class, String.class);

		ListOfViewPropertyId.add(new ViewPropertyId("ID",			PropertyIdVisibility.Hidden, PropertyIdBehavior.ReadOnly, 	PropertyIdSearch.NotSearchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("CATEGORY",		PropertyIdVisibility.Always, PropertyIdBehavior.Editable, 	PropertyIdSearch.NotSearchable,	categoryLink));
		ListOfViewPropertyId.add(new ViewPropertyId("NAME",			PropertyIdVisibility.Always, PropertyIdBehavior.Editable, 	PropertyIdSearch.Searchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("DESCRIPTION",	PropertyIdVisibility.OnlyInDetail, PropertyIdBehavior.Editable, 	PropertyIdSearch.Searchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("PRICE",		PropertyIdVisibility.Always, PropertyIdBehavior.Editable, 	PropertyIdSearch.NotSearchable,	null));
				
		super.build();
		
	}
	
	
	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getBackViewName()
	 */
	@Override
	public String getBackViewName() {
		return MainView.NAME;
	}
	
	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getViewName()
	 */
	@Override
	public String getViewName() {
		return NAME ;
	}
	

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getSQLContainer()
	 */
	@Override
	public SQLContainer getSQLContainer() {
		return VaadinSession.getCurrent().getAttribute(Controller.class).getArticlesContainer();
	}

	@Override
	public List<ViewPropertyId> getViewPropertyId() {
		return ListOfViewPropertyId;
	}
		
}


