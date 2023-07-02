package ci.doci.sygescom.security;

import ci.doci.sygescom.domaine.Role;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.domaine.dto.UserDTO;
import ci.doci.sygescom.service.AuthenticationService;
import ci.doci.sygescom.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Component
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    HttpServletRequest request;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Tentative de connexion depuis : " + request.getRemoteAddr());

        String name = authentication.getName().toLowerCase();
        String password = authentication.getCredentials().toString();

        UserDTO userByLdapInformation = authenticationService.getUserByLdapInformation(name, password);
        if (userByLdapInformation == null) {
            log.info("Aucun utilisateur retrouvé depuis LDAP");
            return null;
        }
        User user = customUserDetailsService.findByLogin(userByLdapInformation.getLogin());

        if (user == null) {
            log.info(name + " n'existe pas dans notre base locale");
            return null;
        }


        user.setContact(userByLdapInformation.getTelBur());
        user.setUsername(userByLdapInformation.getUsername());
        user.setEmail(userByLdapInformation.getEmail());

        log.info("Debut Mise à jour info utilisateur");
        customUserDetailsService.register(user);
        log.info("Fin Mise à jour info utilisateur");

        UsernamePasswordAuthenticationToken principal =
                new UsernamePasswordAuthenticationToken(user,
                        null, getAuthorities(user));

        log.info("Connexion reussie utilisateur : " + user.getLogin() + " - "
                + user.getUsername() + " - "
                + user.getEmail());
        log.info("Machine connectée : " + request.getRemoteAddr());
        return principal;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String userRoles[] = user.getRoles()
                .stream()
                .map(Role::getName)
                .toArray(String[]::new);

        Collection<GrantedAuthority> authorities;
        authorities = AuthorityUtils.createAuthorityList(userRoles);

        return authorities;
    }
}
