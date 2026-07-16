package repositories;
import java.util.List;
import java.io.*;
import java.util.ArrayList;

import model.ChatStatus;

public class ChatStatusRepository {

    private static final String FILE = "backend/database/chat_status.txt";

    public List<ChatStatus> loadAll() {
        List<ChatStatus> result = new ArrayList<>();

        File file = new File(FILE);

        if(!file.exists())
            return result;

        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                if(parts.length != 4)
                    continue;

                result.add(new ChatStatus(parts[0], parts[1],
                            Boolean.parseBoolean(parts[2]),
                            Boolean.parseBoolean(parts[3])));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private synchronized void saveAll(List<ChatStatus> list) {

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
            for(ChatStatus status : list){

                writer.write(status.getUserId()+"|"+
                             status.getChatId()+"|"+
                             status.isPinned()+"|"+
                             status.isArchived());

                writer.newLine();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private ChatStatus findStatus(String userId, String chatId){

        List<ChatStatus> list = loadAll();

        for(ChatStatus status : list){
            if(status.getUserId().equals(userId) && status.getChatId().equals(chatId))
                return status;
        }

        return null;
    }

    public boolean isPinned(String userId, String chatId){

        ChatStatus status = findStatus(userId,chatId);

        if(status == null)
            return false;

        return status.isPinned();
    }

    public boolean isArchived(String userId, String chatId){

        ChatStatus status = findStatus(userId,chatId);

        if(status == null)
            return false;

        return status.isArchived();
    }

    public void setPinned(String userId, String chatId, boolean pinned) {

        List<ChatStatus> list = loadAll();

        ChatStatus target = null;

        for(ChatStatus status : list) {

            if(status.getUserId().equals(userId)
                    &&
               status.getChatId().equals(chatId)){

                target = status;
                break;
            }

        }

        if(target == null) {          //وجود نداشت
            target = new ChatStatus(userId, chatId, pinned,false);
            list.add(target);
        }
        else {
            target.setPinned(pinned);
        }
        saveAll(list);
    }

    public void setArchived(String userId, String chatId, boolean archived) {

        List<ChatStatus> list = loadAll();

        ChatStatus target = null;

        for(ChatStatus status : list){

            if(status.getUserId().equals(userId)
                    &&
               status.getChatId().equals(chatId)){

                target = status;
                break;
            }
        }

        if(target == null){
            target = new ChatStatus(userId, chatId, false, archived);
            list.add(target);
        } 
        else {
            target.setArchived(archived);
        }
        saveAll(list);
    }

}

