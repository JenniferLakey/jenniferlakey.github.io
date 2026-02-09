package edu.snhu.jenniferlakey.services;

import edu.snhu.jenniferlakey.models.Appointment;
import edu.snhu.jenniferlakey.repository.AppointmentRepository;

/**
 * Service layer responsible for enforcing business rules and
 * delegating persistence operations to the AppointmentRepository.
 * This replaces the original in-memory ArrayList implementation
 * with a database-backed architecture.
 */

public class AppointmentService {

	private final AppointmentRepository repository;

	public AppointmentService() {
		this.repository = new AppointmentRepository();
	}

	/**
	 * Add a new appointment.
	 * 
	 * Business rule: Appointment IDs must be unique.
	 * 
	 * The repository now enforces uniqueness using a MongoDB unique index,
	 * so we rely on repository.insert() to throw an IllegalArgumentException
	 * if the ID already exists.
	 */
	public void addNewAppointment(Appointment appointment) {
		repository.insert(appointment);
	}

	/**
	 * Delete an appointment by ID.
	 * 
	 * Business rule: Appointment must exist before deletion.
	 */
	public void deleteAppointment(String id) {
		Appointment existing = repository.findById(id);
		if (existing == null) {
			throw new IllegalArgumentException("Appointment not found");
		}
		repository.delete(id);
	}

	/**
	 * Retrieve an appointment by ID.
	 */
	public Appointment getAppointment(String id) {
		return repository.findById(id);
	}

	/**
	 * Update an appointment.
	 * 
	 * Business rule: Appointment must exist before update.
	 * 
	 * The repository handles partial updates (only non-empty fields),
	 * so the service simply validates existence and delegates.
	 */
	public void updateAppointment(Appointment appointment) {
		Appointment existing = repository.findById(appointment.getAppointmentID());
		if (existing == null) {
			throw new IllegalArgumentException("Appointment not found");
		}
		repository.update(appointment);
	}
}