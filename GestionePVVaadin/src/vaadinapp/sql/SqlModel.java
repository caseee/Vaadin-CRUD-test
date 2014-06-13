package vaadinapp.sql;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;

/**
 * Interface for access data from a sql server
 * @author Marco Casella
 *
 */
public interface SqlModel {
	
	/**
	 * Return a SQLContainer populated with users
	 * @return the sql container
	 * @throws SQLException
	 */
	public SQLContainer getUsersContainer() throws SQLException;
	
	/**
	 * Return a SQLContainer populated with articles
	 * @return the sql container
	 * @throws SQLException
	 */
	public SQLContainer getArticlesContainer() throws SQLException;

	/**
	 * @return  a SQLContainer populated with CATEGORIES
	 * @throws SQLException
	 */
	public SQLContainer getCategoriesContainer() throws SQLException;
	
	/**
	 * @return  a SQLContainer populated with COLORS
	 * @throws SQLException
	 */
	public SQLContainer getColorsContainer() throws SQLException;
	
	/**
	 * @return  a SQLContainer populated with SITES
	 * @throws SQLException
	 */
	public SQLContainer getSitesContainer() throws SQLException;
	
	/**
	 * @return  a SQLContainer populated with SEASONS
	 * @throws SQLException
	 */
	public SQLContainer getSeasonsContainer() throws SQLException;
	
	/**
	 * @return  a SQLContainer populated with SIZES
	 * @throws SQLException
	 */
	public SQLContainer getSizesContainer() throws SQLException;
	
	
	/**
	 * @return  a SQLContainer populated with REGISTRY
	 * @throws SQLException
	 */
	public SQLContainer getRegistryContainer() throws SQLException;
	
	/**
	 * Try to authorize a user 
	 * @param user User name of the user
	 * @param pass Password of the user
	 * @param levelreq Level that user must have
	 * @return a UserBean with the user data, null if authorization fails.
	 */
	public UserBean auth(String user, String pass, int levelreq);
			

}