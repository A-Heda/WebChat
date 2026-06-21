package services;

import model.Message;
import model.User;
import repositories.ChatRepository;
import repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ChatService {

    private ChatRepository chatRepository;
    private UserRepository userRepository;


    public ChatService() {
        chatRepository = new ChatRepository();
        userRepository = new UserRepository();
    }


    private String validateMessage(String text) {

        if(text == null || text.trim().isEmpty())
            return "Message cannot be empty.";

        if(text.length() > 1000)
            return "Message is too long.";

        return "Message is valid.";
    }


    public String sendMessage(String chatId,
                              String senderId,
                              String text) {


        if(!chatRepository.chatExists(chatId))
            return "Chat not found.";


        User sender = userRepository.findById(senderId);

        if(sender == null)
            return "User not found.";


        String response = validateMessage(text);

        if(!response.equals("Message is valid."))
            return response;


        String messageId = UUID.randomUUID().toString();

        Message message = new Message(
                messageId,
                senderId,
                text,
                System.currentTimeMillis(),
                chatId
        );


        chatRepository.saveMessage(message);


        return "SUCCESS";
    }


    public String sendMedia(String chatId,
                            String senderId,
                            String mediaPath) {


        if(!chatRepository.chatExists(chatId))
            return "Chat not found.";


        User sender = userRepository.findById(senderId);

        if(sender == null)
            return "User not found.";


        if(mediaPath == null || mediaPath.trim().isEmpty())
            return "Media path cannot be empty.";


        String messageId = UUID.randomUUID().toString();


        Message message = new Message(
                messageId,
                senderId,
                "",
                System.currentTimeMillis(),
                chatId
        );


        message.setMediaUrl(mediaPath);


        chatRepository.saveMessage(message);


        return "SUCCESS";
    }


    public String editMessage(String chatId,
                              String messageId,
                              String newText,
                              String editorId) {


        Message message =
                chatRepository.findMessageById(chatId, messageId);


        if(message == null)
            return "Message not found.";


        if(!message.getSenderId().equals(editorId))
            return "You can only edit your own messages.";


        String response = validateMessage(newText);


        if(!response.equals("Message is valid."))
            return response;


        message.setPreviousContent(message.getText());

        message.setText(newText);

        message.setEdited(true);


        chatRepository.updateMessage(message);


        return "SUCCESS";
    }


    public String deleteMessage(String chatId,
                                String messageId,
                                String userId) {


        Message message =
                chatRepository.findMessageById(chatId, messageId);


        if(message == null)
            return "Message not found.";


        if(!message.getSenderId().equals(userId))
            return "You can only delete your own messages.";


        message.setDeleted(true);


        chatRepository.updateMessage(message);


        return "SUCCESS";
    }


    public String reportMessage(String chatId,
                                String messageId,
                                String reporterId) {


        Message message =
                chatRepository.findMessageById(chatId, messageId);


        if(message == null)
            return "Message not found.";


        User user = userRepository.findById(reporterId);


        if(user == null)
            return "User not found.";


        if(message.getReportedBy().contains(reporterId))
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


        if(!chatRepository.chatExists(chatId))
            return new ArrayList<>();


        return chatRepository.getAllMessages(chatId);
    }


    public List<Message> searchMessages(String chatId,
                                        String keyword) {


        List<Message> result = new ArrayList<>();


        if(keyword == null || keyword.trim().isEmpty())
            return result;


        List<Message> messages =
                chatRepository.getAllMessages(chatId);


        for(Message message : messages) {


            if(message.getText() != null &&
               message.getText()
                      .toLowerCase()
                      .contains(keyword.toLowerCase())) {


                result.add(message);
            }
        }


        return result;
    }
}