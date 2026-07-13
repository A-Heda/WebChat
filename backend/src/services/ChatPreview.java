package services;

public class ChatPreview {            //برای ذخیره اطلاعات طرف دوم چت ها
    private String chatId;
    private String otherUserId;
    private String otherUsername;
    private String imagePath;
    private String type;           //GROUP or PRIVATE
    private boolean pinned;
    private boolean archived;

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
        this.pinned = false;
        this.archived = false;
    }


    public ChatPreview(String chatId,                     //new constructor, to send the status of a pinned or archived chat
                       String otherUserId,
                       String otherUsername,
                       String imagePath,
                       String type,
                       boolean pinned,
                       boolean archived){

        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.otherUsername = otherUsername;
        this.imagePath = imagePath;
        this.type = type;
        this.pinned = pinned;
        this.archived = archived;
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

    public boolean isPinned(){
        return pinned;
    }

    public boolean isArchived(){
        return archived;
    }
}
