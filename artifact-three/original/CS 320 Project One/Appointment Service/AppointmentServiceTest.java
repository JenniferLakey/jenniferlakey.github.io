package appointmentService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskServiceTest {

	@Test
	void testAppointmentServiceAddNewTask() {
		AppointmentService service = new AppointmentService();
		Appointment a = new Appointment ("ID12345", new Date(125, 07, 06), "Details about the appointment.");
		service.addNewAppointment(a);
		assertTrue(service.appointmentList.contains(a));
		assertTrue(a.getAppointmentID().equals("ID12345"));
		assertTrue(a.getAppointmentDate().equals(new Date(125, 07, 06)));
		assertTrue(a.getDescription().equals("Details about the appointment."));
	}
	
	@Test
	void testAppointmentServiceAddNewAppointmentIDNotUnique() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			AppointmentService service = new AppointmentService();
			Appointment n = new Appointment ("ID012345", new Date(125, 07, 06), "Details about the appointment.");
			service.addNewAppointment(n);
			Appointment a = new Appointment ("ID12345", new Date(125, 07, 06), "Details about the appointment.");
			service.addNewAppointment(a);
			service.addNewAppointment(a);
		}); }
	
	@Test
	void testAppointmentServiceDeleteAppointment() {
		AppointmentService service = new AppointmentService();
		Appointment n = new Appointment ("ID012345", new Date(125, 07, 06), "Details about the appointment.");
		service.addNewAppointment(n);
		Appointment a = new Appointment ("ID12345", new Date(125, 07, 06), "Details about the appointment.");
		service.addNewAppointment(a);
		assertTrue(service.appointmentList.contains(a));
		service.deleteAppointment("ID12345");
		assertTrue(!service.appointmentList.contains(a));
	}
	
	@Test
	void testAppointmentServiceDeleteAppointmentAppointmentNotFound() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			AppointmentService service = new AppointmentService();
			service.deleteAppointment("ID12345");
		}); }
	
	
}

