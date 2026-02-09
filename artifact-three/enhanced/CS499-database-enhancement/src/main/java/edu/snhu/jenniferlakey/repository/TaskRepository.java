package edu.snhu.jenniferlakey.repository;

import com.mongodb.MongoWriteException;
import com.mongodb.ErrorCategory;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import edu.snhu.jenniferlakey.database.MongoConnection;
import edu.snhu.jenniferlakey.models.Task;

/**
 * Repository class responsible for performing CRUD operations
 * on Task objects using the MongoDB "tasks" collection.
 * This class handles all persistence logic and converts between
 * Task models and MongoDB Document representations.
 */

public class TaskRepository {

    private final MongoCollection<Document> collection;

    // Dedicated "tasks" collection and repo initialization,
    // repo uses shared MongoConnection singleton
    public TaskRepository() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("tasks");

        // Ensure unique index on taskID to enforce ID uniqueness
        try {
            collection.dropIndex("taskID_1");
        } catch (Exception ignored) {
            // Index didn't exist — safe to ignore
        }
        collection.createIndex(
                new Document("taskID", 1),
                new IndexOptions().unique(true));
    }

    // Helper to convert Task to Document to prepare for MongoDB storage
    private Document toDocument(Task task) {
        return new Document()
                .append("taskID", task.getTaskID())
                .append("name", task.getName())
                .append("description", task.getDescription());
    }

    // Helper to convert Document to Task
    private Task fromDocument(Document doc) {
        return new Task(
                doc.getString("taskID"),
                doc.getString("name"),
                doc.getString("description"));
    }

    // Create (insert) with duplicate ID protection
    public void insert(Task task) {
        try {
            collection.insertOne(toDocument(task));
        } catch (MongoWriteException e) {
            // Convert duplicate key error into IllegalArgumentException
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                throw new IllegalArgumentException("Task ID already exists");
            }
            throw e;
        }
    }

    // Read (find by ID)
    public Task findById(String id) {
        Document doc = collection.find(new Document("taskID", id)).first();
        return doc != null ? fromDocument(doc) : null;
    }

    // Update (only non-empty fields)
    public void update(Task task) {
        Document filter = new Document("taskID", task.getTaskID());
        Document updateFields = new Document();

        // Only update fields that are not null or empty
        if (task.getName() != null && !task.getName().isEmpty()) {
            updateFields.append("name", task.getName());
        }
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            updateFields.append("description", task.getDescription());
        }

        // If no fields were provided to update, do nothing
        if (updateFields.isEmpty())
            return;

        collection.updateOne(filter, new Document("$set", updateFields));
    }

    // Delete
    public void delete(String id) {
        collection.deleteOne(new Document("taskID", id));
    }
}