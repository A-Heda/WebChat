package cli;

import model.Group;
import model.Message;
import model.User;
import services.AdminService;
import services.GroupService;
import services.UserService;

import java.util.Scanner;

public class AdminCLI {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "1234";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!username.equals(ADMIN_USERNAME)
                || !password.equals(ADMIN_PASSWORD)) {

            System.out.println("Access denied.");
            return;
        }

        AdminService adminService = new AdminService();
        UserService userService = new UserService();
        GroupService groupService = new GroupService();

        while (true) {

            System.out.println("\n===== ADMIN PANEL =====");

            System.out.println("1. Show users");
            System.out.println("2. Add user");
            System.out.println("3. Delete user");
            System.out.println("4. Show groups");
            System.out.println("5. Add member to group");
            System.out.println("6. Remove member");
            System.out.println("7. Reported messages");
            System.out.println("0. Exit");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {

                case 1:

                    for (User u : adminService.getAllUsers()) {

                        System.out.println(
                                u.getId() + " - " +
                                        u.getUsername());
                    }

                    break;

                case 2:

                    System.out.print("ID: ");
                    String id = scanner.nextLine();

                    System.out.print("Username: ");
                    String uname = scanner.nextLine();

                    System.out.print("Password: ");
                    String pass = scanner.nextLine();

                    String result = userService.register(
                            id,
                            uname,
                            pass,
                            pass);

                    System.out.println(result);

                    break;

                case 3:

                    System.out.print("User ID: ");
                    String userId = scanner.nextLine();

                    adminService.deleteUser(userId);

                    System.out.println("Deleted.");

                    break;

                case 4:

                    for (Group g : adminService.getAllGroups()) {

                        System.out.println(
                                g.getName());

                        System.out.println(
                                g.getMemberIds());
                    }

                    break;

                case 5:

                    System.out.print("Group ID: ");
                    String gid = scanner.nextLine();

                    System.out.print("User ID: ");
                    String uid = scanner.nextLine();

                    System.out.println(
                            groupService.addMember(
                                    gid,
                                    uid));

                    break;

                case 6:

                    System.out.print("Group ID: ");
                    gid = scanner.nextLine();

                    System.out.print("User ID: ");
                    uid = scanner.nextLine();

                    System.out.println(
                            groupService.removeMember(
                                    gid,
                                    uid));

                    break;

                case 7:

                    for (Message m : adminService
                            .getReportedMessages()) {

                        System.out.println(
                                m.getText());

                        System.out.println(
                                "Sender: "
                                        + m.getSenderId());
                    }

                    break;

                case 0:
                    return;
            }
        }
    }
}