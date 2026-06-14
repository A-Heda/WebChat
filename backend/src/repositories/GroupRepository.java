package repositories;

import model.Group;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupRepository {

    private static final String GROUP_FILE = "database/groups.txt";

    private String serializeGroup(Group group) {

        String membersJoinedId = String.join(",", group.getMemberIds());  // ["1" , "2" , "3"] --> 1,2,3 

        return group.getId() + "|" +
                group.getName() + "|" +
                group.getAdminId() + "|" +
                membersJoinedId;
    }

    private Group deserializeGroup(String line) {

        String[] parts = line.split("\\|");

        if(parts.length != 4) {
            return null;
        }

        String[] members = parts[3].split(",");
        List<String> memberIds = new ArrayList<>(Arrays.asList(members));  //1,2,3 --> ["1" , "2" , "3"]

        return new Group(
                parts[0],
                parts[1],
                parts[2],
                memberIds
        );
    }

    public void saveGroup (Group group) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(GROUP_FILE, true))) {

        writer.write(serializeGroup(group));
        writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     public List<Group> getAllGroups() {

        List<Group> groups = new ArrayList<>();

        try(BufferedReader reader =  new BufferedReader(new FileReader(GROUP_FILE))) {

            String line;

            while((line = reader.readLine()) != null) {

                Group group = deserializeGroup(line);

                if(group != null) {
                    groups.add(group);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return groups;
    }

    public Group findById (String id) {
        List<Group> groups = getAllGroups();

        for(Group group : groups) {
            if(group.getId().equals(id)) {
                return group;
            }
        }
        return null;
    }

    public void updateGroup (Group updatedGroup) {
        List<Group> groups = getAllGroups();

        for(int i = 0 ; i < groups.size() ; i++) {
            if(groups.get(i).getId().equals(updatedGroup.getId())) {
                groups.set(i, updatedGroup);
                break;
            }
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(GROUP_FILE))) {

            for(Group group : groups) {
                writer.write(serializeGroup(group));
                writer.newLine();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteGroup (String groupId) {
        List<Group> groups = getAllGroups();

        for(int i = 0 ; i < groups.size() ; i++) {
            if(groups.get(i).getId().equals(groupId)) {
                groups.remove(i);
                break;
            }
        }

         try(BufferedWriter writer = new BufferedWriter(new FileWriter(GROUP_FILE))) {

            for(Group group : groups) {
                writer.write(serializeGroup(group));
                writer.newLine();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}