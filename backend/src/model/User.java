
package model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String id;
    private String username;
    private String password;
    private String profileImagePath;
    private boolean blocked;
    private List<String> blockedUsers;
    private List<String> pinnedChats;
    private List<String> archivedChats;
    private List<String> contacts;
    private int failedLoginAttempts;
    private long lockedUntil = 0;
    private long lastSeen = 0;

    public User(String id, String username, String password, String profileImagePath) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.profileImagePath = profileImagePath;
        this.blocked = false;
        this.blockedUsers = new ArrayList<>();
        this.pinnedChats = new ArrayList<>();
        this.archivedChats = new ArrayList<>();
        this.contacts = new ArrayList<>();
        this.failedLoginAttempts = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public List<String> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(List<String> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public List<String> getPinnedChats() {
        return pinnedChats;
    }

    public void setPinnedChats(List<String> pinnedChats) {
        this.pinnedChats = pinnedChats;
    }

    public List<String> getArchivedChats() {
        return archivedChats;
    }

    public void setArchivedChats(List<String> archivedChats) {
        this.archivedChats = archivedChats;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public long getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(long lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}
