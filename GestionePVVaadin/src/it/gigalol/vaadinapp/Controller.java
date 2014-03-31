package it.gigalol.vaadinapp;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.logging.*;


import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.UI;

public class Controller {
	private static final String LOGGER_TYPE = "global";
	private static SqlModel model; 	
	private Logger logger = Logger.getLogger(LOGGER_TYPE);
	
	private UserBean loggedUser = null;
	
	public UserBean getLoggedUser() {
		return loggedUser;
	}


	Controller() {        
       
		try {
			model= new SQLiteImp();
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
		logger.log(level,log);
	}
	
	public SQLContainer getArticlesContainer() {
		return model.getArticlesContainer();
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
}
