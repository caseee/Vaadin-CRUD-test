package it.gigalol.vaadinapp.sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.digest.DigestUtils;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.server.VaadinService;

/**
 * SQLModel implementation for HyperSql server.
 * @author Marco Casella
 *
 */
public class HSQLDBImpl implements SqlModel {
	private static final String WEBDIR = "WEB-INF";
	private static final String DBNAME = "HSQLDB.db";
	private static final String DBNAME_EXTENSION = ".properties";
	private static final String INITSQL = "HSQLDBInit.sql";
	private static final String FILE_SEPARATOR = "file.separator";
	private static final String HSQLDB_JDBC_DRIVER_NAME = "org.hsqldb.jdbc.JDBCDriver";
	private static final String HSQLDB_JDBC_CONNECTION_STRING_PREAMBLE = "jdbc:hsqldb:";
	private static final String HSQLDB_USER = "SA";
	private static final String HSQLDB_PASS = "";
	private static final String FIND_USER_STATEMENT="SELECT * FROM USERS WHERE USERNAME = ?;";
	private static final int HSQLDB_JDBC_START_CONNECTION = 2;
	private static final int HSQLDB_JDBC_MAX_CONNECTION = 10;
	
	private String separator = System.getProperty(FILE_SEPARATOR);
	private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	
	private JDBCConnectionPool pool;

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.SqlModel#getArticlesContainer()
	 */
	public SQLContainer getArticlesContainer() throws SQLException {
		TableQuery tq = new TableQuery(null, null, "ARTICLES", pool, new DefaultSQLGenerator());
		tq.setVersionColumn("ID");
		return new SQLContainer(tq);		 
	}
	
	public HSQLDBImpl() throws java.lang.ClassNotFoundException,SQLException, FileNotFoundException {

		String dbfile = basepath + separator + WEBDIR + separator + DBNAME;

		boolean initdb=true;

		if (new File(dbfile+DBNAME_EXTENSION).exists()) initdb = false;

		Class.forName(HSQLDB_JDBC_DRIVER_NAME);
		pool = new SimpleJDBCConnectionPool(
				HSQLDB_JDBC_DRIVER_NAME,
				HSQLDB_JDBC_CONNECTION_STRING_PREAMBLE+dbfile, 
				HSQLDB_USER, HSQLDB_PASS, 
				HSQLDB_JDBC_START_CONNECTION, 
				HSQLDB_JDBC_MAX_CONNECTION);

		if (pool==null) throw new SQLException();

		if (initdb) popolateDB();

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

				if (rs.next()) // Se trova pi� di un utente
					result = null; 
				else if (!hashpass.equals(DigestUtils.sha1Hex(pass))) // Se la pass non corrisponde
					result = null;
				else if (level < levelreq) // Se il livello � inferiore
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
	public SQLContainer getGroupsContainer() throws SQLException {
		TableQuery tqg = new TableQuery("GROUPS", pool);
		tqg.setVersionColumn("ID");
		return new SQLContainer(tqg);
	}
}
