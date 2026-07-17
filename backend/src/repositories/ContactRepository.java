package repositories;

import model.Contact;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ContactRepository {

    private static final String CONTACT_FILE = "backend/database/contacts.txt";

    private String serializeContact(Contact contact) {
        return contact.getOwnerId() + "|" +
               contact.getContactId() + "|" +
               contact.isBlocked();
    }

    private Contact deserializeContact(String line) {
        String[] parts = line.split("\\|", -1);

        if(parts.length != 3)
            return null;

        Contact contact = new Contact(parts[0], parts[1]);
        contact.setBlocked(Boolean.parseBoolean(parts[2]));
        return contact;
    }

    public synchronized void saveContact(Contact contact) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(CONTACT_FILE, true))) {
            writer.write(serializeContact(contact));
            writer.newLine();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        File file = new File(CONTACT_FILE);

        if(!file.exists())
            return contacts;

        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = reader.readLine()) != null) {
                Contact contact = deserializeContact(line);
                if(contact != null)
                    contacts.add(contact);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public Contact findContact(String ownerId, String contactId) {
        List<Contact> contacts = getAllContacts();
        for(Contact contact : contacts) {
            if(contact.getOwnerId().equals(ownerId) && contact.getContactId().equals(contactId)) {
                return contact;
            }
        }
        return null;
    }

    public List<Contact> getContactsOfUser(String ownerId) {
        List<Contact> result = new ArrayList<>();
        List<Contact> contacts = getAllContacts();
        for(Contact contact : contacts) {
            if(contact.getOwnerId().equals(ownerId)) {
                result.add(contact);
            }
        }
        return result;
    }

    public synchronized void updateContact(Contact updatedContact) {
        List<Contact> contacts = getAllContacts();
        for(int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            if(contact.getOwnerId().equals(updatedContact.getOwnerId()) &&
               contact.getContactId().equals(updatedContact.getContactId())) {
                contacts.set(i, updatedContact);
                break;
            }
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(CONTACT_FILE))) {
            for(Contact contact : contacts) {
                writer.write(serializeContact(contact));
                writer.newLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteContact(String ownerId, String contactId) {
        List<Contact> contacts = getAllContacts();
        contacts.removeIf(contact -> contact.getOwnerId().equals(ownerId) &&
                contact.getContactId().equals(contactId));
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(CONTACT_FILE))) {
            for(Contact contact : contacts) {
                writer.write(serializeContact(contact));
                writer.newLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
