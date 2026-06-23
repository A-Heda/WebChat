package repositories;

import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String USERS_FILE = "backend/database/users.txt";

    private String serializeUser(User user) {
        return user.getId() + "|" +
                user.getUsername() + "|" +
                user.getPassword() + "|" +
                user.getProfileImagePath();
    }

    private User deserializeUser(String line) {

        String[] parts = line.split("\\|" , -1);

        if(parts.length != 4) {
            return null;
        }

        return new User(
                parts[0],
                parts[1],
                parts[2],
                parts[3]
        );
    }

    public void saveUser(User user) {

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {

            writer.write(serializeUser(user));
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        try(BufferedReader reader =  new BufferedReader(new FileReader(USERS_FILE))) {

            String line;

            while((line = reader.readLine()) != null) {

                User user = deserializeUser(line);

                if(user != null) {
                    users.add(user);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

    public User findByUsername(String username) {

        List<User> users = getAllUsers();

        for(User user : users) {

            if(user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }

    public User findById(String id) {

        List<User> users = getAllUsers();

        for(User user : users) {

            if(user.getId().equals(id)) {
                return user;
            }
        }

        return null;
    }

    public void updateUser(User updatedUser) {

        List<User> users = getAllUsers();

        for(int i = 0; i < users.size(); i++) {

            if(users.get(i).getId().equals(updatedUser.getId())) {

                users.set(i, updatedUser);
                break;
            }
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {

            for(User user : users) {

                writer.write(serializeUser(user));
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}