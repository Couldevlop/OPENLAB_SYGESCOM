package ci.doci.sygescom.util.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NotificationWebService {

    private final Logger log = LoggerFactory.getLogger(NotificationWebService.class);

    private final RestTemplate restTemplate;

    String url = "http://10.108.254.130:8081/api/message/SendMailBase64";

    public NotificationWebService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendEmail(Mailer mailer) {
        String token = loginApiNotification();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Mailer> entity = new HttpEntity<>(mailer, headers);

        try {
            ResponseEntity<Response> response = restTemplate.postForEntity(url, entity, Response.class);
            log.info(response.getStatusCode() + "");
            log.info(response.getBody() + "");
        } catch (Exception e) {
            log.error("Une erreur s'est produite", e);
        }

    }

    private String loginApiNotification() {
        String url = "http://10.108.254.130:8081/api/Login/Authenticate";
        String login = "SEMLESS";
        String password = "semless";

        UserInfoLoginApi loginApi = new UserInfoLoginApi();
        loginApi.setLogin(login);
        loginApi.setPassword(password);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserInfoLoginApi> entity = new HttpEntity<>(loginApi, httpHeaders);

        try {
            ResponseEntity<Object> response =
                    restTemplate.postForEntity(url, entity, Object.class);

            Map map = (Map) response.getBody();
            String token = (String) map.get("Token");

            log.info(response.getBody() + "");
            log.info(token);
            return token;
        } catch (Exception e) {
            log.error("Une erreur s'est produite au cours de la connexion", e);
            return null;
        }
    }
}
