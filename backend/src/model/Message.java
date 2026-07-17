
package model;

import java.util.ArrayList;
import java.util.List;

public class Message {

    private String id;
    private String senderId;
    private String text;
    private long timestamp;
    private boolean edited;
    private boolean deleted;
    private String chatId;
    private String previousContent;
    private String mediaUrl;
    private List<String> reportedBy;

    public Message(String id, String senderId, String text, long timestamp, String chatId) {
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.chatId = chatId;
        this.edited = false;
        this.deleted = false;
        this.previousContent = null;
        this.mediaUrl = null;
        this.reportedBy = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getPreviousContent() {
        return previousContent;
    }

    public void setPreviousContent(String previousContent) {
        this.previousContent = previousContent;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public List<String> getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(List<String> reportedBy) {
        this.reportedBy = reportedBy;
    }

    @Override
    public String toString() {
        return id + "|" + senderId + "|" + text + "|" + timestamp + "|" +
                edited + "|" + deleted + "|" + chatId + "|" +
                (previousContent != null ? previousContent : "") + "|" +
                (mediaUrl != null ? mediaUrl : "");
    }
}
