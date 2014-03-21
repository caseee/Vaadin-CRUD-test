/**
 * 
 */
package it.gigalol.vaadinapp;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.server.VaadinService;

/**
 * @author Marco
 *
 */
public class Model implements Serializable {
	private static final long serialVersionUID = -2821722965659956374L;
	
	private static Model istanza = null;
	public static synchronized Model getModel() {
		if (istanza == null) 
			istanza = new Model();
		return istanza;
	}
	
	private SQLContainer ArticlesContainer;
	public SQLContainer getArticlesContainer() {
		return ArticlesContainer;
	}

	private Connection c = null;
	
	private Model() {
		// Find the application directory
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		String homedir = System.getProperty("user.home");
		String separator = System.getProperty("file.separator");
		String dbfile = basepath + separator  +"WEB-INF"+separator+"sqlite.db";
		String dbinit = basepath + separator  +"WEB-INF"+separator+"init.sql";   
		java.util.logging.Logger.getAnonymousLogger().log(java.util.logging.Level.INFO, "DB LOCATION " + dbinit );

		
		try {

			boolean initdb=true;

			if (new File(dbfile).exists()) initdb = false;
			
			SQLiteConfig config = new SQLiteConfig();
			config.setOpenMode(SQLiteOpenMode.READWRITE);
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+dbfile, config.toProperties());
			
			if (initdb) {

				
				FileReader fr = new FileReader(dbinit);
				PrintWriter stdwriter = new PrintWriter(System.out);
				PrintWriter errwriter = new PrintWriter(System.err);
				SqlRunner sr = new SqlRunner(c, stdwriter, errwriter, false, true);
				sr.runScript(fr);
				fr.close();
				System.out.println("Init database successfully");
			}	else {
				
				JDBCConnectionPool pool = new SimpleJDBCConnectionPool(
				        "org.sqlite.JDBC",
				        "jdbc:sqlite:"+dbfile, 
				        "", "", 2, 5);
				TableQuery tq = new TableQuery("ARTICLES", pool);
				tq.setVersionColumn("OPTLOCK");
				SQLContainer ArticlesContainer = new SQLContainer(tq);
				
			}
				

		} catch ( java.lang.ClassNotFoundException cnfe ) {
			System.err.println( "ERRORE INIT SQLITE: " + cnfe.getClass().getName() + ": " + cnfe.getMessage() );
			System.exit(1);
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		catch ( Exception e ) 
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(1);
		}
		System.out.println("Opened database successfully");
	}

	public boolean auth(String user, String pass, int levelreq) {

		boolean result = false;
		try {
			PreparedStatement stmt = c.prepareStatement("SELECT * FROM USERS WHERE USER=?;");			 
			stmt.setString(1, user);
			ResultSet rs = stmt.executeQuery();

			if (rs == null) 
				result = false;
			else if (rs.next() && rs.isFirst()) {

				int level = rs.getInt("LEVEL");
				String hashpass = rs.getString("PASS");

				if (rs.next()) // Se trova pi� di un utente
					result = false; 
				else if (!hashpass.equals(org.apache.commons.codec.digest.DigestUtils.sha1Hex(pass))) // Se la pass non corrisponde
					result = false;
				else if (level < levelreq) // Se il livello � inferiore
					result = false;
				else
					result = true;
				rs.close();
				stmt.close();
			}


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Error trying autenticate:" + user + " at level:" + levelreq);
			e.printStackTrace();
		}

		return result;

	}
}
