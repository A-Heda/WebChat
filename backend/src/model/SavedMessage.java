package model;

public class SavedMessage {

    private String userId;
    private String chatId;
    private String messageId;


    public SavedMessage(String userId, String chatId, String messageId){
        this.userId = userId;
        this.chatId = chatId;
        this.messageId = messageId;
    }


    public String getUserId() {
        return userId;
    }


    public String getChatId() {
        return chatId;
    }


    public String getMessageId() {
        return messageId;
    }
}
