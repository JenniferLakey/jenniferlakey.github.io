package taskService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskServiceTest {

	@Test
	void testTaskServiceAddNewTask() {
		TaskService service = new TaskService();
		Task t = new Task ("ID12345", "This is the task", "Details about how to do the task.");
		service.addNewTask(t);
		assertTrue(service.taskList.contains(t));
		assertTrue(t.getTaskID().equals("ID12345"));
		assertTrue(t.getName().equals("This is the task"));
		assertTrue(t.getDescription().equals("Details about how to do the task."));
	}
	
	@Test
	void testTaskServiceAddNewTaskIDNotUnique() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			TaskService service = new TaskService();
		Task t = new Task ("ID12345", "This is the task", "Details about how to do the task.");
		Task n = new Task ("ID012345", "This is the task", "Details about how to do the task.");
			service.addNewTask(n);
			service.addNewTask(t);
			service.addNewTask(t);
		}); }
	
	@Test
	void testTaskServiceDeleteTask() {
		TaskService service = new TaskService();
		Task t = new Task ("ID12345", "This is the task", "Details about how to do the task.");
		Task n = new Task ("ID012345", "This is the task", "Details about how to do the task.");
		service.addNewTask(n);
		service.addNewTask(t);
		assertTrue(service.taskList.contains(t));
		assertTrue(service.taskList.contains(n));
		service.deleteTask("ID12345");
		assertTrue(!service.taskList.contains(t));
	}
	
	@Test
	void testTaskServiceDeleteTaskTaskNotFound() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			TaskService service = new TaskService();
			service.deleteTask("ID12345");
		}); }
	
	@Test
	void testTaskServiceUpdateTask() {
		TaskService service = new TaskService();
		Task n = new Task ("ID012345", "This is the task", "Details about how to do the task.");
		service.addNewTask(n);
		Task t = new Task ("ID12345", "This is the task", "Details about how to do the task.");
		service.addNewTask(t);
		assertTrue(service.taskList.contains(t));
		service.updateTask("ID12345", "Different name", "");
		assertTrue(service.taskList.contains(t));
		assertTrue(t.getTaskID().equals("ID12345"));
		assertTrue(t.getName().equals("Different name"));
		assertTrue(t.getDescription().equals("Details about how to do the task."));
		service.updateTask("ID12345", "This is the task", "Description change to something else");
		assertTrue(service.taskList.contains(t));
		assertTrue(t.getTaskID().equals("ID12345"));
		assertTrue(t.getName().equals("This is the task"));
		assertTrue(t.getDescription().equals("Description change to something else"));
		service.updateTask("ID12345", "", "Details about how to do the task.");
		assertTrue(service.taskList.contains(t));
		assertTrue(t.getTaskID().equals("ID12345"));
		assertTrue(t.getName().equals("This is the task"));
		assertTrue(t.getDescription().equals("Details about how to do the task."));
	}
	
	@Test
	void testTaskServiceUpdateTaskTaskNotFound() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			TaskService service = new TaskService();
			service.updateTask("ID12345", "This is the task", "Details about how to do the task.");
		}); }
	
}

