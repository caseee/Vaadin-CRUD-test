package it.gigalol.vaadinapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.digest.DigestUtils;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.server.VaadinService;

public class HSQLDBImpl implements SqlModel {
	private static final String WEBDIR = "WEB-INF";
	private static final String DBNAME = "HSQLDB.db";
	private static final String DBNAME_EXTENSION = ".properties";
	private static final String INITSQL = "HSQLDBInit.sql";
	private static final String FILE_SEPARATOR = "file.separator";
	private static final String HSQLDB_JDBC_DRIVER_NAME = "org.hsqldb.jdbc.JDBCDriver";
	private static final String HSQLDB_JDBC_CONNECTION_STRING_PREAMBLE = "jdbc:hsqldb:";
	private static final String HSQLDB_OPTION = ";ifexists=true";
	private static final String HSQLDB_USER = "SA";
	private static final String HSQLDB_PASS = "";
	private static final String FIND_USER_STATEMENT="SELECT * FROM USERS WHERE USER=?;";
	private static final int HSQLDB_JDBC_START_CONNECTION = 2;
	private static final int HSQLDB_JDBC_MAX_CONNECTION = 10;
	
	private String separator = System.getProperty(FILE_SEPARATOR);
	private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	
	private JDBCConnectionPool pool;
			
	private SQLContainer ArticlesContainer;
	public SQLContainer getArticlesContainer() {
		return ArticlesContainer;
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

		TableQuery tq = new TableQuery("ARTICLES", pool);
		tq.setVersionColumn("ID");
		ArticlesContainer = new SQLContainer(tq);


	}

	/* (non-Javadoc)
	 * @see it.gigalol.vaadinapp.SqlInterface#auth(java.lang.String, java.lang.String, int)
	 */
	@Override
	public UserBean auth(String user, String pass, int levelreq) {
		Connection c;
		UserBean result = null;
		try {
			c = pool.reserveConnection();	
			PreparedStatement stmt = c.prepareStatement(FIND_USER_STATEMENT);			 
			stmt.setString(1, user);
			ResultSet rs = stmt.executeQuery();

			if (rs == null) 
				result = null;
			else if (rs.next() && rs.isFirst()) {
				String name = rs.getString(UserBean.NAME);
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
				c.commit();
				rs.close();
				stmt.close();
				pool.releaseConnection(c);
				
			}


		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		finally {
								
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
}
