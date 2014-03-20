package it.gigalol.vaadinapp;

import com.vaadin.navigator.*;
import com.vaadin.navigator.ViewChangeListener.*;
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
            getSession().setAttribute("user", null);

            // Refresh questa view, dovrebbe redirezionare alla vista di login
            getUI().getNavigator().navigateTo(NAME);
        }
    });

    Button movimenta = new Button("Movimenta");
    Button articoli = new Button("Articoli");
    
    
    public MainView() {
    	
		// Add both to a panel
		VerticalLayout fields = new VerticalLayout(movimenta, articoli, logout );
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
        String username = String.valueOf(getSession().getAttribute("user"));

        // And show the username
        text.setValue("Hello " + username);
    }
    
    
    ClickListener MainClickListener = new ClickListener() {
    	private static final long serialVersionUID = -2254580865064907743L;
    	@Override
    	public void buttonClick(ClickEvent event) {
    		
    		
    	}
    	
    };
    
}



