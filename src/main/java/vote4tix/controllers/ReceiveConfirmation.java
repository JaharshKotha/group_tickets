package vote4tix.controllers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vote4tix.Singletons;
import vote4tix.Vote4TixJDBCController;
import vote4tix.db.DatabaseManager;

import java.util.Map;

@RestController
public class ReceiveConfirmation {

    DatabaseManager dbManager = Singletons.getDbManager();
    JdbcTemplate jdbcTemplate = Singletons.getJdbcTemplate();

    @RequestMapping("/confirmPurchase/{invitationId}")
    public String getInviteDetails(@PathVariable("invitationId") String invitationId) {
        Map<String, Object> map= jdbcTemplate.queryForMap(String.format("SELECT * FROM invites WHERE inviteId='%s'",
            invitationId));
        String userId = ((Integer) map.get("userId")).toString();
        String groupId = ((Integer) map.get("groupId")).toString();
        jdbcTemplate.update(String.format("INSERT INTO confirmations (userId, groupId, inviteId) VALUES (%s, %s, %s)",
                userId, groupId, invitationId));
        return "received conf";
    }
}