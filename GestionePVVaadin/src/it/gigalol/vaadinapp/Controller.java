package it.gigalol.vaadinapp;

import it.gigalol.vaadinapp.sql.HSQLDBImpl;
import it.gigalol.vaadinapp.sql.SqlModel;
import it.gigalol.vaadinapp.sql.UserBean;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.*;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.UI;

/**
 * Manages business logic of the application
 * @author Marco Casella
 *
 */
public class Controller implements Serializable {
	private static final long serialVersionUID = 3911062516609139081L;
	private static final String LOGGER_TYPE = "global";
	private static SqlModel model; 	
	
	private UserBean loggedUser = null;
	
	public UserBean getLoggedUser() {
		return loggedUser;
	}

	Controller() {        
       
		try {
			model= new HSQLDBImpl();
		} catch (ClassNotFoundException e) {
			log(Level.SEVERE, "Error loading JDBC driver");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
		} catch (SQLException e) {
			log(Level.SEVERE, "Error connecting to the database.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
		} catch (FileNotFoundException e) {
			log(Level.SEVERE, "Error database file not found.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
		}
	}
	
	public void log(Level level, String log) {
		Logger.getLogger(LOGGER_TYPE).log(level,log);
	}
	
	public SQLContainer getArticlesContainer() {
		try {
			return model.getArticlesContainer();
		} catch (SQLException e) {
			log(Level.SEVERE, "Error retrieving data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}
	
	public boolean login(String user, String pass, int levelreq) {
		loggedUser=model.auth(user, pass, levelreq);
		if (loggedUser==null) 
			return false;
		else 
			return true;
			
	}
	
	public void logout() {
		loggedUser=null;
	}

	public SQLContainer getGroupsContainer() {
		try {
			return model.getGroupsContainer();
		} catch (SQLException e) {
			log(Level.SEVERE, "Error retrieving data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}
	
	public SQLContainer getUsersContainer() {
		try {
			return model.getUsersContainer();
		} catch (SQLException e) {
			log(Level.SEVERE, "Error retrieving data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}
}
