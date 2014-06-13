/**
 * UserLevel.java
 */
package vaadinapp.data;

/**
 * @author Marco Casella
 *
 */
public enum UserLevel {
	
	Customer (1),
	RestrictedSeller (5),
	Seller (10),
	StoreManager (15),
	Manager (20),
	Owner (25),
	Admin (99);
	
    private int value;

    private UserLevel(int value) {
            this.setValue(value);
    }

	public int getValue() {
		return value;
	}

	private void setValue(int value) {
		this.value = value;
	}


}
