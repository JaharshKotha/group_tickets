package vote4tix.controllers;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import vote4tix.Singletons;
import vote4tix.api.GroupRequest;
import vote4tix.db.DatabaseManager;
import vote4tix.models.Invited;


@RestController
@Configurable
public class AddGroupController {

    JdbcTemplate jdbcTemplate = Singletons.getJdbcTemplate();

    DatabaseManager dbManager = Singletons.getDbManager();

    @PostMapping("/addGroup")
    @ResponseStatus(HttpStatus.OK)
    public void addGroup(@RequestBody GroupRequest groupRequest) {
        truncateTables();
        System.out.println(groupRequest);
        for ( Invited person : groupRequest.users )
        {
            System.out.println(person);
        }
        dbManager.createGroup(groupRequest);
    }

    private void truncateTables() {
        jdbcTemplate.execute("TRUNCATE Users");
        jdbcTemplate.execute("TRUNCATE groups");
        jdbcTemplate.execute("TRUNCATE invites");
        jdbcTemplate.execute("TRUNCATE collections");
    }

}
