package model;

public class ChatStatus {
    private String userId;
    private String chatId;
    private boolean pinned;
    private boolean archived;
    private long lastReadTimestamp;

    public ChatStatus(String userId, String chatId, boolean pinned, boolean archived) {
        this.userId = userId;
        this.chatId = chatId;
        this.pinned = pinned;
        this.archived = archived;
        //this.lastReadTimestamp = lastReadTimestamp;    //added new constructor to avoid changes in other classes
    }

    public ChatStatus(String userId, String chatId, boolean pinned, boolean archived, long lastReadTimestamp) {
        this.userId = userId;
        this.chatId = chatId;
        this.pinned = pinned;
        this.archived = archived;
        this.lastReadTimestamp = lastReadTimestamp;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public boolean isPinned() {
        return pinned;
    }
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
    public boolean isArchived() {
        return archived;
    }
    public void setArchived(boolean archived) {
        this.archived = archived;
    }    
    public long getLastReadTimestamp() {
        return lastReadTimestamp;
    }
    public void setLastReadTimestamp(long lastReadTimestamp) {
        this.lastReadTimestamp = lastReadTimestamp;
    }
}
