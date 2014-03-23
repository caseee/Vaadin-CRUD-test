package it.gigalol.vaadinapp;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class ApplicationController {
	private static ApplicationController istanza = null;
	private SqlHelper model = SqlHelper.getSqlHelper();	
	private Logger logger = Logger.getLogger("global");
	
	public static synchronized ApplicationController getApplicationController() {
		if (istanza == null) 
			istanza = new ApplicationController();
		return istanza;
	}
	private ApplicationController() {
				
	}
	
	public void log(Level level, String log) {
		logger.log(level,log);
	}
	
	public SQLContainer getArticlesContainer() {
		return model.getArticlesContainer();
	}
	
	public boolean auth(String user, String pass, int levelreq) {
		return model.auth(user, pass, levelreq);
	}
}
