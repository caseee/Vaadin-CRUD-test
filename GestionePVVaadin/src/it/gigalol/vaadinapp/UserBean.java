package it.gigalol.vaadinapp;

/**
 * Represents a User
 * @author Marco Casella 
 */
public class UserBean {
	
	public static final String NAME = "USER";
	public static final String HASH_PASSWORD = "PASS";
	public static final String LEVEL = "LEVEL";
	
	public String getName() {
		return Name;
	}
	public int getLevel() {
		return level;
	}
	private String Name;
	private int level;

	UserBean(String name, int level) {
		this.Name = name;
		this.level = level;
	}
}
