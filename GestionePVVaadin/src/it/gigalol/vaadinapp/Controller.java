package it.gigalol.vaadinapp;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("gestionepvvaadin")
public class Controller extends UI {
	
	private Model model = Model.getModel();	
	
	public SQLContainer getArticlesContainer() {
		return model.getArticlesContainer();
	}
	
	public boolean auth(String user, String pass, int levelreq) {
		return model.auth(user, pass, levelreq);
	}
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = Controller.class)
	public static class Servlet extends VaadinServlet {
		
	}
	
	protected void init(VaadinRequest request) {
		
		getSession().setAttribute("Controller", this);
		
		// Crea una nuova istanza Navigator, attacata alla vista corrente
		new Navigator(this, this);

		// Aggiunge la vista del log-in
		getNavigator().addView(LoginView.NAME, LoginView.class);

		// Aggiunge la vista principale dell'applicazione
		getNavigator().addView(MainView.NAME, MainView.class);
		
		getNavigator().addView(ArticlesView.NAME, ArticlesView.class);

		// In caso di cambio di vista, viene controllato che vista mostrare
		getNavigator().addViewChangeListener(new ViewChangeListener() {

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {               

				// Controlla se l'utente è connesso
				boolean isLoggedIn = getSession().getAttribute("user") != null;
				boolean isLoginView = event.getNewView() instanceof LoginView;

				if (!isLoggedIn && !isLoginView) {
					// Se non è connesso manda alla vista di log-in
					getNavigator().navigateTo(LoginView.NAME);
					return false;

				} else if (isLoggedIn && isLoginView) {
					// In caso sia loggato e cerca di passare al log-in annulla
					return false;
				}

				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {

			}
		});

	}

}