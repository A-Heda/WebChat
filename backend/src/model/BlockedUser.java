package model;

public class BlockedUser {

    private String ownerId;
    private String blockedId;

    public BlockedUser(String ownerId, String blockedId) {
        this.ownerId = ownerId;
        this.blockedId = blockedId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getBlockedId() {
        return blockedId;
    }
}
