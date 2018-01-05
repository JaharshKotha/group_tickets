package vote4tix.db;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.jdbc.core.JdbcTemplate;
import vote4tix.api.GroupRequest;
import vote4tix.models.Invitation;
import vote4tix.models.Invited;

import java.util.Map;

public class DatabaseManager {

    JdbcTemplate jdbcTemplate;

    public DatabaseManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createGroup(GroupRequest groupRequest) {
        String groupCreateSQL = String.format("INSERT INTO groups (eventId) values ('%s') on duplicate key update eventId=eventId",
            groupRequest
                .eventId);
        jdbcTemplate.execute(groupCreateSQL);

        Map<String, Object> qMap = jdbcTemplate.queryForMap(String.format("SELECT DISTINCT * FROM groups WHERE eventId='%s'",
            groupRequest.eventId));
        System.out.println(qMap);

        Integer groupId = (Integer) qMap.get("groupId");


        for (Invited invited : groupRequest.users) {
            Integer userId = createUser(invited);
            Integer invitedId = createInvitation(invited, userId, groupId);
            sendText(invitedId, invited.email, invited.mobile);
        }
    }

    public String sendText(Integer inviteId, String email, String mobile) {
        final String ACCOUNT_SID = "AC6018d4eb02580ad2c966b95c6a923a3e";
        final String AUTH_TOKEN = "e5019bbf7322f76b3812cccb54e61df3";

        final String fromPhone = "+14806300894";

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message
            .creator(new PhoneNumber(mobile), new PhoneNumber(fromPhone),
                String.format("Hey, %s! Your friends would like for you to check out this event! " +
                        "http://10.40.184.119:8080/inviteLanding/%s",
                    email, inviteId))
            .create();
        return "sms_good";
    }

    private Integer createInvitation(Invited invited, Integer userId, Integer groupId) {
        String createUserSQL = "INSERT INTO invites (userId, groupId) VALUES ('%s', '%s') on duplicate key update userId=userId";
        jdbcTemplate.update(String.format(createUserSQL, userId, groupId));
        System.out.println(userId);
        System.out.println(groupId);
        return (Integer) jdbcTemplate.queryForMap(String.format("SELECT DISTINCT * FROM invites WHERE userId='%s' AND groupId='%s'", userId,
            groupId)).get("inviteId");
    }

    private Integer createUser(Invited invited) {
        String createUserSQL = "INSERT INTO Users (email, mobile) VALUES ('%s', '%s') on duplicate key update email=email";
        jdbcTemplate.update(String.format(createUserSQL, invited.email, invited.mobile));

        return (Integer) jdbcTemplate.queryForMap(String.format("SELECT DISTINCT * FROM Users WHERE email='%s' AND mobile='%s'", invited
                .email,
            invited.mobile)).get("userId");
    }

    public Invitation getInviteInformation(Integer inviteId) {
        Map<String, Object> invitesTable = jdbcTemplate.queryForMap(
            String.format("SELECT * FROM invites WHERE inviteId='%s'", inviteId.toString()));
        Integer userId = (Integer) invitesTable.get("userId");
        Map<String, Object> UsersTable = jdbcTemplate.queryForMap(String.format("SELECT * FROM Users where userId='%s'", userId.toString()));
        String email = (String) UsersTable.get("email");
        String groupId = ((Integer) invitesTable.get("groupId")).toString();

        Map<String, Object> groupsTable = jdbcTemplate.queryForMap(String.format("SELECT * FROM groups WHERE groupId='%s'", groupId
            .toString()));
        String eventId = (String) groupsTable.get("eventId");

        return new Invitation(eventId, groupId, inviteId, email);
    }
}