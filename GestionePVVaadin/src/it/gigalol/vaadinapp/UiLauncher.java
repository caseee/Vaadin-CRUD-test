package it.gigalol.vaadinapp;

import it.gigalol.vaadinapp.view.ArticlesView;
import it.gigalol.vaadinapp.view.LoginView;
import it.gigalol.vaadinapp.view.MainView;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/**
 * Crea un oggetto Controller che gestisce tutti i dati di sessione dell'applicazione
 * e lo registra negli attributi per poterci accedere in ogni classe.
 * Crea un oggetto navigator a cui registra tutte le possibili view,
 * passa alla view iniziale di log-in e controlla ad ogni cambio di view 
 * che l'utente sia loggato, alternativamente rimanda alla pagina di log-in.
 * @author Marco Casella
 */
@Theme("gestionepvvaadin")
public class UiLauncher extends UI {
		
	private static final long serialVersionUID = 4193824680180672120L;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = UiLauncher.class)
	public static class Servlet extends VaadinServlet {

		private static final long serialVersionUID = 1186087034464094368L;
		
	}
	
	private Controller controller = new Controller();
		
	protected void init(VaadinRequest request) {
			
		
		 VaadinSession.getCurrent().setAttribute(Controller.class, controller);
		
		// Crea una nuova istanza Navigator, attacata alla vista corrente
		new Navigator(this, this);

		// Aggiunge la vista del log-in
		getNavigator().addView(LoginView.NAME, LoginView.class);

		// Aggiunge la vista principale dell'applicazione
		getNavigator().addView(MainView.NAME, MainView.class);
		
		getNavigator().addView(ArticlesView.NAME, ArticlesView.class);
		
//		getNavigator().addView(GroupsView.NAME, GroupsView.class);

		// In caso di cambio di vista, viene controllato che vista mostrare
		getNavigator().addViewChangeListener(new ViewChangeListener() {

			private static final long serialVersionUID = 2609946595078253934L;

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {               

				// Controlla se l'utente è connesso
				boolean isLoggedIn =  VaadinSession.getCurrent().getAttribute(Controller.class).getLoggedUser() != null;
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