package appointmentService;

import java.util.Date;

public class Appointment {
	private final String appointmentID;
	private Date appointmentDate;
	private String description;
	
	
	public Appointment (String appointmentID, Date appointmentDate, String description) {
		if (appointmentID == null || appointmentID.length()>10) {
			throw new IllegalArgumentException("Invalid appointment ID");
		}
		if (appointmentDate == null || appointmentDate.before(new Date())) {
			throw new IllegalArgumentException("Invalid appointment date");
		}
		if (description == null || description.length()>50) {
			throw new IllegalArgumentException("Invalid task description");
		}
				
		this.appointmentID = appointmentID;
		this.appointmentDate = appointmentDate;
		this.description = description;
	}
	
	public String getAppointmentID() {
		return appointmentID;
	}
	
	public Date getAppointmentDate() {
		return appointmentDate;
	}
	
	public String getDescription() {
		return description;
	}
}

