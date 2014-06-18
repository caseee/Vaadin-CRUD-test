package vaadinapp.sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
public class HSQLDBImpl implements SqlModel {
	private static final String WEBDIR = "WEB-INF";
	private static final String SCRIPTDIR = "SQL";
	private static final String DBNAME = "HSQLDB.db";
	private static final String DBNAME_EXTENSION = ".properties";
	private static final String [] INITSQL = { "HSQLDB-TABLE.sql", "HSQLDB-BASE.sql","HSQLDB-PROCEDURE.sql", "HSQLDB-VERSIONTRIGGER.sql",  "HSQLDB-TRIGGER.sql"  };
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
	
	public HSQLDBImpl() throws java.lang.ClassNotFoundException,SQLException, SqlToolError, IOException {

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

		
	/**
	 * Uses SqlRunner class to run a sql script and create the tables of databases
	 * @throws SQLException if the script execution fails
	 * @throws IOException 
	 * @throws SqlToolError 
	 */
	private void popolateDB() throws SQLException, IOException, SqlToolError  {

		 
		Connection c = null;
		c = pool.reserveConnection();
		
		Map<String, String> sqlVarMap = new HashMap<String, String>();
        sqlVarMap.put("invoker", getClass().getName());
        // This variable is pretty useless, but this should show you how to
        // set variables which you can access inside of scripts like *{this}.

        File file;
        SqlFile sqlFile;
        
        for (String fileString : INITSQL) {
        	String dbinit = basepath + separator  +WEBDIR+separator+SCRIPTDIR+separator+fileString; 
            file = new File(dbinit);
            if (!file.isFile())
                throw new IOException("SQL file not present: "
                        + file.getAbsolutePath());
            sqlFile = new SqlFile(file);
            sqlFile.setConnection(c);
            sqlFile.addUserVars(sqlVarMap);
            sqlFile.execute();

            // The only reason for the following two statements is so that
            // changes made by one .sql file will effect the future SQL files.
            // Has no effect if you only execute one SQL file.
            c = sqlFile.getConnection();
            sqlVarMap = sqlFile.getUserVars();
        }        


		
//		FileReader fr = null;
//		PrintWriter stdwriter;
//		PrintWriter errwriter;
//		SqlRunner sr;
//		fr = new FileReader(dbinit);
//		stdwriter = new PrintWriter(System.out);
//		errwriter = new PrintWriter(System.err);
//		sr = new SqlRunner(c, stdwriter, errwriter, false, true);
//		sr.runScript(fr);
//		

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
