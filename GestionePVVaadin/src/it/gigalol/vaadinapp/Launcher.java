package it.gigalol.vaadinapp;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("gestionepvvaadin")
public class Launcher extends UI {
	
	Controller controller = new Controller();
	
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = Launcher.class)
	public static class Servlet extends VaadinServlet {
		
	}
	
	protected void init(VaadinRequest request) {
		
		getSession().setAttribute("Controller", controller);
		
		// Crea una nuova istanza Navigator, attacata alla vista corrente
		new Navigator(this, this);

		// Aggiunge la vista del log-in
		getNavigator().addView(LoginView.NAME, LoginView.class);

		// Aggiunge la vista principale dell'applicazione
		getNavigator().addView(MainView.NAME, MainView.class);

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