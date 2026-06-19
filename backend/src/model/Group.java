package model;

import java.util.List;
import java.util.ArrayList;


public class Group {

    private String id;

    private String name;

    private String adminId;

    private List<String> memberIds;

    private String groupImagePath;

    

    public Group(String id, String name, String adminId) {
        this.id = id;
        this.name = name;
        this.adminId = adminId;
        this.memberIds = new ArrayList<>();
        memberIds.add(adminId);
    }

    public Group(String id, String name, String adminId , List<String> memberIds) {
    this.id = id;
    this.name = name;
    this.adminId = adminId;
    this.memberIds = memberIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getGroupImagePath() {
        return groupImagePath;
    }

    public void setGroupImagePath(String groupImagePath) {
        this.groupImagePath = groupImagePath;
    }

}