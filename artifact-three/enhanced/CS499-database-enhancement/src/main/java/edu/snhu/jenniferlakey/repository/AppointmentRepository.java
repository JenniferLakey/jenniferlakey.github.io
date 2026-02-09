package edu.snhu.jenniferlakey.repository;

import com.mongodb.MongoWriteException;
import com.mongodb.ErrorCategory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import edu.snhu.jenniferlakey.database.MongoConnection;
import edu.snhu.jenniferlakey.models.Appointment;

import java.util.Date;

/**
 * Repository class responsible for performing CRUD operations
 * on Appointment objects using the MongoDB "appointments" collection.
 * This class handles all persistence logic and converts between
 * Appointment models and MongoDB Document representations.
 */

public class AppointmentRepository {

    private final MongoCollection<Document> collection;

    // Dedicated "appointments" collection and repo initialization,
    // repo uses shared MongoConnection singleton
    public AppointmentRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("appointments");

        // Ensure unique index on appointmentID
        try {
            collection.dropIndex("appointmentID_1");
        } catch (Exception ignored) {
            // Index didn't exist — safe to ignore
        }
        collection.createIndex(
                new Document("appointmentID", 1),
                new IndexOptions().unique(true));
    }

    // Helper to convert Appointment to Document to prepare for MongoDB storage
    private Document toDocument(Appointment appointment) {
        return new Document()
                .append("appointmentID", appointment.getAppointmentID())
                .append("appointmentDate", appointment.getAppointmentDate())
                .append("description", appointment.getDescription());
    }

    // Helper to convert Document to Appointment
    private Appointment fromDocument(Document doc) {
        return new Appointment(
                doc.getString("appointmentID"),
                doc.getDate("appointmentDate"),
                doc.getString("description"));
    }

    // CRUD methods
    // Create (insert)with duplicate ID protection
    public void insert(Appointment appointment) {
        try {
            collection.insertOne(toDocument(appointment));
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                throw new IllegalArgumentException("Appointment ID already exists");
            }
            throw e;
        }
    }

    // Read (find by ID)
    public Appointment findById(String id) {
        Document doc = collection.find(new Document("appointmentID", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    // Update (only non-empty fields)
    public void update(Appointment appointment) {
        Document filter = new Document("appointmentID", appointment.getAppointmentID());
        Document updateFields = new Document();

        // Only update fields that are not null
        if (appointment.getAppointmentDate() != null) {
            updateFields.append("appointmentDate", appointment.getAppointmentDate());
        }
        if (appointment.getDescription() != null && !appointment.getDescription().isEmpty()) {
            updateFields.append("description", appointment.getDescription());
        }

        if (updateFields.isEmpty())
            return;

        collection.updateOne(filter, new Document("$set", updateFields));
    }

    // Delete
    public void delete(String id) {
        collection.deleteOne(new Document("appointmentID", id));
    }

}