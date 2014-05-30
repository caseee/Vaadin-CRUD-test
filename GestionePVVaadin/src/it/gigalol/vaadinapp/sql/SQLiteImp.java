/**
 * 
 */
package it.gigalol.vaadinapp.sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;
import org.apache.commons.codec.digest.DigestUtils;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.server.VaadinService;

/**
 * SQLModel implementation for SQLite server. 
 * @author Marco
 *
 */
public abstract class SQLiteImp implements SqlModel, Serializable{
	private static final long serialVersionUID = 6051487700495192428L;
	private static final String WEBDIR = "WEB-INF";
	private static final String DBNAME = "SQLite.db";
	private static final String INITSQL = "SQLiteInit.sql";
	private static final String FILE_SEPARATOR = "file.separator";
	private static final String SQLITE_JDBC_DRIVER_NAME = "org.sqlite.JDBC";
	private static final String SQLITE_JDBC_CONNECTION_STRING_PREAMBLE = "jdbc:sqlite:";
	private static final String SQLITE_USER = "SA";
	private static final String SQLITE_PASS = "";
	private static final String FIND_USER_STATEMENT="SELECT * FROM USERS WHERE USER=?;";
	private static final int SQLITE_JDBC_START_CONNECTION = 2;
	private static final int SQLITE_JDBC_MAX_CONNECTION = 10;
    private String separator = System.getProperty(FILE_SEPARATOR);
	private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

    
	private JDBCConnectionPool pool;
			
	private SQLContainer ArticlesContainer;
	private SQLContainer GroupsContainer;
	
	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.SqlModel#getArticlesContainer()
	 */
	public SQLContainer getArticlesContainer() {
		return ArticlesContainer;
	}
	
	public SQLiteImp() throws java.lang.ClassNotFoundException,SQLException, FileNotFoundException {

		String dbfile = basepath + separator  +WEBDIR+separator+DBNAME;

		boolean initdb=true;

		if (new File(dbfile).exists()) initdb = false;

		SQLiteConfig config = new SQLiteConfig();
		config.setOpenMode(SQLiteOpenMode.READWRITE);
		Class.forName(SQLITE_JDBC_DRIVER_NAME);
		pool = new SimpleJDBCConnectionPool(
				SQLITE_JDBC_DRIVER_NAME,
				SQLITE_JDBC_CONNECTION_STRING_PREAMBLE+dbfile, 
				SQLITE_USER, SQLITE_PASS, 
				SQLITE_JDBC_START_CONNECTION, 
				SQLITE_JDBC_MAX_CONNECTION);

		if (pool==null) throw new SQLException();

		if (initdb) popolateDB();

		TableQuery tqa = new TableQuery("ARTICLES", pool);
		tqa.setVersionColumn("ID");
		ArticlesContainer = new SQLContainer(tqa);
		
		TableQuery tqg = new TableQuery("GROUPS", pool);
		tqg.setVersionColumn("ID");
		GroupsContainer = new SQLContainer(tqg);
		

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


			if (rs.next() && rs.isFirst()) {
				String name = rs.getString(UserBean.USERNAME);
				int level = rs.getInt(UserBean.LEVEL);
				String hashpass = rs.getString(UserBean.HASH_PASSWORD);

				if (rs.next()) // Se trova più di un utente
					result = null; 
				else if (!hashpass.equals(DigestUtils.sha1Hex(pass))) // Se la pass non corrisponde
					result = null;
				else if (level < levelreq) // Se il livello è inferiore
					result = null;
				else
					result = new UserBean(name,level);
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

		
	private void popolateDB() throws FileNotFoundException,SQLException  {

		String dbinit = basepath + separator  +WEBDIR+separator+INITSQL;  
		Connection c = null;
		c = pool.reserveConnection();
		FileReader fr = null;
		PrintWriter stdwriter;
		PrintWriter errwriter;
		SqlRunner sr;
		fr = new FileReader(dbinit);
		stdwriter = new PrintWriter(System.out);
		errwriter = new PrintWriter(System.err);
		sr = new SqlRunner(c, stdwriter, errwriter, false, true);
		sr.runScript(fr);
		

	}

	@Override
	public SQLContainer getCategoriesContainer() throws SQLException {
		return GroupsContainer;
	}

}
