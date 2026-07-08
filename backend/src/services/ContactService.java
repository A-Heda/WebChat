package services;

import model.Contact;
import model.User;
import repositories.ContactRepository;
import repositories.UserRepository;

import java.util.List;

public class ContactService {
    private ContactRepository contactRepository;
    private UserRepository userRepository;

    public ContactService() {
        contactRepository = new ContactRepository();
        userRepository = new UserRepository();
    }

    public String addContact(String ownerId, String contactId) {
        if (ownerId == null || ownerId.trim().isEmpty())
            return "Owner ID cannot be empty.";

        if (contactId == null || contactId.trim().isEmpty())
            return "Contact ID cannot be empty.";

        if (ownerId.equals(contactId))
            return "You cannot add yourself.";

        User owner = userRepository.findById(ownerId);

        if (owner == null)
            return "Owner not found.";

        User contactUser = userRepository.findById(contactId);

        if (contactUser == null)
            return "User not found.";

        if (contactRepository.findContact(ownerId, contactId) != null)
            return "Contact already exists.";

        Contact contact = new Contact(ownerId, contactId);
        contactRepository.saveContact(contact);

        return "SUCCESS";
    }

    public String removeContact(String ownerId, String contactId) {
        Contact contact = contactRepository.findContact(ownerId, contactId);

        if (contact == null)
            return "Contact not found.";

        contactRepository.deleteContact(ownerId, contactId);

        return "SUCCESS";
    }

    public String blockContact(String ownerId, String contactId) {
        Contact contact = contactRepository.findContact(ownerId, contactId);

        if (contact == null)
            return "Contact not found.";

        contact.setBlocked(true);
        contactRepository.updateContact(contact);

        return "SUCCESS";
    }

    public String unblockContact(String ownerId, String contactId) {
        Contact contact = contactRepository.findContact(ownerId, contactId);

        if (contact == null)
            return "Contact not found.";

        contact.setBlocked(false);
        contactRepository.updateContact(contact);

        return "SUCCESS";
    }

    public List<Contact> getContacts(String ownerId) {
        return contactRepository.getContactsOfUser(ownerId);
    }
}
