package vote4tix.controllers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import vote4tix.api.GroupRequest;
import vote4tix.db.DatabaseManager;

import javax.sql.DataSource;
import javax.websocket.server.PathParam;
import java.net.URI;
import java.nio.charset.Charset;


@RestController
@Configurable

public class AuthController {
    @RequestMapping(value="/auth/{user}/{password}", method = RequestMethod.GET, produces = { "application/json" })
    public String auth(@PathVariable String user, @PathVariable String password) throws Exception{
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
//        try{
        System.out.println(user+password);
            response = restTemplate.exchange(new URI("https://uapi-intqa.ticketmaster.net/tap/v2/member/wallet?standard=MCC&api-key=1BF36674-5453-3A13-E053-73EA490A9812"),
                    HttpMethod.GET, new HttpEntity<String>(createHeaders(user,password)), String.class);
//        }
//        catch(Exception e){}
        System.err.println(response);
        return response.getBody();
    }
    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
            set("Accept", "application/json");
            set("Content-Type", "application/json");
        }};
    }
}
