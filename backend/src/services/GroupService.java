// GroupService.java
package services;

import model.Group;
import model.User;
import java.util.*;

public class GroupService {

    private Map<String, Group> groups; // groupId -> Group

    public GroupService() {
        this.groups = new HashMap<>();
    }

    // ساخت گروه جدید
    public Group createGroup(String name, String adminId) {
        String groupId = UUID.randomUUID().toString();
        Group group = new Group(groupId, name, adminId);
        groups.put(groupId, group);
        return group;
    }

    // دریافت اطلاعات گروه
    public Group getGroup(String groupId) {
        return groups.get(groupId);
    }

    // افزودن عضو به گروه
    public boolean addMember(String groupId, String userId, String requesterId) {
        Group group = groups.get(groupId);
        if (group == null) return false;

        // فقط ادمین می‌تواند عضو اضافه کند
        if (!group.getAdminId().equals(requesterId)) {
            return false;
        }

        if (!group.getMemberIds().contains(userId)) {
            group.getMemberIds().add(userId);
            return true;
        }
        return false;
    }

    // حذف عضو از گروه
    public boolean removeMember(String groupId, String userId, String requesterId) {
        Group group = groups.get(groupId);
        if (group == null) return false;

        // فقط ادمین می‌تواند عضو حذف کند
        if (!group.getAdminId().equals(requesterId)) {
            return false;
        }

        // ادمین نمی‌تواند خودش را حذف کند
        if (userId.equals(group.getAdminId())) {
            return false;
        }

        return group.getMemberIds().remove(userId);
    }

    // خروج از گروه
    public boolean leaveGroup(String groupId, String userId) {
        Group group = groups.get(groupId);
        if (group == null) return false;

        // اگر ادمین بخواهد خارج شود، باید ابتدا ادمین را تغییر دهد
        if (userId.equals(group.getAdminId())) {
            return false;
        }

        return group.getMemberIds().remove(userId);
    }

    // تغییر ادمین گروه
    public boolean changeAdmin(String groupId, String newAdminId, String currentAdminId) {
        Group group = groups.get(groupId);
        if (group == null) return false;

        // فقط ادمین فعلی می‌تواند ادمین را تغییر دهد
        if (!group.getAdminId().equals(currentAdminId)) {
            return false;
        }

        // عضو جدید باید در گروه باشد
        if (!group.getMemberIds().contains(newAdminId)) {
            return false;
        }

        group.setAdminId(newAdminId);
        return true;
    }

    // تغییر نام گروه
    public boolean updateGroupName(String groupId, String newName, String requesterId) {
        Group group = groups.get(groupId);
        if (group == null) return false;

        // فقط ادمین می‌تواند نام گروه را تغییر دهد
        if (!group.getAdminId().equals(requesterId)) {
            return false;
        }

        group.setName(newName);
        return true;
    }

    // تغییر توضیحات گروه
    public boolean updateGroupDescription(String groupId, String description, String requesterId) {
        Group group = groups.get(groupId);
        if (group == null) return false;

        // فقط ادمین می‌تواند توضیحات را تغییر دهد
        if (!group.getAdminId().equals(requesterId)) {
            return false;
        }

        group.setDescription(description);
        return true;
    }

    // تغییر تصویر گروه
    public boolean updateGroupImage(String groupId, String imagePath, String requesterId) {
        Group group = groups.get(groupId);
        if (group == null) return false;

        // فقط ادمین می‌تواند تصویر گروه را تغییر دهد
        if (!group.getAdminId().equals(requesterId)) {
            return false;
        }

        group.setGroupImagePath(imagePath);
        return true;
    }

    // دریافت لیست اعضای گروه
    public List<String> getMembers(String groupId) {
        Group group = groups.get(groupId);
        return group != null ? new ArrayList<>(group.getMemberIds()) : new ArrayList<>();
    }

    // چک کردن عضویت در گروه
    public boolean isMember(String groupId, String userId) {
        Group group = groups.get(groupId);
        return group != null && group.getMemberIds().contains(userId);
    }

    // چک کردن ادمین بودن
    public boolean isAdmin(String groupId, String userId) {
        Group group = groups.get(groupId);
        return group != null && group.getAdminId().equals(userId);
    }

    // دریافت تمام گروه‌های یک کاربر
    public List<Group> getUserGroups(String userId) {
        List<Group> userGroups = new ArrayList<>();
        for (Group group : groups.values()) {
            if (group.getMemberIds().contains(userId)) {
                userGroups.add(group);
            }
        }
        return userGroups;
    }

    // حذف گروه (فقط توسط ادمین)
    public boolean deleteGroup(String groupId, String requesterId) {
        Group group = groups.get(groupId);
        if (group == null) return false;

        // فقط ادمین می‌تواند گروه را حذف کند
        if (!group.getAdminId().equals(requesterId)) {
            return false;
        }

        groups.remove(groupId);
        return true;
    }
}
