package services;

public class ChatPreview {            //برای ذخیره اطلاعات طرف دوم چت ها
    private String chatId;
    private String otherUserId;
    private String otherUsername;

    public ChatPreview(
            String chatId,
            String otherUserId,
            String otherUsername) {

        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.otherUsername = otherUsername;
    }

    public String getChatId() {
        return chatId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public String getOtherUsername() {
        return otherUsername;
    }
}
