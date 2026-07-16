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
import java.util.Arrays;



public class ChatRepository {
    private static final String CHATS_DIRECTORY = "backend/database/chats/";

    private String serializeMessage(Message message) {


    String previousContent =
            message.getPreviousContent() == null ?
            "" : message.getPreviousContent();


    String mediaUrl =
            message.getMediaUrl() == null ?
            "" : message.getMediaUrl();


    String reports =
            String.join(",", message.getReportedBy());


    return message.getId() + "|" +
            message.getSenderId() + "|" +
            message.getText() + "|" +
            message.getTimestamp() + "|" +
            message.isEdited() + "|" +
            message.isDeleted() + "|" +
            message.getChatId() + "|" +
            previousContent + "|" +
            mediaUrl + "|" +
            reports;
}

    private Message deserializeMessage(String line) {


    String[] parts = line.split("\\|" , -1);


    if(parts.length != 10)
        return null;


    Message message = new Message(
            parts[0],
            parts[1],
            parts[2],
            Long.parseLong(parts[3]),
            parts[6]
    );


    message.setEdited(Boolean.parseBoolean(parts[4]));
    message.setDeleted(Boolean.parseBoolean(parts[5]));


    if(!parts[7].isEmpty())
        message.setPreviousContent(parts[7]);


    if(!parts[8].isEmpty())
        message.setMediaUrl(parts[8]);


    if(!parts[9].isEmpty()) {
        message.setReportedBy(
                new ArrayList<>(
                        Arrays.asList(parts[9].split(","))
                )
        );
    }


    return message;
}

    private String generatePrivateChatId(String user1Id,String user2Id) {

        if(user1Id.compareTo(user2Id) < 0) {
            return "private_" + user1Id + "_" + user2Id;
        }

        return "private_" + user2Id + "_" + user1Id;
    }

    public String getPrivateChatId(String user1Id, String user2Id) {
    return generatePrivateChatId(user1Id, user2Id);
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

    public synchronized void saveMessage (Message message) {               //writes a message in database          
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

     public List<String> getPrivateChats(String userId) {

        List<String> chats = new ArrayList<>();
        File folder = new File(CHATS_DIRECTORY);
        File[] files = folder.listFiles();

        if(files == null)
            return chats;

        for(File file : files) {
            String fileName = file.getName();
            if(!fileName.startsWith("private_"))
                continue;
            if(fileName.contains(userId)){
                chats.add(fileName.replace(".txt",""));
            }
        }
        return chats;
    }
    

    public synchronized void updateMessage (Message updatedMessage) {
        List<Message> messages = getAllMessages(updatedMessage.getChatId());

        for(int i = 0 ; i< messages.size() ; i++) {
            if(messages.get(i).getId().equals(updatedMessage.getId())) {
                messages.set(i, updatedMessage);
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

    public boolean deleteGroupChatFile(String groupId) {
        String chatId = "group_" + groupId;
        File chatFile = new File(getChatFilePath(chatId));
    
        if (chatFile.exists()) {
            return chatFile.delete();
        }
    return false;
    }

}