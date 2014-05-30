package it.gigalol.vaadinapp.sql;

import java.io.Serializable;

import com.vaadin.data.util.sqlcontainer.SQLContainer;

public class LinkedTable implements Serializable {
	
	private static final long serialVersionUID = 3692608420961807340L;
	private String idName;
	private SQLContainer sqlContainer;
	private String showName;
	private Class<?> idType;
	private Class<?> showType;
	private String externalIdName;
	
	
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
	
	/**
	 * @return the showName
	 */
	public String getShowName() {
		return showName;
	}
	
	public LinkedTable(String idName, 
			SQLContainer sqlContainer, 
			String showName,
			String externalIdName,
			Class<?> internalClass , 
			Class<?> externalClass) {
		this.idName=idName;
		this.sqlContainer=sqlContainer;
		this.showName=showName;
		this.idType=internalClass;
		this.showType=externalClass;
		this.externalIdName=externalIdName;
		
	}

	public Class<?> getShowType() {
		return showType;
	}


	public Class<?> getIdType() {
		return idType;
	}

	public String getExternalIdName() {
		return externalIdName;
	}


}
