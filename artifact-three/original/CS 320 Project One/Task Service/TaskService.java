package taskService;
import java.util.ArrayList;
import java.util.List;


public class TaskService {
	List <Task> taskList = new ArrayList<>();
	
	public void addNewTask(Task t) throws IllegalArgumentException {
		if (isUnique(t.getTaskID())) {
           taskList.add(t);
        } else {
            throw new IllegalArgumentException("Task ID must be unique!");
        }
    }
	
	private boolean isUnique(String id) {
        for (Task t: taskList) { // Check uniqueness
        	if(t.getTaskID().equals(id)) {
        		return false;
        	}
        }
        return true;
    }
	
    public void deleteTask(String id) throws IllegalArgumentException{
    	boolean notContained = taskList.stream().noneMatch(obj -> obj.getTaskID() == id);
    	if (notContained){
			throw new IllegalArgumentException("Task not found");
		}
    	Task temp = null;
    	for (Task t: taskList) {
    		if(t.getTaskID().equals(id)) {
    			temp = t;
    		}
    	}
    	taskList.remove(temp);
    }
    
    public void updateTask(String taskID, String name, String description) throws IllegalArgumentException{
    	boolean notContained = taskList.stream().noneMatch(obj -> obj.getTaskID() == taskID);
    	if (notContained){
			throw new IllegalArgumentException("Task not found");
		}
    	for (Task t: taskList) {
    		if(t.getTaskID().equals(taskID)) {
    			if (!name.isEmpty()) {
    				t.setName(name);
    			}
    			if (!description.isEmpty()) {
    				t.setDescription(description);
    			}
    	    }
    	}	
    }
}
