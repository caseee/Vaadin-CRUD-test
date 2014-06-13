package it.gigalol.vaadinapp.data;

import java.io.Serializable;

import com.vaadin.ui.ComboBox;

public class LinkedComboBox implements Serializable {

	private static final long serialVersionUID = -7690045230744956750L;
	private ComboBox cb;
	private LinkedTable lt;
	public LinkedComboBox( ComboBox cb , LinkedTable lt) {
		this.cb=cb;
		this.lt=lt;
	}
	
	public ComboBox getCombobox() {
		return cb;
	}
	
	public LinkedTable getLinkedTable() {
		return lt;
	}
}
