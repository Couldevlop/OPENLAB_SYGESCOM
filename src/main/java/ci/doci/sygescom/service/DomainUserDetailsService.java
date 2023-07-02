package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("userDetailsService")
@Slf4j
public class DomainUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserService userService;

    public DomainUserDetailsService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Tentative de connexion");
        Optional<User> user = userRepository.findByLogin(username);
        if (!user.isPresent())
            throw new UsernameNotFoundException("User introuvable");

        log.info(user.toString());

        return user.get();
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    public User register(User user) {
        if (user != null)
            userService.createUser(user);
        return null;
    }

    public String formatMailPersonnel() {
        List<String> allEmail = userRepository.findAllEmail();
        StringBuilder personnel = new StringBuilder();
        allEmail.forEach(mail -> {
            personnel.append(mail);
            personnel.append(";");
        });

        return personnel.toString();
    }
}
