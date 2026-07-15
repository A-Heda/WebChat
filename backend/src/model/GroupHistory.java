package model;

public class GroupHistory {

    private String groupId;
    private String messageId;
    private String senderId;
    private String text;
    private String action; // EDIT یا DELETE
    private long time;

    public GroupHistory(String groupId,
                        String messageId,
                        String senderId,
                        String text,
                        String action,
                        long time) {

        this.groupId = groupId;
        this.messageId = messageId;
        this.senderId = senderId;
        this.text = text;
        this.action = action;
        this.time = time;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public String getAction() {
        return action;
    }

    public long getTime() {
        return time;
    }
}