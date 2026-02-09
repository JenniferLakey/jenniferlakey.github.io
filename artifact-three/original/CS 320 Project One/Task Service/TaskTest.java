package taskService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

	@Test
	void testTask() {
		Task t = new Task ("ID12345", "This is the task", "Details about how to do the task.");
		assertTrue(t.getTaskID().equals("ID12345"));
		assertTrue(t.getName().equals("This is the task"));
		assertTrue(t.getDescription().equals("Details about how to do the task."));
		}
	
	@Test
	void testTaskIDTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Task ("ID1234567890", "This is the task", "Details about how to do the task.");
		}); }
	
	@Test
	void testTaskIDIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Task (null, "This is the task", "Details about how to do the task.");
		}); }
	
	@Test
	void testTaskNameTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Task ("ID12345", "This is the task too long", "Details about how to do the task.");
		}); }
	
	@Test
	void testTaskNameIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Task ("ID12345", null, "Details about how to do the task.");
		}); }
	
	@Test
	void testTaskDescriptionTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Task ("ID12345", "This is the task", "Details about how to do the task but longer than it should be.");
		}); }
	
	@Test
	void testTaskDescriptonIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Task ("ID12345", "This is the task", null);
		}); }

}
