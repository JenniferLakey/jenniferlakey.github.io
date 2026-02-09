package edu.snhu.jenniferlakey.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Contact model.
 *
 * These tests verify that the Contact constructor enforces all
 * validation rules and correctly stores valid data.
 */

class ContactTest {

	@Test
	void testValidContactCreation() {
		Contact c = new Contact(
				"ID12345",
				"Imma",
				"Person",
				"1112223456",
				"123 Astreet, Somewhere, Aplace");

		assertEquals("ID12345", c.getID());
		assertEquals("Imma", c.getFirstName());
		assertEquals("Person", c.getLastName());
		assertEquals("1112223456", c.getPhone());
		assertEquals("123 Astreet, Somewhere, Aplace", c.getAddress());
	}

	@Test
	void testContactIDTooLong() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Contact("ID123456789", "Imma", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		});
	}

	@Test
	void testContactIDIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Contact(null, "Imma", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		});
	}

	@Test
	void testContactFirstNameTooLong() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Contact("ID12345", "ImmaTooLong", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		});
	}

	@Test
	void testContactFirstNameIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Contact("ID12345", null, "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		});
	}

	@Test
	void testContactLastNameTooLong() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Contact("ID12345", "Imma", "PersonTooLong", "1112223456", "123 Astreet, Somewhere, Aplace");
		});
	}

	@Test
	void testContactLastNameIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Contact("ID12345", "Imma", null, "1112223456", "123 Astreet, Somewhere, Aplace");
		});
	}

	@Test
	void testContactPhoneNotTenDigits() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Contact("ID12345", "Imma", "Person", "11122234567", "123 Astreet, Somewhere, Aplace");
		});
	}

	@Test
	void testContactPhoneIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Contact("ID12345", "Imma", "Person", null, "123 Astreet, Somewhere, Aplace");
		});
	}

	@Test
	void testContactAddressTooLong() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Contact("ID12345", "Imma", "Person", "1112223456", "123 Somestreet, Somewhere, Aplace");
		});
	}

	@Test
	void testContactAddressIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Contact("ID12345", "Imma", "Person", "1112223456", null);
		});
	}
}