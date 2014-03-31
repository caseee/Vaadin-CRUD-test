package it.gigalol.vaadinapp;

import com.vaadin.data.util.sqlcontainer.SQLContainer;

public interface SqlModel {
	
	public SQLContainer getArticlesContainer();

	public UserBean auth(String user, String pass, int levelreq);
			

}