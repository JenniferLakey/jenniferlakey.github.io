package edu.snhu.jenniferlakey.services;

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
 * Integration tests for the TaskService class.
 *
 * These tests verify that business rules (uniqueness, existence checks,
 * and field-by-field updates) are enforced correctly while persistence
 * operations are delegated to the TaskRepository and MongoDB backend.
 */

class TaskServiceTest {

	private TaskService service;
	private MongoCollection<Document> collection;

	@BeforeEach
	void setUp() {
		service = new TaskService();

		MongoDatabase db = MongoConnection.getDatabase();
		collection = db.getCollection("tasks");

		// Clean the collection before each test
		collection.deleteMany(new Document());
	}

	@AfterEach
	void tearDown() {
		collection.deleteMany(new Document());
		MongoConnection.close();
	}

	@Test
	void testAddNewTask() {
		Task t = new Task(
				"T123",
				"This is the task",
				"Details about how to do the task.");

		service.addNewTask(t);

		Task found = service.getTask("T123");

		assertNotNull(found);
		assertEquals("T123", found.getTaskID());
		assertEquals("This is the task", found.getName());
		assertEquals("Details about how to do the task.", found.getDescription());
	}

	@Test
	void testAddNewTaskIDNotUnique() {
		Task first = new Task(
				"T123",
				"This is the task",
				"Details about how to do the task.");

		Task duplicate = new Task(
				"T123",
				"Another task",
				"Another description");

		service.addNewTask(first);

		assertThrows(IllegalArgumentException.class, () -> {
			service.addNewTask(duplicate);
		});
	}

	@Test
	void testDeleteTask() {
		Task t = new Task(
				"T123",
				"This is the task",
				"Details about how to do the task.");

		service.addNewTask(t);

		service.deleteTask("T123");

		Task found = service.getTask("T123");
		assertNull(found);
	}

	@Test
	void testDeleteTaskNotFound() {
		assertThrows(IllegalArgumentException.class, () -> {
			service.deleteTask("DoesNotExist");
		});
	}

	@Test
	void testUpdateTask() {
		Task t = new Task(
				"T123",
				"Original Name",
				"Original Description");

		service.addNewTask(t);

		// Update name only
		service.updateTask("T123", "Updated Name", "");
		Task updated1 = service.getTask("T123");
		assertEquals("Updated Name", updated1.getName());
		assertEquals("Original Description", updated1.getDescription());

		// Update description only
		service.updateTask("T123", "", "Updated Description");
		Task updated2 = service.getTask("T123");
		assertEquals("Updated Name", updated2.getName());
		assertEquals("Updated Description", updated2.getDescription());

		// Update both
		service.updateTask("T123", "Final Name", "Final Description");
		Task updated3 = service.getTask("T123");
		assertEquals("Final Name", updated3.getName());
		assertEquals("Final Description", updated3.getDescription());
	}

	@Test
	void testUpdateTaskNotFound() {
		assertThrows(IllegalArgumentException.class, () -> {
			service.updateTask("DoesNotExist", "Name", "Description");
		});
	}
}