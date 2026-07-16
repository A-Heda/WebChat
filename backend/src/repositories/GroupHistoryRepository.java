package repositories;

import model.GroupHistory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GroupHistoryRepository {

    private static final String FILE =
            "backend/database/group_history.txt";

    public synchronized void saveHistory(GroupHistory history) {

        try {

            File file = new File(FILE);

            file.getParentFile().mkdirs();

            if (!file.exists())
                file.createNewFile();

            BufferedWriter writer =
                    new BufferedWriter(
                            new FileWriter(file, true));

            writer.write(

                    history.getGroupId() + "|" +
                    history.getMessageId() + "|" +
                    history.getSenderId() + "|" +
                    history.getAction() + "|" +
                    history.getText() + "|" +
                    history.getTime()

            );

            writer.newLine();

            writer.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public List<GroupHistory> getHistory(String groupId) {

        List<GroupHistory> list =
                new ArrayList<>();

        File file =
                new File(FILE);

        if (!file.exists())
            return list;

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(file));

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts =
                        line.split("\\|",6);

                if(parts.length!=6)
                    continue;

                if(!parts[0].equals(groupId))
                    continue;

                list.add(

                        new GroupHistory(

                                parts[0],
                                parts[1],
                                parts[2],
                                parts[4],
                                parts[3],
                                Long.parseLong(parts[5])

                        )

                );

            }

            reader.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return list;

    }

 
    
}
