/**
 * AppView.java
 */
package vaadinapp;

import com.vaadin.navigator.View;

/**
 * @author Marco Casella
 *
 */
public class AppView {

	/**
	 * @param viewName
	 * @param viewClass
	 * @param levelRequired
	 */
	public AppView(String viewName, Class<? extends View> viewClass,
			int levelRequired) {
		super();
		this.viewName = viewName;
		this.viewClass = viewClass;
		this.levelRequired = levelRequired;
	}
	/**
	 * @return the viewName
	 */
	public java.lang.String getViewName() {
		return viewName;
	}
	/**
	 * @return the viewClass
	 */
	public java.lang.Class<? extends View> getViewClass() {
		return viewClass;
	}
	/**
	 * @return the levelRequired
	 */
	public int getLevelRequired() {
		return levelRequired;
	}
	private java.lang.String viewName;
    private java.lang.Class<? extends View> viewClass;
    private int levelRequired;
    
    
	
}
