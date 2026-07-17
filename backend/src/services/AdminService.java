package services;

import model.Group;
import model.Message;
import model.User;
import repositories.ChatRepository;
import repositories.GroupRepository;
import repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminService {

    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private ChatRepository chatRepository;
    private UserService userService;
    private GroupService groupService;

    public AdminService() {
        userRepository = new UserRepository();
        groupRepository = new GroupRepository();
        chatRepository = new ChatRepository();
        userService = new UserService();
        groupService = new GroupService();
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public List<Group> getAllGroups() {
        return groupRepository.getAllGroups();
    }

    public void deleteUser(String userId) {

        List<User> users = userRepository.getAllUsers();

        users.removeIf(user -> user.getId().equals(userId));

        userRepository.overwriteUsers(users);
    }

    public List<Message> getReportedMessages() {

        List<Message> reported = new ArrayList<>();

        for (Group g : groupRepository.getAllGroups()) {

            List<Message> messages = chatRepository.getAllMessages(
                    "group_" + g.getId());

            for (Message m : messages) {
                if (!m.getReportedBy().isEmpty()) {
                    reported.add(m);
                }
            }
        }

        for (String chatId : chatRepository.getAllPrivateChatIds()) {

            List<Message> messages = chatRepository.getAllMessages(chatId);

            for (Message m : messages) {
                if (!m.getReportedBy().isEmpty()) {
                    reported.add(m);
                }
            }
        }

        return reported;
    }

    public String addUser(String id,
            String username,
            String password) {

        return userService.register(
                id,
                username,
                password,
                password);
    }

    public String addUserToGroup(String groupId,
            String userId) {

        return groupService.addMember(
                groupId,
                userId);
    }

    public String removeUserFromGroup(String groupId,
            String userId) {

        return groupService.removeMember(
                groupId,
                userId);
    }

    public String createGroup(String id, String name, String adminId) {
    return groupService.createGroup(id, name, adminId);
    }

    public String deleteGroup(String groupId) {
        try {
            Group group = groupRepository.findById(groupId);
            if (group == null) {
                return "Group not found";
            }

            groupRepository.deleteGroup(groupId);
            chatRepository.deleteGroupChatFile(groupId);

            return "Group deleted successfully";
        } catch (Exception e) {
        return "Error deleting group: " + e.getMessage();
        }
    }
}
