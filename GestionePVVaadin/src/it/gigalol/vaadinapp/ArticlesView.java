package it.gigalol.vaadinapp;

import java.sql.SQLException;
import java.util.logging.Level;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class ArticlesView extends CustomComponent implements View{
	private static final long serialVersionUID = 2869411776027184262L;
	public static final String NAME = "articles";
		
	ApplicationController controller; 
	
	Table table = new Table("ARTICLES:");
	Button back = new Button("Back", new Button.ClickListener() {
		private static final long serialVersionUID = 8200131706333299060L;
		@Override
		public void buttonClick(ClickEvent event) {
			getUI().getNavigator().navigateTo(MainView.NAME);
		}
	});
	Button save = new Button("Back", new Button.ClickListener() {
		private static final long serialVersionUID = 8200131706333299060L;
		@Override
		public void buttonClick(ClickEvent event) {
			try {
				controller.getArticlesContainer().commit();
			} catch (UnsupportedOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});
	public ArticlesView() {
		controller = ApplicationController.getApplicationController();
		
		controller.getArticlesContainer();
	
		HorizontalLayout buttons = new HorizontalLayout(back,save);
		VerticalLayout fields = new VerticalLayout(buttons,table);
		
		table.setSizeFull();
		setCompositionRoot(fields);
		
		table.setEditable(isEnabled());
		
		table.setContainerDataSource(controller.getArticlesContainer());
	
	}
	
	

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	

	
}
