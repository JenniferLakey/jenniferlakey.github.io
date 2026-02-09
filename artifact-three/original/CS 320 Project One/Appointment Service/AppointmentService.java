package appointmentService;

import java.util.ArrayList;
import java.util.List;


public class AppointmentService {
	List <Appointment> appointmentList = new ArrayList<>();
	
	public void addNewAppointment(Appointment a) throws IllegalArgumentException {
		if (isUnique(a.getAppointmentID())) {
           appointmentList.add(a);
        } else {
            throw new IllegalArgumentException("Appointment ID must be unique!");
        }
    }
	
	private boolean isUnique(String id) {
        for (Appointment a: appointmentList) { // Check uniqueness
        	if(a.getAppointmentID().equals(id)) {
        		return false;
        	}
        }
        return true;
    }
	
    public void deleteAppointment(String id) throws IllegalArgumentException{
    	boolean notContained = appointmentList.stream().noneMatch(obj -> obj.getAppointmentID() == id);
    	if (notContained){
			throw new IllegalArgumentException("Appointment not found");
		}
    	Appointment temp = null;
    	for (Appointment a: appointmentList) {
    		if(a.getAppointmentID().equals(id)) {
    			temp = a;
    		}
    	}
    	appointmentList.remove(temp);
    }
    		
}

