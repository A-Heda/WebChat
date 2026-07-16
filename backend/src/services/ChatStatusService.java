package services;

import repositories.ChatStatusRepository;

public class ChatStatusService {

    private ChatStatusRepository chatStatusRepository;

    public ChatStatusService() {
        chatStatusRepository = new ChatStatusRepository();
    }

    public void pinChat(String userId, String chatId, boolean pinned) {
        chatStatusRepository.setPinned(userId, chatId, pinned);
    }

    public boolean isPinned(String userId, String chatId) {
        return chatStatusRepository.isPinned(userId, chatId);
    }

    public void setArchived(String userId, String chatId, boolean archived) {
        chatStatusRepository.setArchived(userId, chatId, archived);
    }

    public boolean isArchived(String userId, String chatId) {
        return chatStatusRepository.isArchived(userId, chatId);
    }

    public long getLastRead(String userId, String chatId) {
        return chatStatusRepository.getLastRead(userId, chatId);
    }

    public void markAsRead(String userId, String chatId) {
        chatStatusRepository.markAsRead(userId, chatId, System.currentTimeMillis());
    }
}