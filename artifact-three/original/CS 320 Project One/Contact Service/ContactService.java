package contactService;
import java.util.ArrayList;
import java.util.List;


public class ContactService {
	List <Contact> contactList = new ArrayList<>();
	
	public void addNewContact(Contact c) throws IllegalArgumentException {
		if (isUnique(c.getID())) {
           contactList.add(c);
        } else {
            throw new IllegalArgumentException("ID must be unique!");
        }
    }
	
	private boolean isUnique(String id) {
        for (Contact c: contactList) { // Check uniqueness
        	if(c.getID().equals(id)) {
        		return false;
        	}
        }
        return true;
    }
	
    public void deleteContact(String id) throws IllegalArgumentException{
    	boolean notContained = contactList.stream().noneMatch(obj -> obj.getID() == id);
    	if (notContained){
			throw new IllegalArgumentException("Contact not found");
		}
    	Contact temp = null;
    	for (Contact c: contactList) {
    		if(c.getID().equals(id)) {
    			temp = c;
    		}
    	}
    	contactList.remove(temp);
    }
    
    public void updateContact(String ID, String firstName, String lastName, String phone, String address) throws IllegalArgumentException{
    	boolean notContained = contactList.stream().noneMatch(obj -> obj.getID() == ID);
    	if (notContained){
			throw new IllegalArgumentException("Contact not found");
		}
    	for (Contact c: contactList) {
    		if(c.getID().equals(ID)) {
    			if (!firstName.isEmpty()) {
    				c.setFirstName(firstName);
    			}
    			if (!lastName.isEmpty()) {
    				c.setLastName(lastName);
    			}
    			if (!phone.isEmpty()) {
    				c.setPhone(phone);
    			}
    			if (!address.isEmpty()) {
    				c.setAddress(address);
    			}
    		}
    	}
    }
    		
}
