package model;

public class User {

    private String id;

    private String username;

    private String password;

    private String profileImagePath;

    private boolean blocked;

    public User(String id , String username , String password , String profileImagePath) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.profileImagePath = profileImagePath;
        blocked = false;
    }

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public String getProfileImagePath () {
        return profileImagePath;
    }

    public void setProfileImagePath (String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public boolean isBlocked () {
        return blocked;
    }

    public void setBlocked (boolean blocked) {
        this.blocked = blocked;
    }
    
}