package it.gigalol.vaadinapp.view;

import it.gigalol.vaadinapp.Controller;

import com.vaadin.navigator.*;
import com.vaadin.navigator.ViewChangeListener.*;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

public class MainView extends CustomComponent implements View {

	private static final long serialVersionUID = -6734826951324775495L;

	public static final String NAME = "";

	Label text = new Label();

	Button logout = new Button("Logout", new Button.ClickListener() {

		private static final long serialVersionUID = -9081842014270147559L;

		@Override
		public void buttonClick(ClickEvent event) {

			// "Disconnette" l'utente
			getSession().getAttribute(Controller.class).logout();

			// Refresh questa view, dovrebbe redirezionare alla vista di login
			getUI().getNavigator().navigateTo(NAME);
		}
	});
	ClickListener mainClickListener = new ClickListener() {
		private static final long serialVersionUID = -2254580865064907743L;
		@Override
		public void buttonClick(ClickEvent event) {
			getUI().getNavigator().navigateTo(NAME);

		}

	};
	Button movimenta = new Button("Movimenta");
	Button articoli = new Button("Articoli", new Button.ClickListener() {
		private static final long serialVersionUID = -9123442014270147559L;
		public void buttonClick(ClickEvent event) {
			// Navigate to main view
			getUI().getNavigator().navigateTo(ArticlesView.NAME);
		}
	});

	Button groups = new Button("Groups", new Button.ClickListener() {
		private static final long serialVersionUID = -9123442014270147559L;
		public void buttonClick(ClickEvent event) {
			// Navigate to main view
			getUI().getNavigator().navigateTo(GroupsView.NAME);
		}
	});
	
	Button Colors = new Button("Colors", new Button.ClickListener() {
		private static final long serialVersionUID = -9123442014233347559L;
		public void buttonClick(ClickEvent event) {
			// Navigate to main view
			getUI().getNavigator().navigateTo(ColorsView.NAME);
		}
	});
	
	Button Users = new Button("Users", new Button.ClickListener() {
		private static final long serialVersionUID = -3123442014270147559L;
		public void buttonClick(ClickEvent event) {
			// Navigate to main view
			getUI().getNavigator().navigateTo(UsersView.NAME);
		}
	});
	
	public MainView() {

		java.util.logging.Logger.getAnonymousLogger().log(java.util.logging.Level.INFO, "MAIN VIEW CREATED" );

		// Add both to a panel
		VerticalLayout fields = new VerticalLayout(groups, articoli, logout, Colors, Users);
		fields.setCaption("Pagina Principale");
		fields.setSpacing(true);
		fields.setMargin(new MarginInfo(true, true, true, false));
		fields.setSizeUndefined();

		// The view root layout
		VerticalLayout viewLayout = new VerticalLayout(fields);
		viewLayout.setSizeFull();
		viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
		viewLayout.setStyleName(Reindeer.LAYOUT_WHITE);
		setCompositionRoot(viewLayout);

		setCompositionRoot(new CssLayout(fields));


	}

	@Override
	public void enter(ViewChangeEvent event) {
		// Get the user name from the session
		String username =  VaadinSession.getCurrent().getAttribute(Controller.class).getLoggedUser().getName();

		// And show the username
		text.setValue("Hello " + username);
	}




}



