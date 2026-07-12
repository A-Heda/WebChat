package services;

import model.User;
import repositories.UserRepository;

public class UserService {
    private UserRepository userRepository;

    public UserService() {
        userRepository = new UserRepository();
    }

    private String validateId(String id) {

        if (id == null || id.trim().isEmpty())
            return "ID cannot be empty";

        if (userRepository.findById(id) != null)
            return "ID already exists";

        return "Id is valid.";
    }

    private String validateUsername(String username) {

        if (username == null || username.trim().isEmpty())
            return "Username cannot be empty";

        if (username.length() < 3)
            return "Username must be at least 3 characters";

        if (userRepository.findByUsername(username) != null)
            return "Username already exists";

        return "Username is valid.";
    }

    private String validatePassword(String password, String username) {

        if (password == null)
            return "Password cannot be empty.";

        if (password.length() < 8)
            return "Password must be at least 8 characters.";

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialCharacter = false;

        char[] passArray = password.toCharArray();
        for (char ch : passArray) {
            if (Character.isUpperCase(ch))
                hasUpperCase = true;

            else if (Character.isLowerCase(ch))
                hasLowerCase = true;

            else if (Character.isDigit(ch))
                hasDigit = true;

            else if ("!@#$%^&*".indexOf(ch) != -1)
                hasSpecialCharacter = true;
        }

        if (!hasUpperCase)
            return "Password must contain at leat one UpperCase character.";
        if (!hasLowerCase)
            return "Password must contain at leat one LowerCase character.";
        if (!hasDigit)
            return "Password must contain at leat one Digit.";
        if (!hasSpecialCharacter)
            return "Password must contain at leat one Special character, like: @, #, $, !, ...";
        if (password.contains(username))
            return "Password must not contain username";
        else
            return "Password is valid.";
    }

    public String register(String id, String username, String password, String repeatPassword) {

        String usernameValidateResponse = validateUsername(username);
        if (!usernameValidateResponse.equalsIgnoreCase("Username is valid."))
            return usernameValidateResponse;

        String passwordValidateResponse = validatePassword(password, username);
        if (!passwordValidateResponse.equalsIgnoreCase("Password is valid."))
            return passwordValidateResponse;

        String idValidateResponse = validateId(id);
        if (!idValidateResponse.equalsIgnoreCase("Id is valid."))
            return idValidateResponse;

        if (!password.equals(repeatPassword))
            return "Repeating the password went wrong.";

        User user = new User(id, username, password, null);
        userRepository.saveUser(user);

        return "SUCCESS";
    }

    public String changeUserId(String oldId,
            String newId) {

        User user = userRepository.findById(oldId);

        if (user == null)
            return "User not found.";

        if (userRepository.findById(newId) != null)
            return "ID already exists.";

        user.setId(newId);

        userRepository.deleteUser(oldId);

        userRepository.saveUser(user);

        return "SUCCESS";
    }

    public String login(String username, String password) {

        if (username == null || username.trim().isEmpty())
            return "Username cannot be empty";

        if (password == null || password.isEmpty())
            return "Password cannot be empty";

        User user = userRepository.findByUsername(username);
        if (user == null)
            return "User not found.";

        if (!user.getPassword().equals(password))
            return "Wrong password";

        return "SUCCESS";

    }

    public String deleteAccount(String userId, String password) {

        User user = userRepository.findById(userId);

        if (user == null)
            return "User not found.";

        if (!user.getPassword().equals(password))
            return "Wrong password.";

        userRepository.deleteUser(userId);

        return "SUCCESS";
    }

    public User findUserById(String id) {
        return userRepository.findById(id);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String changePassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId);
        if (user == null)
            return "User not found.";

        if (!user.getPassword().equals(oldPassword))
            return "Old password is incorrect.";

        String passwordValidateResponse = validatePassword(newPassword, user.getUsername());
        if (!passwordValidateResponse.equalsIgnoreCase("Password is valid."))
            return passwordValidateResponse;

        if (oldPassword.equals(newPassword))
            return "New password should be different.";

        user.setPassword(newPassword);
        userRepository.updateUser(user);
        return "SUCCESS";
    }

    public String changeUsername(String userId, String newUsername) {

        User user = userRepository.findById(userId);

        if (user == null)
            return "User not found.";

        if (userRepository.findByUsername(newUsername) != null)
            return "Username already exists.";

        user.setUsername(newUsername);

        userRepository.updateUser(user);

        return "SUCCESS";
    }

    public String updateProfileImage(String userId, String imagePath) {
        User user = userRepository.findById(userId);
        if (user == null)
            return "User not found.";

        user.setProfileImagePath(imagePath);
        userRepository.updateUser(user);
        return "SUCCESS";
    }
}