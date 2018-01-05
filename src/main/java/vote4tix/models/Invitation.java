package vote4tix.models;

import java.util.List;

public class Invitation {
    String eventId;
    String groupId;
    Integer inviteId;
    String email;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getInviteId() {
        return inviteId;
    }

    public void setInviteId(Integer inviteId) {
        this.inviteId = inviteId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Invitation(String eventId, String groupId, Integer inviteId, String email) {
        this.eventId = eventId;
        this.groupId = groupId;
        this.inviteId = inviteId;
        this.email = email;
    }

    public List<String> getConfirmedUsers() {
        String confirmedUsersSQL = "SELECT * FROM ";
        List<String> confirmedUsers = null;
        return confirmedUsers;
    }

    public List<String> getUnconfirmedUsers() {
        return null;
    }
}
