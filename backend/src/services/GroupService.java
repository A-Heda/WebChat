package services;

import model.Group;
import model.User;
import repositories.ChatRepository;
import repositories.GroupRepository;
import repositories.UserRepository;

import java.util.List;


public class GroupService {

    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private ChatRepository chatRepository;


    public GroupService() {
        groupRepository = new GroupRepository();
        userRepository = new UserRepository();
        chatRepository = new ChatRepository();
    }


    private String validateGroupId(String id) {

        if(id == null || id.trim().isEmpty())
            return "Group ID cannot be empty.";

        if(groupRepository.findById(id) != null)
            return "Group ID already exists.";

        return "Group ID is valid.";
    }


    private String validateGroupName(String name) {

        if(name == null || name.trim().isEmpty())
            return "Group name cannot be empty.";

        if(name.length() < 3)
            return "Group name must be at least 3 characters.";

        return "Group name is valid.";
    }


    public String createGroup(String id, String name, String adminId) {        //not used yet

        String idResponse = validateGroupId(id);
        if(!idResponse.equals("Group ID is valid."))
            return idResponse;


        String nameResponse = validateGroupName(name);
        if(!nameResponse.equals("Group name is valid."))
            return nameResponse;


        User admin = userRepository.findById(adminId);

        if(admin == null)
            return "Admin user not found.";


        Group group = new Group(id, name, adminId);

        groupRepository.saveGroup(group);
        chatRepository.createGroupChatFile(id);

        return "SUCCESS";
    }


    public String addMember(String groupId, String userId) {

        Group group = groupRepository.findById(groupId);

        if(group == null)
            return "Group not found.";


        User user = userRepository.findById(userId);

        if(user == null)
            return "User not found.";


        if(group.getMemberIds().contains(userId))
            return "User is already a member.";


        group.getMemberIds().add(userId);

        groupRepository.updateGroup(group);

        return "SUCCESS";
    }


    public String removeMember(String groupId, String userId) {

        Group group = groupRepository.findById(groupId);

        if(group == null)
            return "Group not found.";


        if(!group.getMemberIds().contains(userId))
            return "User is not a member of this group.";


        if(group.getAdminId().equals(userId))
            return "Admin cannot be removed from the group.";


        group.getMemberIds().remove(userId);

        groupRepository.updateGroup(group);

        return "SUCCESS";
    }


    public String leaveGroup(String groupId, String userId) {

        return removeMember(groupId, userId);
    }


    public String deleteGroup(String groupId, String requesterId) {

        Group group = groupRepository.findById(groupId);

        if(group == null)
            return "Group not found.";


        if(!group.getAdminId().equals(requesterId))
            return "Only the admin can delete the group.";


        groupRepository.deleteGroup(groupId);

        return "SUCCESS";
    }


    public String changeGroupName(String groupId, String newName) {

        Group group = groupRepository.findById(groupId);

        if(group == null)
            return "Group not found.";


        String response = validateGroupName(newName);

        if(!response.equals("Group name is valid."))
            return response;


        group.setName(newName);

        groupRepository.updateGroup(group);

        return "SUCCESS";
    }


    public String changeDescription(String groupId, String description) {

        Group group = groupRepository.findById(groupId);

        if(group == null)
            return "Group not found.";


        if(description == null)
            return "Description cannot be null.";


        group.setDescription(description);

        groupRepository.updateGroup(group);

        return "SUCCESS";
    }


    public String changeGroupImage(String groupId, String imagePath) {

        Group group = groupRepository.findById(groupId);

        if(group == null)
            return "Group not found.";


        if(imagePath == null || imagePath.trim().isEmpty())
            return "Image path cannot be empty.";


        group.setGroupImagePath(imagePath);

        groupRepository.updateGroup(group);

        return "SUCCESS";
    }


    public Group findGroupById(String groupId) {

        return groupRepository.findById(groupId);
    }


    public List<Group> getAllGroups() {

        return groupRepository.getAllGroups();
    }


    public boolean isAdmin(String groupId, String userId) {

        Group group = groupRepository.findById(groupId);

        if(group == null)
            return false;


        return group.getAdminId().equals(userId);
    }
}