package vote4tix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import vote4tix.Singletons;
import vote4tix.db.DatabaseManager;
import vote4tix.models.Invitation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@RestController
public class ConfirmationController {

    DatabaseManager dbManager = Singletons.getDbManager();
    JdbcTemplate jdbcTemplate = Singletons.getJdbcTemplate();

    @RequestMapping("/inviteLanding/{inviteId}")
    public String inviteLanding(@PathVariable String inviteId) {
        File confirmationPageHtmlFile = null;
        try {
            confirmationPageHtmlFile = ResourceUtils.getFile("classpath:assets/html/confirmPage.html");
        } catch (FileNotFoundException e) {
            return "Down for the count " + e.getMessage();
        }

        return formatEventTemplate(getInviteId(Integer.parseInt(inviteId)), confirmationPageHtmlFile);
    }

    /*
        String eventId;
        String artist;
        String eventImage;
    */
    public Invitation getInviteId(Integer inviteId) {
        return dbManager.getInviteInformation(inviteId);
    }

    /*
        Username,
        event name,
        event cost,
        event artist
        event image
     */
    public String formatEventTemplate(Invitation invitation, File confirmationPageHtmlFile) {
        String confirmationPageHtml = null;
        try {
            confirmationPageHtml = new Scanner(confirmationPageHtmlFile).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            return "Down for the count " + e.getMessage();
        }
        String eventName = getName(invitation.getEventId());
        System.out.println(findNonConfirmedUsers(invitation.getGroupId()));
        return String.format(confirmationPageHtml,
            invitation.getEmail(),
            eventName,
            "{EVENT_COST_FROM_CART}", // get cost
            buildConfirmedUsersHtml(findConfirmedUsers(invitation.getGroupId())),
            eventName,
            getImage(invitation.getEventId())
        );
    }

    private String getImage(String eventId) {
        ResponseEntity<HashMap> values = new RestTemplate().getForEntity(String.format("https://app.ticketmaster" +
                ".com//discovery/v2/events/%s/images.json?apikey=gZJKzlGFcQjlaYsDC59Uk1mrD1uC27kV", eventId),
            HashMap.class);

        List<Object> images = (List<Object>) values.getBody().get("images");
        System.out.println(images);
        HashMap<String, Object> objs = (HashMap<String, Object>) images.get(0);
        System.out.println(objs);
        String url = (String) objs.get("url");
        return url;
    }

    private String getName(String eventId) {
        ResponseEntity<HashMap> values = new RestTemplate().getForEntity(String.format("https://app.ticketmaster" +
                ".com/discovery/v2/events/%s.json?apikey=gZJKzlGFcQjlaYsDC59Uk1mrD1uC27kV", eventId),
            HashMap.class);
        return (String) values.getBody().get("name");
    }

    private List<String> findConfirmedUsers(String groupId) {
        List<String> users = new ArrayList<>();
        jdbcTemplate.query(String.format("SELECT userId from confirmations WHERE groupId='%s'", groupId), (RowMapper<Void>)
            (resultSet, i) -> {
            String userEmail = ((Integer) resultSet.getObject("userId")).toString();
            users.add(userEmail);
            return null;
        });
        return getUserNames(users);
    }

    private List<String> getUserNames(List<String> users) {
        List<String> userNames = new ArrayList<>();
        for (String userId : users) {
            String userName = (String) jdbcTemplate.queryForMap(String.format("Select * from users where userId='%s'", userId)).get("email");
            userNames.add(userName);
        }
        return userNames;
    }

    private String findNonConfirmedUsers(String groupId) {
        String nonConfirmed = "";
        List<Map<String, Object>> map = jdbcTemplate.queryForList(String.format("SELECT " +
            "cnf.userId, " +
            "inv.userId, " +
            "inv.groupId " +
            "FROM confirmations cnf " +
            "RIGHT JOIN invites inv " +
            "ON cnf.userId = inv.userId " +
            "WHERE cnf.userId IS NULL " +
            "AND inv.groupId='%s'", groupId));
        List<String> userIds = new ArrayList<>();
        map.forEach(mapItem -> userIds.add(((Integer) mapItem.get("userId")).toString()));

        List<String> userNames = getUserNames(userIds);
        for (int ix = 0; ix < userNames.size() -1; ix++ ) {
            String listItem = "<li> <a href=\"http://10.40.184.119:8080/nudgeUser/" + userIds.get(ix) +
                "\">" + userNames.get(ix) + "</a></li>";
            nonConfirmed = nonConfirmed + listItem;
        }
        return nonConfirmed;
    }

    private String buildConfirmedUsersHtml(List<String> confirmedUsers) {
        String usersHtml = "";
        for (String user : confirmedUsers) {
            usersHtml = usersHtml + "<li>" + user + "</li>";
        }
        return usersHtml;
    }
}