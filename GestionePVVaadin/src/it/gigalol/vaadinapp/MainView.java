package it.gigalol.vaadinapp;

import com.vaadin.navigator.*;
import com.vaadin.navigator.ViewChangeListener.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

public class MainView extends CustomComponent implements View {

	private static final long serialVersionUID = -6734826951324775495L;

	public static final String NAME = "";

    Label text = new Label();

    Button logout = new Button("Logout", new Button.ClickListener() {

		private static final long serialVersionUID = -9081842014270147559L;

		@Override
        public void buttonClick(ClickEvent event) {

            // "Disconnette" l'utente
            getSession().setAttribute("user", null);

            // Refresh questa view, dovrebbe redirezionare alla vista di login
            getUI().getNavigator().navigateTo(NAME);
        }
    });

    public MainView() {
        setCompositionRoot(new CssLayout(text, logout));
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // Get the user name from the session
        String username = String.valueOf(getSession().getAttribute("user"));

        // And show the username
        text.setValue("Hello " + username);
    }
}
