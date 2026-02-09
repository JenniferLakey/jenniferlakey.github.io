package contactService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContactTest {

	@Test
	void testContact() {
		Contact c = new Contact ("ID12345", "Imma", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		assertTrue(c.getID().equals("ID12345"));
		assertTrue(c.getFirstName().equals("Imma"));
		assertTrue(c.getLastName().equals("Person"));
		assertTrue(c.getPhone().equals("1112223456"));
		assertTrue(c.getAddress().equals("123 Astreet, Somewhere, Aplace"));
	}
	
	@Test
	void testContactIDTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Contact ("ID123456789", "Imma", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		}); }
	
	@Test
	void testContactIDIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Contact (null, "Imma", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		}); }
	
	@Test
	void testContactFirstNameTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Contact ("ID12345", "ImmaTooLong", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		}); }
	
	@Test
	void testContactFirstNameIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Contact ("ID12345", null, "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		}); }
	
	@Test
	void testContactLastNameTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Contact ("ID12345", "Imma", "PersonTooLong", "1112223456", "123 Astreet, Somewhere, Aplace");
		}); }
	
	@Test
	void testContactLastNameIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Contact ("ID12345", "Imma", null, "1112223456", "123 Astreet, Somewhere, Aplace");
		}); }
	
	@Test
	void testContactPhoneNotTen() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Contact ("ID12345", "Imma", "Person", "11122234567", "123 Astreet, Somewhere, Aplace");
		}); }
	
	@Test
	void testContactPhoneIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Contact ("ID12345", "Imma", "Person", null, "123 Astreet, Somewhere, Aplace");
		}); }
	
	@Test
	void testContactAddressTooLong() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Contact ("ID12345", "Imma", "Person", "1112223456", "123 Somestreet, Somewhere, Aplace");
		}); }
	
	@Test
	void testContactAddressIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			new Contact ("ID12345", "Imma", "Person", "1112223456", null);
		}); }

}
