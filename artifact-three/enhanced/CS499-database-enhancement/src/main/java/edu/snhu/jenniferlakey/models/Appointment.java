package edu.snhu.jenniferlakey.models;

import java.util.Date;

/**
 * Represents an appointment with a unique ID, scheduled date, and description.
 *
 * This class enforces strict validation rules to ensure that all appointment
 * data is valid at the time of creation. These validation constraints were part
 * of the original CS-320 project and remain unchanged to preserve the integrity
 * of the original artifact.
 *
 * Validation rules:
 * appointmentID must be non-null and no longer than 10 characters
 * appointmentDate must be non-null and must not occur in the past
 * description must be non-null and no longer than 50 characters
 */

public class Appointment {

	private final String appointmentID;
	private Date appointmentDate;
	private String description;

	/**
	 * Constructs a new Appointment object after validating all fields.
	 *
	 * @param appointmentID   the unique identifier for the appointment
	 * @param appointmentDate the scheduled date of the appointment
	 * @param description     a brief description of the appointment
	 *
	 * @throws IllegalArgumentException if any validation rule is violated
	 */

	public Appointment(String appointmentID, Date appointmentDate, String description) {
		if (appointmentID == null || appointmentID.length() > 10) {
			throw new IllegalArgumentException("Invalid appointment ID");
		}
		if (appointmentDate == null || appointmentDate.before(new Date())) {
			throw new IllegalArgumentException("Invalid appointment date");
		}
		if (description == null || description.length() > 50) {
			throw new IllegalArgumentException("Invalid task description");
		}

		this.appointmentID = appointmentID;
		this.appointmentDate = appointmentDate;
		this.description = description;
	}

	// Returns the unique appointment ID.
	public String getAppointmentID() {
		return appointmentID;
	}

	// Returns the scheduled appointment date.
	public Date getAppointmentDate() {
		return appointmentDate;
	}

	// Returns the appointment description.
	public String getDescription() {
		return description;
	}
}