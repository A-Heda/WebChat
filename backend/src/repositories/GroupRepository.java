package repositories;

import model.Group;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupRepository {

    private static final String GROUP_FILE = "backend/database/groups.txt";

    private String serializeGroup(Group group) {

    String membersJoinedId = String.join(",", group.getMemberIds());

    String image = group.getGroupImagePath() == null ?
            "" : group.getGroupImagePath();

    String description = group.getDescription() == null ?
            "" : group.getDescription();


    return group.getId() + "|" +
            group.getName() + "|" +
            group.getAdminId() + "|" +
            membersJoinedId + "|" +
            image + "|" +
            description;
}

    private Group deserializeGroup(String line) {

    String[] parts = line.split("\\|" , -1);

    if(parts.length != 6)
        return null;


    List<String> memberIds = new ArrayList<>();

    if(!parts[3].isEmpty()) {
        memberIds.addAll(Arrays.asList(parts[3].split(",")));
    }


    Group group = new Group(
            parts[0],
            parts[1],
            parts[2],
            memberIds
    );


    group.setGroupImagePath(parts[4]);
    group.setDescription(parts[5]);

    return group;
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

    public List<Group> getUserGroups(String userId){

        List<Group> result = new ArrayList<>();
        List<Group> groups = getAllGroups();

        for(Group group : groups) {
            if(group.getMemberIds().contains(userId)) {
            result.add(group);
            }
        }
    return result;
    }
    
}