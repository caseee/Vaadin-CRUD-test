package it.gigalol.vaadinapp.view;

import com.vaadin.server.VaadinSession;

import it.gigalol.vaadinapp.Controller;
import it.gigalol.vaadinapp.sql.LinkedTable;


/**
 * Extends AbstractSingleTableManagerView to show, search, edit, add and delete articles.
 * @author Marco Casella
 *
 */
public class ArticlesView extends AbstractSingleTableManagerView {

	public static String NAME = "articles";
	private static final long serialVersionUID = -2762624119626051272L;
		
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
		return new String [] { "NAME", "DESCRIPTION" };
	}
	
	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getEditIds()
	 */
	@Override
	protected String[] getEditIds() {
		return new String [] { "NAME", "GROUP_ID", "DESCRIPTION","PRICE" };
	}
	
	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getShowIds()
	 */
	@Override
	protected String[] getShowIds() {
		return new String [] { "NAME", "PRICE", "GROUP_ID" };
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.view.AbstractSingleTableManagerView#getLinkedTable()
	 */
	@Override
	protected LinkedTable[] getLinkedTable() {
		return new LinkedTable [] {new LinkedTable("GROUP_ID", VaadinSession.getCurrent().getAttribute(Controller.class).getGroupsContainer(), "DESCRIPTION")};
	}
		
}


