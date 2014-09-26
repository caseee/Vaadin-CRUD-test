package vaadinapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.*;

import org.hsqldb.cmdline.SqlToolError;

import vaadinapp.data.UserBean;
import vaadinapp.sql.HSQLDBImpl;
import vaadinapp.sql.SqlModel;
import vaadinapp.view.*;

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
	private ArrayList <AppView> views = new ArrayList <AppView>();
	/**
	 * @return the views
	 */
	public ArrayList<AppView> getViews() {
		return views;
	}

	private UserBean loggedUser = null;
	
	public UserBean getLoggedUser() {
		return loggedUser;
	}

	Controller() {        
       
		try {
			model= new HSQLDBImpl();
			views.add(new AppView("Users", UsersView.class, 50));
			views.add(new AppView("Sites", SitesView.class, 50));
			views.add(new AppView("Articles", ArticlesView.class, 1));
			views.add(new AppView("Groups", GroupsView.class, 1));
			views.add(new AppView("Colors", ColorsView.class, 1));
			views.add(new AppView("Sizes", SizesView.class, 1));
			views.add(new AppView("Registry", RegistryView.class, 1));
			
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
		} catch (SqlToolError e) {
			log(Level.SEVERE, "SqlTool Error.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
		} catch (IOException e) {
			log(Level.SEVERE, "File db error.");
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
		} catch (SQLException  | NullPointerException e) {
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

	public SQLContainer getCategoriesContainer() {
		try {
			return model.getCategoriesContainer();
		} catch (SQLException  | NullPointerException e) {
			log(Level.SEVERE, "Error retrieving Categories data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}
	
	public SQLContainer getUsersContainer() {
		try {
			return model.getUsersContainer();
		} catch (SQLException  | NullPointerException e) {
			log(Level.SEVERE, "Error retrieving Users data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}
	
	public SQLContainer getColorsContainer() {
		try {
			return model.getColorsContainer();
		} catch (SQLException  | NullPointerException e) {
			log(Level.SEVERE, "Error retrieving Colors data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}

	public SQLContainer getSizesContainer() {
		try {
			return model.getSizesContainer();
		} catch (SQLException  | NullPointerException e) {
			log(Level.SEVERE, "Error retrieving Sizes data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}

	public SQLContainer getSitesContainer() {
		try {
			return model.getSitesContainer();
		} catch (SQLException  | NullPointerException e) {
			log(Level.SEVERE, "Error retrieving Sites data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}
	
	public SQLContainer getRegistryContainer() {
		try {
			return model.getRegistryContainer();
		} catch (SQLException  | NullPointerException e) {
			log(Level.SEVERE, "Error retrieving Registry data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}
	
	public SQLContainer getMovimentationsContainer() {
		try {
			return model.getMovimentationsContainer();
		} catch (SQLException  | NullPointerException e) {
			log(Level.SEVERE, "Error retrieving Registry data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}

	public SQLContainer getMovimentation_TypesContainer() {
		try {
			return model.getMovimentation_TypesContainer();
		} catch (SQLException  | NullPointerException e) {
			log(Level.SEVERE, "Error retrieving Registry data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}
	
	public SQLContainer getMovimentation_SpecsContainer() {
		try {
			return model.getMovimentation_SpecsContainer();
		} catch (SQLException  | NullPointerException e) {
			log(Level.SEVERE, "Error retrieving Registry data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}

	/**
	 * @return
	 */
	public SQLContainer getInventoryContainer() {
		try {
			return model.getInventoryContainer();
		} catch (SQLException  | NullPointerException e) {
			log(Level.SEVERE, "Error retrieving Registry data.");
			e.printStackTrace();
			UI.getCurrent().getSession().close();
			return null;
		}
	}
	
	
	
}
