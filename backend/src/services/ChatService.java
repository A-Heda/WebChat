package services;

import model.Message;
import model.SavedMessage;
import model.User;
import model.Group;
import repositories.ChatRepository;
import repositories.GroupRepository;
import repositories.SavedMessageRepository;
import repositories.UserRepository;
import repositories.ChatStatusRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatService {

    private ChatRepository chatRepository;
    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private ChatStatusRepository chatStatusRepository;
    private SavedMessageRepository savedMessageRepository;
    private BlockService blockService;
    private GroupHistoryService groupHistoryService;

    public ChatService() {
        chatRepository = new ChatRepository();
        userRepository = new UserRepository();
        groupRepository = new GroupRepository();
        chatStatusRepository = new ChatStatusRepository();
        savedMessageRepository = new SavedMessageRepository();
        blockService = new BlockService();
        groupHistoryService = new GroupHistoryService();
    }

    private String validateMessage(String text) {

        if (text == null || text.trim().isEmpty())
            return "Message cannot be empty.";

        if (text.length() > 1000)
            return "Message is too long.";

        return "Message is valid.";
    }

    public String createPrivateChat(String user1Id, String user2Id) {

        if (user1Id == null || user2Id == null) {
            return "User ID cannot be empty.";
        }

        if (user1Id.equals(user2Id)) {
            return "You cannot create chat with yourself.";
        }

        if (userRepository.findById(user1Id) == null)
            return "First user not found.";

        if (userRepository.findById(user2Id) == null)
            return "Second user not found.";

        String chatId = chatRepository.getPrivateChatId(user1Id, user2Id);

        if (chatRepository.chatExists(chatRepository.getPrivateChatId(user1Id, user2Id)))
            return chatId;

        chatRepository.createPrivateChatFile(user1Id, user2Id);

        return chatId;
    }

    public String sendMessage(String chatId,
            String senderId,
            String text) {

        if (!chatRepository.chatExists(chatId))
            return "Chat not found.";

        User sender = userRepository.findById(senderId);

        if (sender == null)
            return "User not found.";

        String response = validateMessage(text);

        if (!response.equals("Message is valid."))
            return response;

        // ================= Block Check =================

        if (chatId.startsWith("private_")) {

            String[] parts = chatId.split("_");

            if (parts.length == 3) {

                String user1 = parts[1];
                String user2 = parts[2];

                String otherUser = senderId.equals(user1)
                        ? user2
                        : user1;

                if (blockService.isBlocked(senderId, otherUser)) {
                    return "You cannot send messages because one of the users has blocked the other.";
                }
            }
        }

        // =================================================

        String messageId = UUID.randomUUID().toString();

        Message message = new Message(
                messageId,
                senderId,
                text,
                System.currentTimeMillis(),
                chatId);

        chatRepository.saveMessage(message);

        return "SUCCESS";
    }

    public String sendSavedMessage(String userId, String text, String mediaUrl) {

        String messageId = UUID.randomUUID().toString();
        User user = userRepository.findById(userId);

        if (user == null)
            return "User not found.";

        if ((text == null || text.trim().isEmpty()) &&
                (mediaUrl == null || mediaUrl.trim().isEmpty()))
            return "Message cannot be empty.";

        String chatId = "saved_" + userId;

        Message message = new Message(messageId, userId, text,
                System.currentTimeMillis(), chatId);

        message.setMediaUrl(mediaUrl);

        chatRepository.saveMessage(message);

        SavedMessage saveMessage = new SavedMessage(userId, chatId, messageId);
        savedMessageRepository.save(saveMessage);

        return "SUCCESS";
    }

    public String sendMedia(String chatId,
            String senderId,
            String mediaPath) {

        if (!chatRepository.chatExists(chatId))
            return "Chat not found.";

        User sender = userRepository.findById(senderId);

        if (sender == null)
            return "User not found.";

        if (mediaPath == null || mediaPath.trim().isEmpty())
            return "Media path cannot be empty.";

        String messageId = UUID.randomUUID().toString();

        Message message = new Message(
                messageId,
                senderId,
                "",
                System.currentTimeMillis(),
                chatId);

        message.setMediaUrl(mediaPath);

        chatRepository.saveMessage(message);

        return "SUCCESS";
    }

    public String editMessage(String chatId,
            String messageId,
            String newText,
            String editorId) {

        Message message = chatRepository.findMessageById(chatId, messageId);

        if (message == null)
            return "Message not found.";

        if (!message.getSenderId().equals(editorId))
            return "You can only edit your own messages.";

        String response = validateMessage(newText);

        if (!response.equals("Message is valid."))
            return response;

        // فقط برای گروه
        if (chatId.startsWith("group_")) {

            String groupId =
                    chatId.substring("group_".length());

            groupHistoryService.saveHistory(

                    groupId,
                    message.getId(),
                    message.getSenderId(),
                    message.getText(), // متن قبل از ادیت
                    "EDIT"

            );

        }

        message.setPreviousContent(message.getText());

        message.setText(newText);

        message.setEdited(true);

        chatRepository.updateMessage(message);

        return "SUCCESS";
    }

    public String deleteMessage(String chatId,
            String messageId,
            String requesterId) {

        Message message = chatRepository.findMessageById(chatId, messageId);

        if (message == null)
            return "Message not found.";

        if (!message.getSenderId().equals(requesterId))
            return "You can only delete your own messages.";

        // فقط برای گروه
        if (chatId.startsWith("group_")) {

            String groupId =
                    chatId.substring("group_".length());

            groupHistoryService.saveHistory(

                    groupId,
                    message.getId(),
                    message.getSenderId(),
                    message.getText(), // متن قبل از حذف
                    "DELETE"

            );

        }

        message.setDeleted(true);

        chatRepository.updateMessage(message);

        return "SUCCESS";
    }

    public String reportMessage(String chatId,
            String messageId,
            String reporterId) {

        Message message = chatRepository.findMessageById(chatId, messageId);

        if (message == null)
            return "Message not found.";

        User user = userRepository.findById(reporterId);

        if (user == null)
            return "User not found.";

        if (message.getReportedBy().contains(reporterId))
            return "You have already reported this message.";

        message.getReportedBy().add(reporterId);

        chatRepository.updateMessage(message);

        return "SUCCESS";
    }

    public Message findMessageById(String chatId,
            String messageId) {

        return chatRepository.findMessageById(chatId, messageId);
    }

    public List<Message> getAllMessages(String chatId) {

        if (!chatRepository.chatExists(chatId))
            return null;

        return chatRepository.getAllMessages(chatId);
    }

    public List<ChatPreview> getPrivateChats(String userId) {

        List<ChatPreview> result = new ArrayList<>();

        List<String> chats = chatRepository.getPrivateChats(userId);

        for (String chatId : chats) {
            String[] parts = chatId.split("_");
            String firstUser = parts[1];
            String secondUser = parts[2];
            String otherUserId = firstUser.equals(userId) ? secondUser : firstUser;

            User otherUser = userRepository.findById(otherUserId);

            if (otherUser == null)
                continue;
            ChatPreview preview = new ChatPreview(chatId, otherUserId, otherUser.getUsername(),
                otherUser.getProfileImagePath(), "PRIVATE",
                chatStatusRepository.isPinned(userId, chatId),
                chatStatusRepository.isArchived(userId, chatId));

            fillUnreadAndLastTime(preview, chatId, userId);
            result.add(preview);
        }

        return result;
    }

    public List<ChatPreview> getGroupChats(String userId) {
    
        List<ChatPreview> result = new ArrayList<>();
        List<Group> groups = groupRepository.getUserGroups(userId);
        for (Group group : groups) {
            String chatId = "group_" + group.getId();
            ChatPreview preview = new ChatPreview(chatId, group.getId(), group.getName(),
                group.getGroupImagePath(), "GROUP", chatStatusRepository.isPinned(userId, chatId),
                chatStatusRepository.isArchived(userId, chatId));

        fillUnreadAndLastTime(preview, chatId, userId);

        result.add(preview);
    }

    return result;
}

    private void fillUnreadAndLastTime(ChatPreview chatPreview, String chatId, String userId) {
        List<Message> chatMessages = chatRepository.getAllMessages(chatId);

        long lastReadTimestamp = chatStatusRepository.getLastRead(userId, chatId);
        long lastMessageTimestamp = 0;
        int unreadMessageCount = 0;

        for(Message message : chatMessages) {
            if(message.getTimestamp() > lastMessageTimestamp) {
                lastMessageTimestamp = message.getTimestamp();
            }

            if(!message.getSenderId().equals(userId)
                    && !message.isDeleted()
                    && message.getTimestamp() > lastReadTimestamp) {
                    unreadMessageCount++;
            }
        }

        chatPreview.setUnreadCount(unreadMessageCount);
        chatPreview.setLastMessageTime(lastMessageTimestamp);
    }


    public List<ChatPreview> getUserChats(String userId) {

        List<ChatPreview> result = new ArrayList<>();
        result.addAll(getPrivateChats(userId));

        result.addAll(getGroupChats(userId));

        return result;
    }

    public ChatPreview getGroupChatInfo(String chatId) {

        String groupId = chatId.substring("group_".length());
        Group group = groupRepository.findById(groupId);

        if (group == null)
            return null;

        return new ChatPreview(chatId, group.getId(), group.getName(),
                group.getGroupImagePath(), "GROUP");
    }

    public ChatPreview getPrivateChatInfo(
            String chatId,
            String currentUserId) {

        if (!chatRepository.chatExists(chatId))
            return null;

        String[] parts = chatId.split("_");

        if (parts.length != 3)
            return null;

        String firstUser = parts[1];
        String secondUser = parts[2];

        String otherUserId;

        if (firstUser.equals(currentUserId))
            otherUserId = secondUser;
        else
            otherUserId = firstUser;

        User otherUser = userRepository.findById(otherUserId);

        if (otherUser == null)
            return null;

        return new ChatPreview(chatId, otherUserId, otherUser.getUsername(),
                otherUser.getProfileImagePath(), "PRIVATE");
    }

    public ChatPreview getChatInfo(String chatId, String currentUserId) {

        if (chatId.startsWith("private_"))
            return getPrivateChatInfo(chatId, currentUserId);

        if (chatId.startsWith("group_"))
            return getGroupChatInfo(chatId);

        return null;
    }

    public List<Message> searchMessages(String chatId,
            String keyword) {

        List<Message> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty())
            return result;

        List<Message> messages = chatRepository.getAllMessages(chatId);

        for (Message message : messages) {

            if (message.getText() != null &&
                    message.getText()
                            .toLowerCase()
                            .contains(keyword.toLowerCase())) {

                result.add(message);
            }
        }

        return result;
    }

    public String createGroupChat(String groupId, String groupName, String adminId, List<String> memberIds) {

        if (groupId == null || groupId.trim().isEmpty())
            return "Group ID is required.";

        if (groupName == null || groupName.trim().isEmpty())
            return "Group name is required.";

        if (userRepository.findById(adminId) == null)
            return "Admin not found.";

        if (groupRepository.findById(groupId) != null)
            return "Group ID already exists.";

        for (String id : memberIds) {
            if (userRepository.findById(id) == null)
                return "User " + id + " not found.";
        }

        if (!memberIds.contains(adminId))
            memberIds.add(adminId);

        Group group = new Group(groupId, groupName, adminId, memberIds);

        chatRepository.createGroupChatFile(groupId);
        groupRepository.saveGroup(group);

        return "group_" + groupId;
    }

    public String saveSavedMessage(String userId, String chatId, String messageId) {

        Message message = chatRepository.findMessageById(chatId, messageId);

        if (message == null)
            return "Message not found.";

        SavedMessage saved = new SavedMessage(userId, chatId, messageId);
        savedMessageRepository.save(saved);

        return "SUCCESS";

    }

    public List<Message> getSavedMessages(String userId) {

        List<Message> result = new ArrayList<>();
        List<SavedMessage> saved = savedMessageRepository.getUserSavedMessages(userId);
        for (SavedMessage s : saved) {
            Message message = chatRepository.findMessageById(s.getChatId(), s.getMessageId());
            if (message != null)
                result.add(message);
        }

        return result;

    }

}