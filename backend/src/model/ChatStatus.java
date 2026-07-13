package model;

public class ChatStatus {
    private String userId;
    private String chatId;
    private boolean pinned;
    private boolean archived;

    public ChatStatus(String userId, String chatId, boolean pinned, boolean archived) {
        this.userId = userId;
        this.chatId = chatId;
        this.pinned = pinned;
        this.archived = archived;
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
}
