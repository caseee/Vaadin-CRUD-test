/**
 * SitesView.java
 */
package vaadinapp.view;

import java.util.ArrayList;
import java.util.List;

import vaadinapp.Controller;
import vaadinapp.data.PropertyIdBehavior;
import vaadinapp.data.PropertyIdSearch;
import vaadinapp.data.PropertyIdVisibility;
import vaadinapp.data.ViewPropertyId;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.VaadinSession;

/**
 * @author Marco Casella
 *
 */
public class SitesView extends AbstractSingleTableManagerView {

	private static final long serialVersionUID = 5295128965443660371L;
	public static String NAME = "sites";
	private List<ViewPropertyId> ListOfViewPropertyId;
	
	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getSQLContainer()
	 */
	@Override
	protected SQLContainer getSQLContainer() {
		return VaadinSession.getCurrent().getAttribute(Controller.class).getSitesContainer();
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#initChild()
	 */
	@Override
	protected boolean initChild() {
		ListOfViewPropertyId = new ArrayList<ViewPropertyId> ();
		
		ListOfViewPropertyId.add(new ViewPropertyId("ID",	PropertyIdVisibility.Hidden, PropertyIdBehavior.ReadOnly, 	PropertyIdSearch.NotSearchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("NAME",	PropertyIdVisibility.Always, PropertyIdBehavior.Editable, 	PropertyIdSearch.Searchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("INFORMATION",	PropertyIdVisibility.OnlyInDetail, PropertyIdBehavior.Editable, 	PropertyIdSearch.NotSearchable,	null));

		return true;
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getBackViewName()
	 */
	@Override
	protected String getBackViewName() {
		return MainView.NAME;
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getViewName()
	 */
	@Override
	protected String getViewName() {
		return NAME ;
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getMinimunUserLevel()
	 */
	@Override
	protected int getMinimunUserLevel() {
		return 10;
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getViewPropertyId()
	 */
	@Override
	protected List<ViewPropertyId> getViewPropertyId() {
		return ListOfViewPropertyId;
	}

}
