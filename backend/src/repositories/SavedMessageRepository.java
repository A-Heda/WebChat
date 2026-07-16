package repositories;


import model.SavedMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;



public class SavedMessageRepository {

    private static final String FILE = "backend/database/saved_messages.txt";

    private String serializeSavedMessage(SavedMessage saved) {
        return saved.getUserId() + "|" +
               saved.getChatId() + "|" +
               saved.getMessageId();
    }

    public synchronized void save(SavedMessage saved) {

        try{
            File file = new File(FILE);
            file.getParentFile().mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
            writer.write(serializeSavedMessage(saved));
            writer.newLine();
            writer.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    public List<SavedMessage> getUserSavedMessages(String userId){

        List<SavedMessage> result = new ArrayList<>();

        File file = new File(FILE);

        if(!file.exists())
            return result;

        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while((line=reader.readLine()) != null) {
                String[] p = line.split("\\|"); 

                if(p[0].equals(userId)){
                    result.add(new SavedMessage(p[0], p[1], p[2]));
                }
            }

            reader.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}