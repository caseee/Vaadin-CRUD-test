package it.gigalol.vaadinapp.sql;

import java.io.Serializable;

import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class LinkedTable implements Serializable {
	
	private static final long serialVersionUID = 3692608420961807340L;

	/**
	 * @return the idName
	 */
	public String getIdName() {
		return idName;
	}
	
	/**
	 * @return the sqlContainer
	 */
	public SQLContainer getSqlContainer() {
		return sqlContainer;
	}
	private String idName;
	private SQLContainer sqlContainer;
	private String showName;
	
	/**
	 * @return the showName
	 */
	public String getShowName() {
		return showName;
	}
	
	public LinkedTable(String idName,SQLContainer sqlContainer, String showName) {
		this.idName=idName;
		this.sqlContainer=sqlContainer;
		this.showName=showName;
	}
		
}
