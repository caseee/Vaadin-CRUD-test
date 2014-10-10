package vaadinapp.view;

import java.util.ArrayList;
import java.util.List;

import vaadinapp.Controller;
import vaadinapp.data.LinkedProperty;
import vaadinapp.data.PropertyIdBehavior;
import vaadinapp.data.PropertyIdSearch;
import vaadinapp.data.PropertyIdVisibility;
import vaadinapp.data.ViewPropertyId;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.VaadinSession;


/**
 * Extends AbstractSingleTableManagerView to show, search, edit, add and delete articles.
 * @author Marco Casella
 *
 */
public class ArticlesView extends AbstractSingleTableManagerView {

	public static String NAME = "articles";
	private List<ViewPropertyId> ListOfViewPropertyId;
	private static final long serialVersionUID = -2762624119626051272L;
		
	
	/* (non-Javadoc)
	 * @see vaadinapp.view.AbstractSingleTableManagerView#initChild()
	 */
	public boolean initChild() {
				
		ListOfViewPropertyId = new ArrayList<ViewPropertyId> ();
		
		LinkedProperty categoryLink = new LinkedProperty("CATEGORY", 	VaadinSession.getCurrent().getAttribute(Controller.class).getCategoriesContainer(), 
				"CATEGORY", "ID", Integer.class, String.class);
		LinkedProperty colorsLink = new LinkedProperty("COLOR", 	VaadinSession.getCurrent().getAttribute(Controller.class).getColorsContainer(), 
				"NAME", "ID", Integer.class, String.class);
		LinkedProperty sizeLink = new LinkedProperty("SIZE", 	VaadinSession.getCurrent().getAttribute(Controller.class).getSizesContainer(), 
				"NAME", "ID", Integer.class, String.class);

		ListOfViewPropertyId.add(new ViewPropertyId("ID",			PropertyIdVisibility.Hidden, 		PropertyIdBehavior.ReadOnly, 	PropertyIdSearch.NotSearchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("CATEGORY",		PropertyIdVisibility.Always, 		PropertyIdBehavior.Editable, 	PropertyIdSearch.NotSearchable,	categoryLink));
		ListOfViewPropertyId.add(new ViewPropertyId("NAME",			PropertyIdVisibility.Always, 		PropertyIdBehavior.Editable, 	PropertyIdSearch.Searchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("DESCRIPTION",	PropertyIdVisibility.OnlyInDetail, 	PropertyIdBehavior.Editable, 	PropertyIdSearch.Searchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("COLOR",		PropertyIdVisibility.OnlyInDetail, 	PropertyIdBehavior.Editable, 	PropertyIdSearch.Searchable,	colorsLink));
		ListOfViewPropertyId.add(new ViewPropertyId("SIZE",			PropertyIdVisibility.OnlyInDetail, 	PropertyIdBehavior.Editable, 	PropertyIdSearch.Searchable,	sizeLink));
		ListOfViewPropertyId.add(new ViewPropertyId("PRICE",		PropertyIdVisibility.Always, 		PropertyIdBehavior.Editable, 	PropertyIdSearch.NotSearchable,	null));
		//ListOfViewPropertyId.add(new ViewPropertyId("VERSIONID",		PropertyIdVisibility.Hidden, 		PropertyIdBehavior.ReadOnly, 	PropertyIdSearch.NotSearchable,	null));
				
		return true;
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

	/* (non-Javadoc)
	 * @see vaadinapp.view.AbstractSingleTableManagerView#getViewPropertyId()
	 */
	@Override
	public List<ViewPropertyId> getViewPropertyId() {
		return ListOfViewPropertyId;
	}


	/* (non-Javadoc)
	 * @see vaadinapp.view.AbstractSingleTableManagerView#getMinimunUserLevel()
	 */
	@Override
	protected int getMinimunUserLevel() {
		return 0;
	}
		
}


