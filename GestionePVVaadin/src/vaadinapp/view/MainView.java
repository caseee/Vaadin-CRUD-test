package vaadinapp.view;

import java.io.File;

import vaadinapp.AppView;
import vaadinapp.Controller;

import com.vaadin.navigator.*;
import com.vaadin.navigator.ViewChangeListener.*;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

public class MainView extends CustomComponent implements View {

	private static final long serialVersionUID = -6734826951324775495L;
	public static final String NAME = "";
	private static final String COMPONENT_WIDTH = "800px";
	private final Image img; 
	Label text = new Label();

	private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	FileResource resource = new FileResource(new File(basepath +"/WEB-INF/resources/img/main.png"));

	Button logout = new Button("Logout", new Button.ClickListener() {

		private static final long serialVersionUID = -9081842014270147559L;

		@Override
		public void buttonClick(ClickEvent event) {
			// logout
			getSession().getAttribute(Controller.class).logout();
			// redirect to log-in view
			getUI().getNavigator().navigateTo(NAME);
		}
	});

	public MainView() {
		setSizeFull();

		// Add both to a panel
		GridLayout grid = new GridLayout(4, 4); 

		for (AppView view : VaadinSession.getCurrent().getAttribute(Controller.class).getViews()) {
			final AppView finalView = view;
			if (view.getLevelRequired() <= VaadinSession.getCurrent().getAttribute(Controller.class).getLoggedUser().getLevel() )
			{
				Button btn = new Button(view.getViewName(), new Button.ClickListener() {

					private static final long serialVersionUID = 15619813541891L;

					@Override
					public void buttonClick(ClickEvent event) {
						getUI().getNavigator().navigateTo(finalView.getViewName());
					}


				});
				btn.setWidth("180px");
				btn.setWidth("180px");
				grid.addComponent(btn);
			}
		}


		img = new Image("Main Menu", resource);
		img.setWidth("256px");
		logout.setWidth("180px");
		logout.setWidth("180px");
		grid.addComponent(logout);	

		grid.setSpacing(true);
		grid.setMargin(new MarginInfo(true, true, true, false));
		grid.setSizeUndefined();

		// The view root layout
		VerticalLayout viewLayout = new VerticalLayout(img,grid);
		viewLayout.setSizeFull();
		viewLayout.setComponentAlignment(img, Alignment.TOP_CENTER);
		viewLayout.setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
		viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);
		setCompositionRoot(viewLayout);

	}

	@Override
	public void enter(ViewChangeEvent event) {


	}


}



