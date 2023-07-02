package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthenticationService {
    private final RestTemplate restTemplate;

    @Autowired
    public AuthenticationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserDTO getUserByLdapInformation(String login, String password) {

        /*
        String url = "http://10.108.255.40:7004/BNI_LdapAuthentication/api/services/" +
                "authentification/{login}/{password}";
         */
        String url = "http://10.108.255.202:7006/" +
                "BNI_LdapAuthentication/api/services/authentification/{LOGIN}/{PASSWORD}";
        //String url = "http://10.108.255.202:7006/BNI_LdapAuthentication/api/services/authentificationLDAP/{LOGIN}/{PASSWORD}";
        Map<String, String> uri = new HashMap<>();
        uri.put("LOGIN", login);
        uri.put("PASSWORD", password);

        //uri.put("login", login);
        //uri.put("password", password);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDTO> entity = new HttpEntity<>(httpHeaders);

        try {
            ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                    UserDTO.class, uri);

            String email = response.getBody().getEmail();
            log.info("Email Utilisateur AD : " + email);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }
}
