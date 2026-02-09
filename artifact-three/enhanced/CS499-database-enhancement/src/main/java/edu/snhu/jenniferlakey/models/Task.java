package edu.snhu.jenniferlakey.models;

/**
 * Represents a task with a unique ID, name, and description.
 *
 * This class enforces strict validation rules to ensure that all task
 * data is valid at the time of creation. These validation constraints
 * were part of the original CS-320 project and remain unchanged to
 * preserve the integrity of the original artifact.
 *
 * Validation rules:
 *
 * taskID must be non-null and no longer than 10 characters
 * name must be non-null and no longer than 20 characters
 * description must be non-null and no longer than 50 characters
 */

public class Task {

	private final String taskID;
	private String name;
	private String description;

	/**
	 * Constructs a new Task object after validating all fields.
	 *
	 * @param taskID      the unique identifier for the task
	 * @param name        the task name
	 * @param description a brief description of the task
	 *
	 * @throws IllegalArgumentException if any validation rule is violated
	 */

	public Task(String taskID, String name, String description) {
		if (taskID == null || taskID.length() > 10) {
			throw new IllegalArgumentException("Invalid task ID");
		}
		if (name == null || name.length() > 20) {
			throw new IllegalArgumentException("Invalid task name");
		}
		if (description == null || description.length() > 50) {
			throw new IllegalArgumentException("Invalid task description");
		}

		this.taskID = taskID;
		this.name = name;
		this.description = description;
	}

	// Returns the unique task ID.
	public String getTaskID() {
		return taskID;
	}

	// Returns the task name.
	public String getName() {
		return name;
	}

	// Updates the task name.
	public void setName(String name) {
		this.name = name;
	}

	// Returns the task description.
	public String getDescription() {
		return description;
	}

	// Updates the task description.
	public void setDescription(String description) {
		this.description = description;
	}
}