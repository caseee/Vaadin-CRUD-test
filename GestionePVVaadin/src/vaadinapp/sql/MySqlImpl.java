package vaadinapp.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import vaadinapp.data.UserBean;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.server.VaadinService;

import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;


/**
 * SQLModel implementation for HyperSql server.
 * @author Marco Casella
 *
 */
public class MySqlImpl implements SqlModel {
	private static final String JDBC_DRIVER_NAME = "com.mysql.jdbc.Driver";
	private static final String JDBC_CONNECTION_STRING_PREAMBLE = "jdbc:mysql:";
	private static final String SQL_IP = "192.168.1.7";
	private static final String SQL_DB = "gestione";
	private static final String SQL_USER = "gestione";
	private static final String SQL_PASS = "Qaj5mClM";
	private static final String FIND_USER_STATEMENT="SELECT * FROM USERS WHERE USERNAME = ?;";
	private static final int JDBC_START_CONNECTION = 2;
	private static final int JDBC_MAX_CONNECTION = 10;

	
	private JDBCConnectionPool pool;

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.SqlModel#getArticlesContainer()
	 */
	public SQLContainer getArticlesContainer() throws SQLException {
		TableQuery tq = new TableQuery(null, null, "ARTICLES", pool, new DefaultSQLGenerator());
		tq.setVersionColumn("ID");
		return new SQLContainer(tq);		 
	}
	
	public MySqlImpl() throws java.lang.ClassNotFoundException,SQLException, SqlToolError, IOException, InstantiationException, IllegalAccessException {

		
		pool = new SimpleJDBCConnectionPool(
				JDBC_DRIVER_NAME,
				JDBC_CONNECTION_STRING_PREAMBLE+"//"+SQL_IP+"/"+SQL_DB,
				SQL_USER, 
				SQL_PASS, 
				JDBC_START_CONNECTION, 
				JDBC_MAX_CONNECTION);

		if (pool==null) throw new SQLException();
		

	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.SqlInterface#auth(java.lang.String, java.lang.String, int)
	 */
	@Override
	public UserBean auth(String user, String pass, int levelreq) {
		Connection c = null;
		UserBean result = null;
		try {
			c = pool.reserveConnection();	
			PreparedStatement stmt = c.prepareStatement(FIND_USER_STATEMENT);			 
			stmt.setString(1, user);
			ResultSet rs = stmt.executeQuery();

			if (rs == null) 
				result = null;
			else if (rs.next() && rs.isFirst()) {
				String name = rs.getString(UserBean.USERNAME);
				int level = rs.getInt(UserBean.LEVEL);
				String hashpass = rs.getString(UserBean.HASH_PASSWORD);
				int site = rs.getInt(UserBean.SITE);

				if (rs.next()) // Se trova piu' di un utente
					result = null; 
				else if (!hashpass.equals(DigestUtils.sha1Hex(pass))) // Se la pass non corrisponde
					result = null;
				else if (level < levelreq) // Se il livello e' inferiore
					result = null;
				else
					result = new UserBean(name,level,site);
				
				rs.close();
				stmt.close();
				c.close();
				
			}

		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
			pool.releaseConnection(c);			
		}

		return result;

	}

		

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.sql.SqlModel#getGroupsContainer()
	 */
	@Override
	public SQLContainer getCategoriesContainer() throws SQLException {
		TableQuery tqg = new TableQuery("CATEGORIES", pool);
		tqg.setVersionColumn("VERSIONID");
		
		return new SQLContainer(tqg);
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.sql.SqlModel#getUsersContainer()
	 */
	@Override
	public SQLContainer getUsersContainer() throws SQLException {
		TableQuery tqg = new TableQuery("USERS", pool, new DefaultSQLGenerator());
		tqg.setVersionColumn("VERSIONID");
		return new SQLContainer(tqg);
	}
	
	private SQLContainer getContainer(String tableName) throws SQLException {
		TableQuery tqg = new TableQuery(tableName, pool, new DefaultSQLGenerator());
		tqg.setVersionColumn("VERSIONID");
		return new SQLContainer(tqg);
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.sql.SqlModel#getColorsContainer()
	 */
	@Override
	public SQLContainer getColorsContainer() throws SQLException {
		return getContainer("COLORS");
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.sql.SqlModel#getSitesContainer()
	 */
	@Override
	public SQLContainer getSitesContainer() throws SQLException {
		return getContainer("SITES");
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.sql.SqlModel#getSeasonsContainer()
	 */
	@Override
	public SQLContainer getSeasonsContainer() throws SQLException {
		return getContainer("SEASONS");
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.sql.SqlModel#getSizesContainer()
	 */
	@Override
	public SQLContainer getSizesContainer() throws SQLException {
		return getContainer("SIZES");
	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.sql.SqlModel#getRegistryContainer()
	 */
	@Override
	public SQLContainer getRegistryContainer() throws SQLException {
		return getContainer("REGISTRY");
	}

	/* (non-Javadoc)
	 * @see vaadinapp.sql.SqlModel#getMovimentationsContainer()
	 */
	@Override
	public SQLContainer getMovimentationsContainer() throws SQLException {
		return getContainer("MOVIMENTATIONS");
	}

	/* (non-Javadoc)
	 * @see vaadinapp.sql.SqlModel#getMovimentation_SpecsContainer()
	 */
	@Override
	public SQLContainer getMovimentation_SpecsContainer() throws SQLException {
		return getContainer("MOVIMENTATION_SPECS");
	}

	/* (non-Javadoc)
	 * @see vaadinapp.sql.SqlModel#getMovimentation_TypesContainer()
	 */
	@Override
	public SQLContainer getMovimentation_TypesContainer() throws SQLException {
		return getContainer("MOVIMENTATION_TYPES");
	}

	/* (non-Javadoc)
	 * @see vaadinapp.sql.SqlModel#getInventoryContainer()
	 */
	@Override
	public SQLContainer getInventoryContainer() throws SQLException {
		return getContainer("INVENTORY");
	}
	

	
}
