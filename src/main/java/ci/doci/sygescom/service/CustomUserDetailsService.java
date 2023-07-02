package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.Role;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.exception.UserNotFoundException;
import ci.doci.sygescom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByLogin(username);
        if (!optionalUser.isPresent())
            throw new UsernameNotFoundException("Ce compte n'existe pas");

        return optionalUser.get();
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findUserSysDba(Role role) {
        if (role == null)
            throw new RuntimeException();

        return userRepository.findByRoles(role);
    }

    public User register(User user) {
        if (user == null)
            throw new UserNotFoundException("User not found");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode("12345"));

        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getLogin().equals("Aucun"))
                .collect(Collectors.toList());
    }

}
