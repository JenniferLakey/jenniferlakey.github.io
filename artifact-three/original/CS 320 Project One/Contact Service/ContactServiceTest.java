package contactService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContactServiceTest {

	@Test
	void testContactServiceAddNewContact() {
		ContactService service = new ContactService();
		Contact c = new Contact ("ID12345", "Imma", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		service.addNewContact(c);
		assertTrue(service.contactList.contains(c));
		assertTrue(c.getID().equals("ID12345"));
		assertTrue(c.getFirstName().equals("Imma"));
		assertTrue(c.getLastName().equals("Person"));
		assertTrue(c.getPhone().equals("1112223456"));
		assertTrue(c.getAddress().equals("123 Astreet, Somewhere, Aplace"));
	}
	
	@Test
	void testContactServiceAddNewContactIDNotUnique() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			ContactService service = new ContactService();
			Contact c = new Contact ("ID12345", "Imma", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
			service.addNewContact(c);
			service.addNewContact(c);
		}); }
	
	@Test
	void testContactServiceDeleteContact() {
		ContactService service = new ContactService();
		Contact c = new Contact ("ID12345", "Imma", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		service.addNewContact(c);
		assertTrue(service.contactList.contains(c));
		service.deleteContact("ID12345");
		assertTrue(!service.contactList.contains(c));
	}
	
	@Test
	void testContactServiceDeleteContactContactNotFound() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			ContactService service = new ContactService();
			service.deleteContact("ID12345");
		}); }
	
	@Test
	void testContactServiceUpdateContact() {
		ContactService service = new ContactService();
		Contact c = new Contact ("ID12345", "Imma", "Person", "1112223456", "123 Astreet, Somewhere, Aplace");
		service.addNewContact(c);
		assertTrue(service.contactList.contains(c));
		assertTrue(c.getID().equals("ID12345"));
		assertTrue(c.getFirstName().equals("Imma"));
		assertTrue(c.getLastName().equals("Person"));
		assertTrue(c.getPhone().equals("1112223456"));
		assertTrue(c.getAddress().equals("123 Astreet, Somewhere, Aplace"));
		service.updateContact("ID12345", "Emma", "", "", "");
		assertTrue(service.contactList.contains(c));
		assertTrue(c.getID().equals("ID12345"));
		assertTrue(c.getFirstName().equals("Emma"));
		assertTrue(c.getLastName().equals("Person"));
		assertTrue(c.getPhone().equals("1112223456"));
		assertTrue(c.getAddress().equals("123 Astreet, Somewhere, Aplace"));
		service.updateContact("ID12345", "Imma", "Nugget", "", "");
		assertTrue(service.contactList.contains(c));
		assertTrue(c.getID().equals("ID12345"));
		assertTrue(c.getFirstName().equals("Imma"));
		assertTrue(c.getLastName().equals("Nugget"));
		assertTrue(c.getPhone().equals("1112223456"));
		assertTrue(c.getAddress().equals("123 Astreet, Somewhere, Aplace"));
		service.updateContact("ID12345", "", "Person", "1234567890", "");
		assertTrue(service.contactList.contains(c));
		assertTrue(c.getID().equals("ID12345"));
		assertTrue(c.getFirstName().equals("Imma"));
		assertTrue(c.getLastName().equals("Person"));
		assertTrue(c.getPhone().equals("1234567890"));
		assertTrue(c.getAddress().equals("123 Astreet, Somewhere, Aplace"));
		service.updateContact("ID12345", "", "", "1112223456", "Not Here");
		assertTrue(service.contactList.contains(c));
		assertTrue(c.getID().equals("ID12345"));
		assertTrue(c.getFirstName().equals("Imma"));
		assertTrue(c.getLastName().equals("Person"));
		assertTrue(c.getPhone().equals("1112223456"));
		assertTrue(c.getAddress().equals("Not Here"));
	}
	
	@Test
	void testContactServiceUpdateContactContactNotFound() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->{
			ContactService service = new ContactService();
			service.updateContact("ID12345", "Emma", "", "", "");
		}); }
	

}
