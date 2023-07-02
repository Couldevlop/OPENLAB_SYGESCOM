package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.exception.BadActionException;
import ci.doci.sygescom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Set<User> getUsers() {
        Set<User> users = new HashSet<>();
        userRepository.findAll().iterator().forEachRemaining(users::add);

        return users.stream()
                .filter(user -> !user.getLogin().equals("Aucun"))
                .collect(Collectors.toSet());
    }

    public User findByLogin(String login) {
        Optional<User> optionalUser = userRepository.findByLogin(login);
        if (!optionalUser.isPresent())
            throw new RuntimeException("Utilisateur avec Login : " + login + " n'a pas été trouvé.");
        return optionalUser.get();
    }

    public User findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent())
            throw new RuntimeException("Utilisateur avec Login : " + String.valueOf(id)
                    + " n'a pas été trouvé.");

        return optionalUser.get();
    }

    public User createUser(User user) {
        if (user != null) {
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteUser(Long id) {
        User user = this.findById(id);
        if (user != null)
            userRepository.delete(user);
    }


    public boolean _doVerifyIfItsFirstConnexion(String login){

            Optional<User> user = userRepository.findByLogin(login);
            if (user.isPresent()){
                User use = user.get();
                if(use.isFirstConnection() == true){
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }

    }


    public User _doCreateNewPassword(User u){
        if(_doVerifyIfItsFirstConnexion(u.getLogin()) == true){
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User user = userRepository.findByLogin(u.getLogin()).orElse(null);
            user.setPassword(passwordEncoder.encode(u.getPassword()));
            user.setUsername(u.getUsername());
            user.setStations(u.getStations());
            user.setEmail(u.getEmail());
            user.setFirstConnection(false);
            user.setStatut(u.isStatut());
           return  userRepository.save(user);

        }else {
            return u;
        }
    }



    public BadActionException _doDetecteErrorAction(Object t, String message, Object t1){
       BadActionException error = new BadActionException();
       error.setT(t);
       error.setT1(t1);
       error.setMessage(message);
       return  error;
    }
}
