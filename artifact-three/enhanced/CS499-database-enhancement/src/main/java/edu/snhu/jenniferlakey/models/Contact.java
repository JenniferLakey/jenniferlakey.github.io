package edu.snhu.jenniferlakey.models;

/**
 * Represents a contact with a unique ID, first name, last name,
 * phone number, and address.
 *
 * This class enforces strict validation rules to ensure that all
 * contact data is valid at the time of creation. These validation
 * constraints were part of the original CS-320 project and remain
 * unchanged to preserve the integrity of the original artifact.
 *
 * Validation rules:
 * ID must be non-null and no longer than 10 characters
 * firstName must be non-null and no longer than 10 characters
 * lastName must be non-null and no longer than 10 characters
 * phone must be non-null and exactly 10 digits
 * address must be non-null and no longer than 30 characters
 */

public class Contact {

	private final String ID;
	private String firstName;
	private String lastName;
	private String phone;
	private String address;

	/**
	 * Constructs a new Contact object after validating all fields.
	 *
	 * @param ID        the unique identifier for the contact
	 * @param firstName the contact's first name
	 * @param lastName  the contact's last name
	 * @param phone     the contact's phone number
	 * @param address   the contact's address
	 *
	 * @throws IllegalArgumentException if any validation rule is violated
	 */

	public Contact(String ID, String firstName, String lastName, String phone, String address) {
		if (ID == null || ID.length() > 10) {
			throw new IllegalArgumentException("Invalid ID");
		}
		if (firstName == null || firstName.length() > 10) {
			throw new IllegalArgumentException("Invalid first name");
		}
		if (lastName == null || lastName.length() > 10) {
			throw new IllegalArgumentException("Invalid last name");
		}
		if (phone == null || phone.length() != 10) {
			throw new IllegalArgumentException("Invalid phone number");
		}
		if (address == null || address.length() > 30) {
			throw new IllegalArgumentException("Invalid address");
		}

		this.ID = ID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.address = address;
	}

	// Returns the unique contact ID.
	public String getID() {
		return ID;
	}

	// Returns the contact's first name.
	public String getFirstName() {
		return firstName;
	}

	// Updates the contact's first name.
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	// Returns the contact's last name.
	public String getLastName() {
		return lastName;
	}

	// Updates the contact's last name.
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	// Returns the contact's phone number.
	public String getPhone() {
		return phone;
	}

	// Updates the contact's phone number.
	public void setPhone(String phone) {
		this.phone = phone;
	}

	// Returns the contact's address.
	public String getAddress() {
		return address;
	}

	// Updates the contact's address.
	public void setAddress(String address) {
		this.address = address;
	}
}