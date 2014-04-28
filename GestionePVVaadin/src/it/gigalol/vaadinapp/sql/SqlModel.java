package it.gigalol.vaadinapp.sql;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;

/**
 * Interface for access data from a sql server
 * @author Marco Casella
 *
 */
public interface SqlModel {
	
	
	/**
	 * Return a SQLContainer populated with articles
	 * @return the sql container
	 * @throws SQLException
	 */
	public SQLContainer getArticlesContainer() throws SQLException;

	
	/**
	 * Try to authorize a user 
	 * @param user User name of the user
	 * @param pass Password of the user
	 * @param levelreq Level that user must have
	 * @return a UserBean with the user data, null if authorization fails.
	 */
	public UserBean auth(String user, String pass, int levelreq);
			

}