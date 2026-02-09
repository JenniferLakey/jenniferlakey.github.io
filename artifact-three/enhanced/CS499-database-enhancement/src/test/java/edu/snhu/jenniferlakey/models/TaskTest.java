package edu.snhu.jenniferlakey.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Task model.
 *
 * These tests verify that the Task constructor enforces all
 * validation rules and correctly stores valid data.
 */

class TaskTest {

	@Test
	void testValidTaskCreation() {
		Task t = new Task(
				"ID12345",
				"This is the task",
				"Details about how to do the task.");

		assertEquals("ID12345", t.getTaskID());
		assertEquals("This is the task", t.getName());
		assertEquals("Details about how to do the task.", t.getDescription());
	}

	@Test
	void testTaskIDTooLong() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Task("ID1234567890", "This is the task", "Details about how to do the task.");
		});
	}

	@Test
	void testTaskIDIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Task(null, "This is the task", "Details about how to do the task.");
		});
	}

	@Test
	void testTaskNameTooLong() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Task("ID12345", "This is the task too long", "Details about how to do the task.");
		});
	}

	@Test
	void testTaskNameIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Task("ID12345", null, "Details about how to do the task.");
		});
	}

	@Test
	void testTaskDescriptionTooLong() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Task("ID12345", "This is the task", "Details about how to do the task but longer than it should be.");
		});
	}

	@Test
	void testTaskDescriptionIsNull() {
		assertThrows(IllegalArgumentException.class, () -> {
			new Task("ID12345", "This is the task", null);
		});
	}
}