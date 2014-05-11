package it.gigalol.vaadinapp.view;

import it.gigalol.vaadinapp.Controller;
import it.gigalol.vaadinapp.sql.LinkedTable;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.server.VaadinSession;

public class GroupsView extends AbstractSingleTableManagerView {

	public static String NAME = "groups";
	private static final long serialVersionUID = -2762613219626051272L;
		
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
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getSearchIds()
	 */
	@Override
	protected String[] getSearchIds() {
		return new String [] { "DESCRIPTION" };
	}
	
	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getEditIds()
	 */
	@Override
	protected String[] getEditIds() {
		return new String [] { "FATHER_ID", "DESCRIPTION" };
	}
	
	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getShowIds()
	 */
	@Override
	protected String[] getShowIds() {
		return new String [] { "FATHER_ID", "DESCRIPTION" };
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getLinkedTable()
	 */
	@Override
	protected LinkedTable[] getLinkedTable() {
		return new LinkedTable[] {};
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getSQLContainer()
	 */
	@Override
	protected SQLContainer getSQLContainer() {
		return VaadinSession.getCurrent().getAttribute(Controller.class).getGroupsContainer();
	}
		
}