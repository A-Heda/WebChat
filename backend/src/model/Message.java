package model;

public class Message {

    private String id;

    private String senderId;

    private String text;

    private long timestamp;

    private boolean edited;

    private boolean deleted;

    private String chatId;

    public Message(String id, String senderId, String text, long timestamp, String chatId) {
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.chatId = chatId;

        edited = false;
        deleted = false;
    }

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getSenderId () {
        return senderId;
    }

    public void setSenderId (String senderId) {
        this.senderId = senderId;
    }

    public String getText () {
        return text;
    }

    public void setText (String text) {
        this.text = text;
    }

    public long getTimestamp () {
        return timestamp;
    }

    public void setTimestamp (long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEdited () {
        return edited;
    }

    public void setEdited (boolean edited) {
        this.edited = edited;
    }

    public boolean isDeleted () {
        return deleted;
    }

    public void setDeleted (boolean deleted) {
        this.deleted = deleted;
    }

    public String getChatId () {
        return chatId;
    }

    public void setChatId (String chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {      //created to use to save to database
        return id + "|" +
           senderId + "|" + text + "|" + timestamp + "|" +
           edited + "|" + deleted + "|" + chatId;
    }

}