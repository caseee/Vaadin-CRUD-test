package it.gigalol.vaadinapp;

public class Controller {
	private Model model = Model.getModel();	
	
	public boolean auth(String user, String pass, int levelreq) {
		return model.auth(user, pass, levelreq);
	}
	
}
