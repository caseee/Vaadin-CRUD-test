package vaadinapp.data;

import java.io.Serializable;

/**
 * This class represents the behavior of Property Id (Column) of a table in a View
 * @author Marco Casella
 * 
 */
public class ViewPropertyId implements Serializable {

	private static final long serialVersionUID = -813797253534433554L;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the searchable
	 */
	public PropertyIdSearch getSearchable() {
		return searchable;
	}

	/**
	 * @return the linked
	 */
	public LinkedProperty getLinked() {
		return linked;
	}


	private String name;
	
	/**
	 * @return the behavior
	 */
	public PropertyIdBehavior getBehavior() {
		return behavior;
	}

	/**
	 * @param name
	 * @param visibility
	 * @param behavior
	 * @param searchable
	 * @param linked
	 */
	public ViewPropertyId(String name, PropertyIdVisibility visibility,
			PropertyIdBehavior behavior, PropertyIdSearch searchable,
			LinkedProperty linked ) {
		super();
		this.name = name;
		this.visibility = visibility;
		this.behavior = behavior;
		this.searchable = searchable;
		this.linked = linked;
	}



	/**
	 * @return the visibility
	 */
	public PropertyIdVisibility getVisibility() {
		return visibility;
	}


	private PropertyIdVisibility visibility;
	private PropertyIdBehavior behavior;
	private PropertyIdSearch searchable;
	private LinkedProperty linked = null;

	
	
	
}


