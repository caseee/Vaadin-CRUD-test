package it.gigalol.vaadinapp.data;

import it.gigalol.vaadinapp.sql.LinkedTable;

/**
 * This class represents the behavior of Property Id (Column) of a table in a View
 * @author Marco Casella
 * 
 */
public class ViewPropertyId {

	/**
	 * @param name
	 * @param behaviour
	 * @param visibility
	 * @param searchable
	 * @param linked
	 */
	public ViewPropertyId(String name, PropertyIdBehavior behaviour,
			PropertyIdVisibility visibility, PropertyIdSearch searchable,
			LinkedTable linked) {
		super();
		this.name = name;
		this.behaviour = behaviour;
		this.visibility = visibility;
		this.searchable = searchable;
		this.linked = linked;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the visibility
	 */
	public PropertyIdVisibility getVisibility() {
		return visibility;
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
	public LinkedTable getLinked() {
		return linked;
	}

	/**
	 * @param behaviour the behaviour to set
	 */
	public void setBehaviour(PropertyIdBehavior behaviour) {
		this.behaviour = behaviour;
	}

	private String name;
	private PropertyIdBehavior behaviour;
	private PropertyIdVisibility visibility;
	private PropertyIdSearch searchable;
	private LinkedTable linked = null;
	
	ViewPropertyId() {
		
	}
	
}


