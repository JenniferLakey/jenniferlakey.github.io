package taskService;

public class Task {
	private final String taskID;
	private String name;
	private String description;
	
	
	public Task (String taskID, String name, String description) {
		if (taskID == null || taskID.length()>10) {
			throw new IllegalArgumentException("Invalid task ID");
		}
		if (name == null || name.length()>20) {
			throw new IllegalArgumentException("Invalid task name");
		}
		if (description == null || description.length()>50) {
			throw new IllegalArgumentException("Invalid task description");
		}
				
		this.taskID = taskID;
		this.name = name;
		this.description = description;
	}
	
	public String getTaskID() {
		return taskID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
