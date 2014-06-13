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
public class ColorsView extends AbstractSingleTableManagerView {

	public static String NAME = "colors";
	private List<ViewPropertyId> ListOfViewPropertyId;
	private static final long serialVersionUID = -2761124119116051272L;
			
	
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
		return VaadinSession.getCurrent().getAttribute(Controller.class).getColorsContainer();
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

		ListOfViewPropertyId.add(new ViewPropertyId("ID",	PropertyIdVisibility.Hidden, PropertyIdBehavior.ReadOnly, 	PropertyIdSearch.NotSearchable,	null));
		ListOfViewPropertyId.add(new ViewPropertyId("NAME",	PropertyIdVisibility.Always, PropertyIdBehavior.Editable, 	PropertyIdSearch.NotSearchable,	null));

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
