package it.gigalol.vaadinapp;

import com.vaadin.data.util.sqlcontainer.SQLContainer;

public interface SqlInterface {
	
	public SQLContainer getArticlesContainer();

	public UserBean auth(String user, String pass, int levelreq);
			

}