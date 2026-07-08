package model;

public class Contact {
    private String ownerId;      // صاحب لیست مخاطبین
    private String contactId;    // مخاطب
    private boolean blocked;     // آیا صاحب لیست، مخاطب را بلاک کرده؟


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