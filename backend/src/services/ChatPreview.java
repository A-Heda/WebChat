package services;

public class ChatPreview {            //برای ذخیره اطلاعات طرف دوم چت ها
    private String chatId;
    private String otherUserId;
    private String otherUsername;
    private String imagePath;
    private String type;           //GROUP or PRIVATE

    public ChatPreview(
            String chatId,
            String otherUserId,
            String otherUsername,
            String imagePath,
            String type) {

        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.otherUsername = otherUsername;
        this.imagePath = imagePath;
        this.type = type;
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

    public String getType () {
        return type;
    }

    public String getImagePath() {
        return imagePath;
    }
}
