// ChatService.java
package services;

import model.Message;
import model.User;
import java.util.*;
import java.util.stream.Collectors;

public class ChatService {

    private Map<String, List<Message>> chatMessages; // chatId -> List<Message>
    private Map<String, Map<String, String>> reactions; // messageId -> (userId -> emoji)

    public ChatService() {
        this.chatMessages = new HashMap<>();
        this.reactions = new HashMap<>();
    }

    // ارسال پیام
    public Message sendMessage(String senderId, String chatId, String text, String mediaUrl) {
        if (text != null && text.length() > 500) {
            throw new IllegalArgumentException("پیام نباید بیشتر از 500 کاراکتر باشد");
        }

        String messageId = UUID.randomUUID().toString();
        Message message = new Message(messageId, senderId, text, System.currentTimeMillis(), chatId);
        message.setMediaUrl(mediaUrl);

        chatMessages.putIfAbsent(chatId, new ArrayList<>());
        chatMessages.get(chatId).add(message);

        return message;
    }

    // دریافت تمام پیام‌های یک چت
    public List<Message> getMessages(String chatId) {
        return chatMessages.getOrDefault(chatId, new ArrayList<>());
    }

    // جستجو در پیام‌ها
    public List<Message> searchMessages(String chatId, String keyword) {
        return chatMessages.getOrDefault(chatId, new ArrayList<>()).stream()
                .filter(m -> !m.isDeleted() && m.getText().contains(keyword))
                .collect(Collectors.toList());
    }

    // ویرایش پیام
    public boolean editMessage(String messageId, String newText, String userId) {
        if (newText != null && newText.length() > 500) {
            throw new IllegalArgumentException("پیام نباید بیشتر از 500 کاراکتر باشد");
        }

        for (List<Message> messages : chatMessages.values()) {
            for (Message msg : messages) {
                if (msg.getId().equals(messageId) && msg.getSenderId().equals(userId)) {
                    msg.setPreviousContent(msg.getText());
                    msg.setText(newText);
                    msg.setEdited(true);
                    return true;
                }
            }
        }
        return false;
    }

    // حذف پیام
    public boolean deleteMessage(String messageId, String userId) {
        for (List<Message> messages : chatMessages.values()) {
            for (Message msg : messages) {
                if (msg.getId().equals(messageId) && msg.getSenderId().equals(userId)) {
                    msg.setDeleted(true);
                    msg.setText("[پیام حذف شده]");
                    return true;
                }
            }
        }
        return false;
    }

    // گزارش پیام
    public boolean reportMessage(String messageId, String reporterId) {
        for (List<Message> messages : chatMessages.values()) {
            for (Message msg : messages) {
                if (msg.getId().equals(messageId)) {
                    if (!msg.getReportedBy().contains(reporterId)) {
                        msg.getReportedBy().add(reporterId);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // واکنش به پیام
    public void reactToMessage(String messageId, String userId, String emoji) {
        reactions.putIfAbsent(messageId, new HashMap<>());
        reactions.get(messageId).put(userId, emoji);
    }

    // حذف واکنش
    public void removeReaction(String messageId, String userId) {
        if (reactions.containsKey(messageId)) {
            reactions.get(messageId).remove(userId);
        }
    }

    // دریافت واکنش‌های یک پیام
    public Map<String, String> getReactions(String messageId) {
        return reactions.getOrDefault(messageId, new HashMap<>());
    }

    // پین کردن چت
    public void pinChat(User user, String chatId) {
        if (!user.getPinnedChats().contains(chatId)) {
            user.getPinnedChats().add(chatId);
        }
    }

    // حذف پین چت
    public void unpinChat(User user, String chatId) {
        user.getPinnedChats().remove(chatId);
    }

    // آرشیو کردن چت
    public void archiveChat(User user, String chatId) {
        if (!user.getArchivedChats().contains(chatId)) {
            user.getArchivedChats().add(chatId);
        }
    }

    // خروج از آرشیو
    public void unarchiveChat(User user, String chatId) {
        user.getArchivedChats().remove(chatId);
    }

    // بلاک کردن کاربر
    public void blockUser(User user, String targetUserId) {
        if (!user.getBlockedUsers().contains(targetUserId)) {
            user.getBlockedUsers().add(targetUserId);
        }
    }

    // آنبلاک کردن کاربر
    public void unblockUser(User user, String targetUserId) {
        user.getBlockedUsers().remove(targetUserId);
    }

    // چک کردن بلاک بودن
    public boolean isBlocked(User user, String targetUserId) {
        return user.getBlockedUsers().contains(targetUserId);
    }

    // دریافت تاریخچه ویرایش پیام
    public String getEditHistory(String messageId) {
        for (List<Message> messages : chatMessages.values()) {
            for (Message msg : messages) {
                if (msg.getId().equals(messageId) && msg.getPreviousContent() != null) {
                    return msg.getPreviousContent();
                }
            }
        }
        return null;
    }

    // دریافت پیام‌های حذف شده
    public List<Message> getDeletedMessages(String chatId) {
        return chatMessages.getOrDefault(chatId, new ArrayList<>()).stream()
                .filter(Message::isDeleted)
                .collect(Collectors.toList());
    }
}
