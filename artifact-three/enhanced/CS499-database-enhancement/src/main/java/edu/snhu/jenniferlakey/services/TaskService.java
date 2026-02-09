package edu.snhu.jenniferlakey.services;

import edu.snhu.jenniferlakey.models.Task;
import edu.snhu.jenniferlakey.repository.TaskRepository;

/**
 * Service layer responsible for enforcing business rules and
 * delegating persistence operations to the TaskRepository.
 * This replaces the original in-memory ArrayList implementation
 * with a database-backed architecture.
 */

public class TaskService {

	private final TaskRepository repository;

	public TaskService() {
		this.repository = new TaskRepository();
	}

	/**
	 * Add a new task.
	 *
	 * Business rule: Task IDs must be unique.
	 *
	 * The repository now enforces uniqueness using a MongoDB unique index,
	 * so we rely on repository.insert() to throw an IllegalArgumentException
	 * if the ID already exists.
	 */
	public void addNewTask(Task task) {
		repository.insert(task);
	}

	/**
	 * Delete a task by ID.
	 *
	 * Business rule: Task must exist before deletion.
	 */
	public void deleteTask(String id) {
		Task existing = repository.findById(id);
		if (existing == null) {
			throw new IllegalArgumentException("Task not found");
		}
		repository.delete(id);
	}

	/**
	 * Update a task's fields.
	 *
	 * Business rule: Task must exist before update.
	 *
	 * The service applies updates only to non-empty fields,
	 * and the repository performs the actual partial update.
	 */
	public void updateTask(String taskID, String name, String description) {
		Task existing = repository.findById(taskID);
		if (existing == null) {
			throw new IllegalArgumentException("Task not found");
		}

		// Apply updates only if fields are non-empty
		if (name != null && !name.isEmpty()) {
			existing.setName(name);
		}
		if (description != null && !description.isEmpty()) {
			existing.setDescription(description);
		}

		repository.update(existing);
	}

	/**
	 * Retrieve a task by ID.
	 */
	public Task getTask(String id) {
		return repository.findById(id);
	}
}