/**
 * AppView.java
 */
package vaadinapp;

import java.io.Serializable;

import com.vaadin.navigator.View;

/**
 * @author Marco Casella
 *
 */
public class AppView implements Serializable {

	private static final long serialVersionUID = -1803907903358962357L;
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
