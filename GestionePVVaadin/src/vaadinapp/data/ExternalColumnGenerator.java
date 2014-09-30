/**
 * ExternalColumnGenerator.java
 */
package vaadinapp.data;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;

/**
 * @author Marco Casella
 *
 */
public class ExternalColumnGenerator implements  ColumnGenerator {
	
		private static final long serialVersionUID = -2367424099450361243L;
		
		/**
		 * @param table
		 * @param originName
		 * @param originContainer
		 * @param destinationName
		 * @param destinationContainer
		 */
		public ExternalColumnGenerator(Table table, String originName,
				SQLContainer originContainer, String destinationName,
				SQLContainer destinationContainer) {
			super();
			this.table = table;
			this.originName = originName;
			this.originContainer = originContainer;
			this.destinationName = destinationName;
			this.destinationContainer = destinationContainer;
		}

		private Table table;
		private String originName;
		private SQLContainer originContainer;
		private String destinationName;
		private SQLContainer destinationContainer;

		public Component generateCell(Table source, Object itemId, Object columnId) {
			if (table.getItem(itemId).getItemProperty(originName).getValue() == null) 
				return null;
			// Convert internal id property to external show property 
			Label l = new Label();
			// Retrieve the item in the external table
			Item item = originContainer.getReferencedItem(itemId, destinationContainer);
			// Get the property used to show item
			Property<?> property = item.getItemProperty(destinationName); 
			l.setValue(property.getValue().toString());
			l.setSizeUndefined();
			return l;
		}
	}
