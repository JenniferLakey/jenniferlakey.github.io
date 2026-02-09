package edu.snhu.jenniferlakey.repository;

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
 * Integration tests for the AppointmentRepository class.
 *
 * These tests verify that CRUD operations correctly interact with
 * the MongoDB "appointments" collection and that Appointment objects
 * are properly mapped to and from Document representations.
 */

class AppointmentRepositoryTest {

    private AppointmentRepository repository;
    private MongoCollection<Document> collection;

    // Helper to generate a valid future date
    private Date futureDate() {
        return new Date(System.currentTimeMillis() + 86400000); // +1 day
    }

    @BeforeEach
    void setUp() {
        repository = new AppointmentRepository();
        MongoDatabase db = MongoConnection.getDatabase();
        collection = db.getCollection("appointments");

        // Clean the collection before each test
        collection.deleteMany(new Document());
    }

    @AfterEach
    void tearDown() {
        // Clean up and close the connection
        collection.deleteMany(new Document());
        MongoConnection.close();
    }

    @Test
    void testInsertAndFindById() {
        Appointment a = new Appointment("A123", futureDate(), "Test appointment");
        repository.insert(a);

        Appointment found = repository.findById("A123");

        assertNotNull(found);
        assertEquals("A123", found.getAppointmentID());
        assertEquals(a.getAppointmentDate(), found.getAppointmentDate());
        assertEquals("Test appointment", found.getDescription());
    }

    @Test
    void testFindByIdNotFound() {
        Appointment found = repository.findById("DoesNotExist");
        assertNull(found);
    }

    @Test
    void testUpdateAppointment() {
        Appointment a = new Appointment("A123", futureDate(), "Original description");
        repository.insert(a);

        Appointment updated = new Appointment("A123", futureDate(), "Updated description");
        repository.update(updated);

        Appointment found = repository.findById("A123");

        assertNotNull(found);
        assertEquals("Updated description", found.getDescription());
    }

    @Test
    void testDeleteAppointment() {
        Appointment a = new Appointment("A123", futureDate(), "To be deleted");
        repository.insert(a);

        repository.delete("A123");

        Appointment found = repository.findById("A123");
        assertNull(found);
    }
}