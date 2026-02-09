package edu.snhu.jenniferlakey.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import edu.snhu.jenniferlakey.database.MongoConnection;
import edu.snhu.jenniferlakey.models.Task;

/**
 * Integration tests for the TaskRepository class.
 *
 * These tests verify that CRUD operations correctly interact with
 * the MongoDB "tasks" collection and that Task objects are properly
 * mapped to and from Document representations.
 */

class TaskRepositoryTest {

    private TaskRepository repository;
    private MongoCollection<Document> collection;

    @BeforeEach
    void setUp() {
        repository = new TaskRepository();
        MongoDatabase db = MongoConnection.getDatabase();
        collection = db.getCollection("tasks");

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
        Task t = new Task(
                "T123",
                "Test Task",
                "This is a test task.");

        repository.insert(t);

        Task found = repository.findById("T123");

        assertNotNull(found);
        assertEquals("T123", found.getTaskID());
        assertEquals("Test Task", found.getName());
        assertEquals("This is a test task.", found.getDescription());
    }

    @Test
    void testFindByIdNotFound() {
        Task found = repository.findById("DoesNotExist");
        assertNull(found);
    }

    @Test
    void testUpdateTask() {
        Task t = new Task(
                "T123",
                "Original Task",
                "Original description.");

        repository.insert(t);

        Task updated = new Task(
                "T123",
                "Updated Task",
                "Updated description.");

        repository.update(updated);

        Task found = repository.findById("T123");

        assertNotNull(found);
        assertEquals("Updated Task", found.getName());
        assertEquals("Updated description.", found.getDescription());
    }

    @Test
    void testDeleteTask() {
        Task t = new Task(
                "T123",
                "Task to Delete",
                "This task will be deleted.");

        repository.insert(t);

        repository.delete("T123");

        Task found = repository.findById("T123");
        assertNull(found);
    }
}