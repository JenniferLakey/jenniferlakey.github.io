package edu.snhu.jenniferlakey.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import edu.snhu.jenniferlakey.database.MongoConnection;
import edu.snhu.jenniferlakey.models.Contact;

/**
 * Integration tests for the ContactService class.
 *
 * These tests verify that business rules (uniqueness, existence checks,
 * and field-by-field updates) are enforced correctly while persistence
 * operations are delegated to the ContactRepository and MongoDB backend.
 */

class ContactServiceTest {

	private ContactService service;
	private MongoCollection<Document> collection;

	@BeforeEach
	void setUp() {
		service = new ContactService();

		MongoDatabase db = MongoConnection.getDatabase();
		collection = db.getCollection("contacts");

		// Clean the collection before each test
		collection.deleteMany(new Document());
	}

	@AfterEach
	void tearDown() {
		collection.deleteMany(new Document());
		MongoConnection.close();
	}

	@Test
	void testAddNewContact() {
		Contact c = new Contact(
				"C123",
				"Imma",
				"Person",
				"1112223456",
				"123 Astreet");

		service.addNewContact(c);

		Contact found = service.getContact("C123");

		assertNotNull(found);
		assertEquals("C123", found.getID());
		assertEquals("Imma", found.getFirstName());
		assertEquals("Person", found.getLastName());
		assertEquals("1112223456", found.getPhone());
		assertEquals("123 Astreet", found.getAddress());
	}

	@Test
	void testAddNewContactIDNotUnique() {
		Contact first = new Contact(
				"C123",
				"Imma",
				"Person",
				"1112223456",
				"123 Astreet");

		Contact duplicate = new Contact(
				"C123",
				"Other",
				"Name",
				"9998887777",
				"456 Newstreet");

		service.addNewContact(first);

		assertThrows(IllegalArgumentException.class, () -> {
			service.addNewContact(duplicate);
		});
	}

	@Test
	void testDeleteContact() {
		Contact c = new Contact(
				"C123",
				"Imma",
				"Person",
				"1112223456",
				"123 Astreet");

		service.addNewContact(c);

		service.deleteContact("C123");

		Contact found = service.getContact("C123");
		assertNull(found);
	}

	@Test
	void testDeleteContactNotFound() {
		assertThrows(IllegalArgumentException.class, () -> {
			service.deleteContact("DoesNotExist");
		});
	}

	@Test
	void testUpdateContact() {
		Contact c = new Contact(
				"C123",
				"Imma",
				"Person",
				"1112223456",
				"123 Astreet");

		service.addNewContact(c);

		// Update first name only
		service.updateContact("C123", "Emma", "", "", "");
		Contact updated1 = service.getContact("C123");
		assertEquals("Emma", updated1.getFirstName());
		assertEquals("Person", updated1.getLastName());
		assertEquals("1112223456", updated1.getPhone());
		assertEquals("123 Astreet", updated1.getAddress());

		// Update last name only
		service.updateContact("C123", "", "Nugget", "", "");
		Contact updated2 = service.getContact("C123");
		assertEquals("Emma", updated2.getFirstName());
		assertEquals("Nugget", updated2.getLastName());

		// Update phone only
		service.updateContact("C123", "", "", "1234567890", "");
		Contact updated3 = service.getContact("C123");
		assertEquals("1234567890", updated3.getPhone());

		// Update address only
		service.updateContact("C123", "", "", "", "New Address");
		Contact updated4 = service.getContact("C123");
		assertEquals("New Address", updated4.getAddress());
	}

	@Test
	void testUpdateContactNotFound() {
		assertThrows(IllegalArgumentException.class, () -> {
			service.updateContact("DoesNotExist", "Emma", "", "", "");
		});
	}
}