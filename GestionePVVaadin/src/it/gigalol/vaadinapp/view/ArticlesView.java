package it.gigalol.vaadinapp.view;

/**
 * @author Marco Casella
 *
 */
public class ArticlesView extends AbstractSingleTableManagerView {

	private static final long serialVersionUID = -2762624119626051272L;
	public static final String NAME = "articles";
	public static final String BACK = MainView.NAME;
	
	private final static String [] searchable = new String [] { "NAME" };
	private final static String [] visible = new String [] { "NAME", "PRICE" };
	private final static String [] editable = new String [] { "NAME", "GROUP_ID", "DESCRIPTION","PRICE" };
	
	@Override
	protected String getBackViewName() {
		return BACK;
	}
	@Override
	protected String getViewName() {
		return NAME;
	}
	@Override
	protected String[] getSearchableIds() {
		return searchable;
	}
	@Override
	protected String[] getEditableIds() {
		return editable;
	}
	@Override
	protected String[] getVisibleIds() {
		return visible;
	}
		
}


