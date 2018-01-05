package vote4tix;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import vote4tix.db.DatabaseManager;

import javax.sql.DataSource;

public class Singletons {

    public static JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            DataSource dataSource = DataSourceBuilder.create()
                .driverClassName("com.mysql.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/votetix")
                .username("vote")
                .password("tix")
                .build();

            jdbcTemplate = new JdbcTemplate(dataSource, false);
        }

        return jdbcTemplate;
    }

    public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        Singletons.jdbcTemplate = jdbcTemplate;
    }

    public static JdbcTemplate jdbcTemplate;

    public static DatabaseManager getDbManager() {
        if (dbManager == null) {
            dbManager = new DatabaseManager(jdbcTemplate);
        }

        return dbManager;
    }

    public static void setDbManager(DatabaseManager dbManager) {
        Singletons.dbManager = dbManager;
    }

    public static DatabaseManager dbManager;

}
