package edu.snhu.jenniferlakey.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import edu.snhu.jenniferlakey.database.MongoConnection;
import edu.snhu.jenniferlakey.models.Appointment;

/**
 * Integration tests for the AppointmentService class.
 *
 * These tests verify that business rules (uniqueness, existence checks)
 * are enforced correctly while persistence operations are delegated to
 * the AppointmentRepository and MongoDB backend.
 */

class AppointmentServiceTest {

	private AppointmentService service;
	private MongoCollection<Document> collection;

	// Helper to generate a valid future date
	private Date futureDate() {
		return new Date(System.currentTimeMillis() + 86400000); // +1 day
	}

	@BeforeEach
	void setUp() {
		service = new AppointmentService();

		MongoDatabase db = MongoConnection.getDatabase();
		collection = db.getCollection("appointments");

		// Clean the collection before each test
		collection.deleteMany(new Document());
	}

	@AfterEach
	void tearDown() {
		collection.deleteMany(new Document());
		MongoConnection.close();
	}

	@Test
	void testAddNewAppointment() {
		Appointment a = new Appointment("A123", futureDate(), "Test appointment");
		service.addNewAppointment(a);

		Appointment found = service.getAppointment("A123");

		assertNotNull(found);
		assertEquals("A123", found.getAppointmentID());
		assertEquals(a.getAppointmentDate(), found.getAppointmentDate());
		assertEquals("Test appointment", found.getDescription());
	}

	@Test
	void testAddNewAppointmentIDNotUnique() {
		Appointment first = new Appointment("A123", futureDate(), "First");
		Appointment duplicate = new Appointment("A123", futureDate(), "Duplicate");

		service.addNewAppointment(first);

		assertThrows(IllegalArgumentException.class, () -> {
			service.addNewAppointment(duplicate);
		});
	}

	@Test
	void testDeleteAppointment() {
		Appointment a = new Appointment("A123", futureDate(), "To delete");
		service.addNewAppointment(a);

		service.deleteAppointment("A123");

		Appointment found = service.getAppointment("A123");
		assertNull(found);
	}

	@Test
	void testDeleteAppointmentNotFound() {
		assertThrows(IllegalArgumentException.class, () -> {
			service.deleteAppointment("DoesNotExist");
		});
	}
}