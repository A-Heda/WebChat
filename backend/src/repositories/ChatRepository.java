package repositories;

import model.Message;

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;



public class ChatRepository {
    private static final String CHATS_DIRECTORY = "database/chats/";

    private String serializeMessage(Message message) {
        return message.getId() + "|" + message.getSenderId()
                + "|" + message.getText() + "|" + message.getTimestamp() + "|"
                + message.isEdited()+ "|" + message.isDeleted()+ "|" + message.getChatId();
    }

    private Message deserializeMessage(String line) {
        String[] parts = line.split("\\|");

         if(parts.length != 7) {
            return null;
        }
        long timestamp = Long.parseLong(parts[3]);
        Message message = new Message(parts[0], parts[1], parts[2], timestamp , parts[6]);

        message.setEdited(Boolean.parseBoolean(parts[4]));

        message.setDeleted(Boolean.parseBoolean(parts[5]));

        return message;
    }

    private String generatePrivateChatId(String user1Id, String user2Id) {      //e.g. private_1_2
        int firstUserId = Integer.parseInt(user1Id);
        int secondUserId = Integer.parseInt(user2Id);

        if (firstUserId < secondUserId)
            return "private_" + firstUserId + "_" + secondUserId;
        else
            return "private_" + secondUserId + "_" + firstUserId;
    }

    private String getChatFilePath (String chatId) {          //e.g. database/chats/private_1_2.txt   
        return CHATS_DIRECTORY + chatId + ".txt";
    }

    public boolean chatExists (String chatId) {
        File chatFile = new File(getChatFilePath(chatId));
        return chatFile.exists();
    }

    public void createPrivateChatFile (String user1Id, String user2Id) {
        File chatsDirectory = new File(CHATS_DIRECTORY);

        if(!chatsDirectory.exists()) {
        chatsDirectory.mkdirs();
        }

        String chatId = generatePrivateChatId(user1Id, user2Id);
        File chatFile = new File(getChatFilePath(chatId));           //only creates an obj not a real file

        if(!chatFile.exists()) {
        try {
            chatFile.createNewFile();
        }catch(IOException e) {
            e.printStackTrace();
            }
        }
    }

    public void createGroupChatFile (String groupId) {
        File chatsDirectory = new File(CHATS_DIRECTORY);

        if(!chatsDirectory.exists()) {
        chatsDirectory.mkdirs();
        }

        String chatId = "group_" + groupId;
        File chatFile = new File(getChatFilePath(chatId));           //only creates an obj not a real file
        if(!chatFile.exists()) {
            try {
                chatFile.createNewFile();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveMessage (Message message) {
        File chatsDirectory = new File(CHATS_DIRECTORY);

        if(!chatsDirectory.exists()) {
        chatsDirectory.mkdirs();
        }

        String chatId = message.getChatId();

        File chatFile = new File(getChatFilePath(chatId));
        if(!chatFile.exists()) {
            try {
            chatFile.createNewFile();
        }catch(IOException e) {
            e.printStackTrace();
            }
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(chatFile , true))) {
            writer.write(serializeMessage(message));
            writer.newLine();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getAllMessages (String chatId) {
        List<Message> messages = new ArrayList<>();

        File chatFile = new File(getChatFilePath(chatId));

        if (!chatFile.exists()) {
            return messages;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(chatFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
       
            Message message = deserializeMessage(line);

            if (message != null) {
                messages.add(message);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

    return messages;
    }

    public void updateMessage (Message updatedMessage) {
        List<Message> messages = getAllMessages(updatedMessage.getChatId());

        for(int i = 0 ; i< messages.size() ; i++) {
            if(messages.get(i).getId().equals(updatedMessage.getId())) {
                messages.set(i, updatedMessage);
                updatedMessage.setEdited(true);
                break;
            }
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(getChatFilePath
                                                         (updatedMessage.getChatId())))) {
        for(Message message : messages) {
            writer.write(serializeMessage(message));
            writer.newLine();
        }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Message findMessageById (String chatId , String messageId) {
        List<Message> messages = getAllMessages(chatId);

        for(Message message : messages) {
            if(message.getId().equals(messageId)) {
            return message;
            }
        }
    return null;
    }

    public void deleteMessage(String chatId, String messageId) {
        Message message = findMessageById(chatId, messageId);
        if(message != null) {
        message.setDeleted(true);
        updateMessage(message);
        }
    }
}