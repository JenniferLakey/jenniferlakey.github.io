package edu.snhu.jenniferlakey.services;

import edu.snhu.jenniferlakey.models.Contact;
import edu.snhu.jenniferlakey.repository.ContactRepository;

/**
 * Service layer responsible for enforcing business rules and
 * delegating persistence operations to the ContactRepository.
 * This replaces the original in-memory ArrayList implementation
 * with a database-backed architecture.
 */

public class ContactService {

	private final ContactRepository repository;

	public ContactService() {
		this.repository = new ContactRepository();
	}

	/**
	 * Add a new contact.
	 *
	 * Business rule: Contact IDs must be unique.
	 *
	 * The repository now enforces uniqueness using a MongoDB unique index,
	 * so we rely on repository.insert() to throw an IllegalArgumentException
	 * if the ID already exists.
	 */
	public void addNewContact(Contact contact) {
		repository.insert(contact);
	}

	/**
	 * Delete a contact by ID.
	 *
	 * Business rule: Contact must exist before deletion.
	 */
	public void deleteContact(String id) {
		Contact existing = repository.findById(id);
		if (existing == null) {
			throw new IllegalArgumentException("Contact not found");
		}
		repository.delete(id);
	}

	/**
	 * Update a contact's fields.
	 *
	 * Business rule: Contact must exist before update.
	 *
	 * The service applies updates only to non-empty fields,
	 * and the repository performs the actual partial update.
	 */
	public void updateContact(String id, String firstName, String lastName, String phone, String address) {
		Contact existing = repository.findById(id);
		if (existing == null) {
			throw new IllegalArgumentException("Contact not found");
		}

		// Apply updates only if fields are non-empty
		if (firstName != null && !firstName.isEmpty()) {
			existing.setFirstName(firstName);
		}
		if (lastName != null && !lastName.isEmpty()) {
			existing.setLastName(lastName);
		}
		if (phone != null && !phone.isEmpty()) {
			existing.setPhone(phone);
		}
		if (address != null && !address.isEmpty()) {
			existing.setAddress(address);
		}

		repository.update(existing);
	}

	/**
	 * Retrieve a contact by ID.
	 */
	public Contact getContact(String id) {
		return repository.findById(id);
	}
}