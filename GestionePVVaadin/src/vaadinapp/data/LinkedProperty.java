package vaadinapp.data;

import java.io.Serializable;

import com.vaadin.data.util.sqlcontainer.SQLContainer;

/**
 * Represents a Property stored in another table (SQLContainer) with a foreign key references
 * @author Marco Casella
 *
 */
public class LinkedProperty implements Serializable {
	
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
	
	/**
	 * @param idName property name of the source id (foreign key)
	 * @param sqlContainer sql contanier of the destination table
	 * @param showName property name of the destination table to show
	 * @param externalIdName property name of the destination table key (primary key or candidate)
	 * @param internalClass 
	 * @param externalClass
	 */
	public LinkedProperty(String idName, 
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

	/**
	 * @return type of property to be displayed 
	 */
	public Class<?> getShowType() {
		return showType;
	}

	/**
	 * @return type of key and foreign key property
	 */
	public Class<?> getIdType() {
		return idType;
	}

	/**
	 * @return name of the property of the foreign key
	 */
	public String getExternalIdName() {
		return externalIdName;
	}


}
