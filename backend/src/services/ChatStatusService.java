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

public void archiveChat(String userId, String chatId, boolean archived){
    chatStatusRepository.setArchived(userId, chatId, archived);
}
    
}
