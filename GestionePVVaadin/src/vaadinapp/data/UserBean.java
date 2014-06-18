package vaadinapp.data;

import java.io.Serializable;

/**
 * Represents a User
 * @author Marco Casella 
 */
public class UserBean implements Serializable{
	private static final long serialVersionUID = -7895389242772414150L;
	public static final String USERNAME = "USERNAME";
	public static final String HASH_PASSWORD = "PASS";
	public static final String LEVEL = "LEVEL";
	public static final String SITE = "SITE";
	
	public String getName() {
		return Name;
	}
	public int getLevel() {
		return level;
	}
	private String Name;
	private int level;
	private int site;

	public UserBean(String name, int level, int site) {
		this.Name = name;
		this.level = level;
		this.site = site;
	}
	/**
	 * @return
	 */
	public int getSite() {
		return site;
	}
}
