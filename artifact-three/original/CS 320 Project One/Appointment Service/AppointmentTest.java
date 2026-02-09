package appointmentService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AppointmentTest {

	@Test
	void testAppointment() {
		Appointment a = new Appointment ("ID12345", new Date(125, 07, 06), "Details about the appointment.");
		assertTrue(a.getAppointmentID().equals("ID12345"));
		assertTrue(a.getAppointmentDate().equals(new Date(125, 07, 06)));
		assertTrue(a.getDescription().equals("Details about the appointment."));
		}
	
	@Test
	void testAppointmentIDTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Appointment ("ID123457890", new Date(125, 07, 06), "Details about the appointment.");
		}); }
	
	@Test
	void testAppointmentIDIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Appointment (null, new Date(125, 07, 06), "Details about the appointment.");
		}); }
	
	@Test
	void testAppointmentDateInPast() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Appointment ("ID12345", new Date(125, 5, 05), "Details about the appointment.");
		}); }
	
	@Test
	void testAppointmentDateIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Appointment ("ID12345", null, "Details about the appointment.");
		}); }
	
	@Test
	void testAppointmentDescriptionTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Appointment ("ID12345", new Date(125, 07, 06), "Details about the appointment but much longer than it should be.");
		}); }
	
	@Test
	void testAppointmentDescriptonIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Appointment ("ID12345", new Date(125, 07, 06), null);
		}); }

}

