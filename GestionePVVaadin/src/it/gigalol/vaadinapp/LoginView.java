package it.gigalol.vaadinapp;


import java.io.Serializable;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;



/**
 * Shows a simple log-in view.
 * @author Marco Casella
 *
 */
public class LoginView extends CustomComponent implements View, Button.ClickListener, Serializable {

	private static final long serialVersionUID = 3350818906987552789L;
	private static final String COMPONENT_WIDTH = "300px";
	public static final String NAME = "login";
	private final TextField user;
	private final PasswordField password;
	private final Button loginButton;	

	public LoginView() {
		setSizeFull();
		// Create the user input field
		user = new TextField("Nome Utente:");
		user.setWidth(COMPONENT_WIDTH);
		user.setRequired(true);
		//user.addValidator(new UserValidator());
		user.setInputPrompt("Il tuo nome utente");
		user.setInvalidAllowed(false);
		// Create the password input field
		password = new PasswordField("Password:");
		password.setWidth("300px");
		//password.addValidator(new PasswordValidator());				
		password.setRequired(true);
		password.setValue("");
		password.setNullRepresentation("");
		// Create login button
		loginButton = new Button("Login", this);
		loginButton.setWidth(COMPONENT_WIDTH);
		// Add both to a panel
		VerticalLayout fields = new VerticalLayout(user, password, loginButton);
		fields.setCaption("Inserire i dati di accesso per l'applicazione. (Default admin/nimad)");
		fields.setSpacing(true);
		fields.setMargin(new MarginInfo(true, true, true, false));
		fields.setSizeUndefined();
		// The view root layout
		VerticalLayout viewLayout = new VerticalLayout(fields);
		viewLayout.setSizeFull();
		viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
		viewLayout.setStyleName(Reindeer.LAYOUT_WHITE);
		setCompositionRoot(viewLayout);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// focus the user name field when user arrives to the login view
		user.focus();
	}

	
	@Override
	public void buttonClick(ClickEvent event) {
		//
		// Validate the fields using the navigator. By using validators for the
		// fields we reduce the amount of queries we have to use to the database
		// for wrongly entered passwords
		//
		if (!user.isValid() || !password.isValid()) {
			return;
		}

		String username = user.getValue();
		String password = this.password.getValue();

		boolean isValid =  VaadinSession.getCurrent().getAttribute(Controller.class).login(username, password, 1);

		if(isValid){

			// Navigate to main view
			getUI().getNavigator().navigateTo(MainView.NAME);

		} else {

			// Wrong password clear the password field and refocuses it
			this.password.setValue(null);
			this.password.focus();
		}
	}
}
