package edu.snhu.jenniferlakey.repository;

import com.mongodb.MongoWriteException;
import com.mongodb.ErrorCategory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import edu.snhu.jenniferlakey.database.MongoConnection;
import edu.snhu.jenniferlakey.models.Contact;

/**
 * Repository class responsible for performing CRUD operations
 * on Contact objects using the MongoDB "contacts" collection.
 * This class handles all persistence logic and converts between
 * Contact models and MongoDB Document representations.
 */

public class ContactRepository {

    private final MongoCollection<Document> collection;

    // Dedicated "contacts" collection and repo initialization,
    // repo uses shared MongoConnection singleton
    public ContactRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("contacts");

        // Ensure unique index on contactID to enforce ID uniqueness
        try {
            collection.dropIndex("contactID_1");
        } catch (Exception ignored) {
            // Index didn't exist — safe to ignore
        }
        collection.createIndex(
                new Document("contactID", 1),
                new IndexOptions().unique(true));
    }

    // Helper to convert Contact to Document to prepare for MongoDB storage
    private Document toDocument(Contact contact) {
        return new Document()
                .append("contactID", contact.getID())
                .append("firstName", contact.getFirstName())
                .append("lastName", contact.getLastName())
                .append("phone", contact.getPhone())
                .append("address", contact.getAddress());
    }

    // Helper to convert Document to Contact
    private Contact fromDocument(Document doc) {
        return new Contact(
                doc.getString("contactID"),
                doc.getString("firstName"),
                doc.getString("lastName"),
                doc.getString("phone"),
                doc.getString("address"));
    }

    // Create (insert) with duplicate ID protection
    public void insert(Contact contact) {
        try {
            collection.insertOne(toDocument(contact));
        } catch (MongoWriteException e) {
            // Convert duplicate key error into IllegalArgumentException
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                throw new IllegalArgumentException("Contact ID already exists");
            }
            throw e;
        }
    }

    // Read (find by ID)
    public Contact findById(String id) {
        Document doc = collection.find(new Document("contactID", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    // Update (only non-empty fields)
    public void update(Contact contact) {
        Document filter = new Document("contactID", contact.getID());
        Document updateFields = new Document();

        // Only update fields that are not null or empty
        if (contact.getFirstName() != null && !contact.getFirstName().isEmpty()) {
            updateFields.append("firstName", contact.getFirstName());
        }
        if (contact.getLastName() != null && !contact.getLastName().isEmpty()) {
            updateFields.append("lastName", contact.getLastName());
        }
        if (contact.getPhone() != null && !contact.getPhone().isEmpty()) {
            updateFields.append("phone", contact.getPhone());
        }
        if (contact.getAddress() != null && !contact.getAddress().isEmpty()) {
            updateFields.append("address", contact.getAddress());
        }

        // If no fields were provided to update, do nothing
        if (updateFields.isEmpty())
            return;

        collection.updateOne(filter, new Document("$set", updateFields));
    }

    // Delete
    public void delete(String id) {
        collection.deleteOne(new Document("contactID", id));
    }
}