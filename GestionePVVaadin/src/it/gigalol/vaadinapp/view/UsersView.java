package it.gigalol.vaadinapp.view;

import it.gigalol.vaadinapp.Controller;
import it.gigalol.vaadinapp.data.PropertyIdBehavior;
import it.gigalol.vaadinapp.data.PropertyIdSearch;
import it.gigalol.vaadinapp.data.PropertyIdVisibility;
import it.gigalol.vaadinapp.data.ViewPropertyId;
import it.gigalol.vaadinapp.sql.LinkedTable;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.VaadinSession;

public class UsersView extends AbstractSingleTableManagerView {

	
	public static String NAME = "users";
	private List<ViewPropertyId> ListOfViewPropertyId;
	private static final long serialVersionUID = -2761124119116051111L;
	
	
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
		return VaadinSession.getCurrent().getAttribute(Controller.class).getUsersContainer();
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

		LinkedTable siteLink = new LinkedTable("SITE", 	VaadinSession.getCurrent().getAttribute(Controller.class).getCategoriesContainer(), 
				"NAME", "ID", Integer.class, String.class);
		
		ListOfViewPropertyId.add(new ViewPropertyId("ID",	PropertyIdVisibility.Hidden, PropertyIdBehavior.ReadOnly, 	PropertyIdSearch.NotSearchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("USERNAME",	PropertyIdVisibility.Always, PropertyIdBehavior.Editable, 	PropertyIdSearch.Searchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("PASS",	PropertyIdVisibility.OnlyInDetail, PropertyIdBehavior.Editable, 	PropertyIdSearch.NotSearchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("LEVEL",	PropertyIdVisibility.Always, PropertyIdBehavior.Editable, 	PropertyIdSearch.NotSearchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("SITE",	PropertyIdVisibility.OnlyInDetail, PropertyIdBehavior.Editable, 	PropertyIdSearch.NotSearchable,	siteLink));

		return true;
	}

	@Override
	protected int getMinimunUserLevel() {
		return 99;
	}

}
