package it.gigalol.vaadinapp;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;

public interface SqlModel {
	
	public SQLContainer getArticlesContainer() throws SQLException;

	public UserBean auth(String user, String pass, int levelreq);
			

}