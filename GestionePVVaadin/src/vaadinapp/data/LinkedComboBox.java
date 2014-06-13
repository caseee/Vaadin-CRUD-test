package vaadinapp.data;

import java.io.Serializable;

import com.vaadin.ui.ComboBox;

public class LinkedComboBox implements Serializable {

	private static final long serialVersionUID = -7690045230744956750L;
	private ComboBox cb;
	private LinkedProperty lt;
	public LinkedComboBox( ComboBox cb , LinkedProperty lt) {
		this.cb=cb;
		this.lt=lt;
	}
	
	public ComboBox getCombobox() {
		return cb;
	}
	
	public LinkedProperty getLinkedTable() {
		return lt;
	}
}
