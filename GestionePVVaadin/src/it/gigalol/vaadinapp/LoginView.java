package it.gigalol.vaadinapp;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class LoginView extends CustomComponent implements View, Button.ClickListener {

	private static final long serialVersionUID = 3350818906987552789L;

	public static final String NAME = "login";

	private final TextField user;

	private final PasswordField password;

	private final Button loginButton;

	public LoginView() {

		setSizeFull();

		// Create the user input field
		user = new TextField("Nome Utente:");
		user.setWidth("300px");
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
		loginButton.setWidth("300px");
		
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
		// focus the username field when user arrives to the login view
		user.focus();
	}

	
	private static final class UserValidator extends AbstractValidator<String> {

		private static final long serialVersionUID = 4213573236844539042L;

		public UserValidator() {
			super("Nome utente non valido");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean isValidValue(String value) {
			return true;
		}

		@Override
		public Class<String> getType() {
			return String.class;
		}
		
	}
	
	
	//
	// Validator for validating the passwords
	//
	private static final class PasswordValidator extends 	AbstractValidator<String> {

		private static final long serialVersionUID = -9001413658148628824L;

		public PasswordValidator() {
			super("Password non valida.");
		}

		@Override
		protected boolean isValidValue(String value) {
			//
			// Password must be at least 8 characters long and contain at least
			// one number
			//
			if (value != null
					&& (value.length() < 8 || !value.matches(".*\\d.*"))) {
				return false;
			}
			return true;
		}

		@Override
		public Class<String> getType() {
			return String.class;
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {

		//
		// Validate the fields using the navigator. By using validors for the
		// fields we reduce the amount of queries we have to use to the database
		// for wrongly entered passwords
		//
		if (!user.isValid() || !password.isValid()) {
			return;
		}

		String username = user.getValue();
		String password = this.password.getValue();

		//
		// Validate username and password with database here. For examples sake
		// I use a dummy username and password.
		//
		boolean isValid = ((Controller) getSession().getAttribute("Controller")).auth(username, password, 1);

		if(isValid){
			// Store the current user in the service session
			getSession().setAttribute("user", username);

			// Navigate to main view
			getUI().getNavigator().navigateTo(MainView.NAME);

		} else {

			// Wrong password clear the password field and refocuses it
			this.password.setValue(null);
			this.password.focus();
		}
	}
}
