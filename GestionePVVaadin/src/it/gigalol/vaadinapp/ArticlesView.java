package it.gigalol.vaadinapp;

import java.util.logging.Level;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class ArticlesView extends CustomComponent implements View{
	private static final long serialVersionUID = 2869411776027184262L;
	public static final String NAME = "articles";
		
	ApplicationController controller; 
	
	Table table = new Table("This is my Table");

	public ArticlesView() {
		controller = ApplicationController.getApplicationController();
		controller.getArticlesContainer();
		    	
		// Add both to a panel
		VerticalLayout fields = new VerticalLayout(table );
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
		
		table.setContainerDataSource(controller.getArticlesContainer());
	
	}
	
	

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
