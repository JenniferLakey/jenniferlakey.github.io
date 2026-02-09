package edu.snhu.jenniferlakey.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Appointment model.
 *
 * These tests verify that the Appointment constructor enforces all
 * validation rules and correctly stores valid data.
 */

class AppointmentTest {

	// Helper to create a valid future date (avoids deprecated constructors)
	private Date futureDate() {
		return new Date(System.currentTimeMillis() + 86400000); // +1 day
	}

	@Test
	void testValidAppointmentCreation() {
		Date date = futureDate();
		Appointment a = new Appointment("ID12345", date, "Details about the appointment.");

		assertEquals("ID12345", a.getAppointmentID());
		assertEquals(date, a.getAppointmentDate());
		assertEquals("Details about the appointment.", a.getDescription());
	}

	@Test
	void testAppointmentIDTooLong() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Appointment("ID1234567890", futureDate(), "Valid description");
		});
	}

	@Test
	void testAppointmentIDIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Appointment(null, futureDate(), "Valid description");
		});
	}

	@Test
	void testAppointmentDateInPast() {
		Date pastDate = new Date(System.currentTimeMillis() - 86400000); // -1 day

		assertThrows(IllegalArgumentException.class, () -> {
			new Appointment("ID12345", pastDate, "Valid description");
		});
	}

	@Test
	void testAppointmentDateIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Appointment("ID12345", null, "Valid description");
		});
	}

	@Test
	void testAppointmentDescriptionTooLong() {
		String longDescription = "This description is intentionally made longer than fifty characters to trigger validation.";

		assertThrows(IllegalArgumentException.class, () -> {
			new Appointment("ID12345", futureDate(), longDescription);
		});
	}

	@Test
	void testAppointmentDescriptionIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Appointment("ID12345", futureDate(), null);
		});
	}
}