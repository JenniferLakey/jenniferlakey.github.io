package edu.snhu.jenniferlakey.repository;

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
 * Integration tests for the ContactRepository class.
 *
 * These tests verify that CRUD operations correctly interact with
 * the MongoDB "contacts" collection and that Contact objects are
 * properly mapped to and from Document representations.
 */
class ContactRepositoryTest {

    private ContactRepository repository;
    private MongoCollection<Document> collection;

    @BeforeEach
    void setUp() {
        repository = new ContactRepository();
        MongoDatabase db = MongoConnection.getDatabase();
        collection = db.getCollection("contacts");

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
        Contact c = new Contact(
                "C123",
                "Imma",
                "Person",
                "1112223456",
                "123 Astreet");

        repository.insert(c);

        Contact found = repository.findById("C123");

        assertNotNull(found);
        assertEquals("C123", found.getID());
        assertEquals("Imma", found.getFirstName());
        assertEquals("Person", found.getLastName());
        assertEquals("1112223456", found.getPhone());
        assertEquals("123 Astreet", found.getAddress());
    }

    @Test
    void testFindByIdNotFound() {
        Contact found = repository.findById("DoesNotExist");
        assertNull(found);
    }

    @Test
    void testUpdateContact() {
        Contact c = new Contact(
                "C123",
                "Imma",
                "Person",
                "1112223456",
                "123 Astreet");

        repository.insert(c);

        Contact updated = new Contact(
                "C123",
                "Updated",
                "Name",
                "9998887777",
                "456 Newstreet");

        repository.update(updated);

        Contact found = repository.findById("C123");

        assertNotNull(found);
        assertEquals("Updated", found.getFirstName());
        assertEquals("Name", found.getLastName());
        assertEquals("9998887777", found.getPhone());
        assertEquals("456 Newstreet", found.getAddress());
    }

    @Test
    void testDeleteContact() {
        Contact c = new Contact(
                "C123",
                "Imma",
                "Person",
                "1112223456",
                "123 Astreet");

        repository.insert(c);

        repository.delete("C123");

        Contact found = repository.findById("C123");
        assertNull(found);
    }
}