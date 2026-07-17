package model;

public class Contact {
    private String ownerId;
    private String contactId;
    private boolean blocked;


    public Contact(String ownerId, String contactId) {
        this.ownerId = ownerId;
        this.contactId = contactId;
        this.blocked = false;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getContactId() {
        return contactId;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
