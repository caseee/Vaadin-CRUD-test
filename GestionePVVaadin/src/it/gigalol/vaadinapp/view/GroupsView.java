package it.gigalol.vaadinapp.view;

import it.gigalol.vaadinapp.Controller;
import it.gigalol.vaadinapp.data.LinkedProperty;
import it.gigalol.vaadinapp.data.PropertyIdBehavior;
import it.gigalol.vaadinapp.data.PropertyIdSearch;
import it.gigalol.vaadinapp.data.PropertyIdVisibility;
import it.gigalol.vaadinapp.data.ViewPropertyId;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.VaadinSession;

/**
 * @author Marco Casella
 *
 */
public class GroupsView extends AbstractSingleTableManagerView {

	public static String NAME = "groups";
	private List<ViewPropertyId> ListOfViewPropertyId;
	private static final long serialVersionUID = -2761124119626051272L;
			
	
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
		return VaadinSession.getCurrent().getAttribute(Controller.class).getCategoriesContainer();
	}

	@Override
	public List<ViewPropertyId> getViewPropertyId() {
		return ListOfViewPropertyId;
	}


	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#build()
	 */
	@Override
	protected boolean initChild() {
		ListOfViewPropertyId = new ArrayList<ViewPropertyId> ();
		LinkedProperty categoryLink = new LinkedProperty("FATHER_ID", 	VaadinSession.getCurrent().getAttribute(Controller.class).getCategoriesContainer(), 
				"CATEGORY", "ID", Integer.class, String.class);

		ListOfViewPropertyId.add(new ViewPropertyId("ID",			PropertyIdVisibility.Hidden, PropertyIdBehavior.ReadOnly, 	PropertyIdSearch.NotSearchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("FATHER_ID",	PropertyIdVisibility.Always, PropertyIdBehavior.Editable, 	PropertyIdSearch.NotSearchable,	categoryLink));
		ListOfViewPropertyId.add(new ViewPropertyId("CATEGORY",		PropertyIdVisibility.Always, PropertyIdBehavior.Editable, 	PropertyIdSearch.Searchable,	null));
		return true;
	}


	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getMinimunUserLevel()
	 */
	@Override
	protected int getMinimunUserLevel() {
		// TODO Auto-generated method stub
		return 1;
	}

}
