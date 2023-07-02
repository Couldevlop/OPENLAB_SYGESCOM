package ci.doci.sygescom.ServiceImpl;

import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.repository.UserRepository;
import ci.doci.sygescom.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User update(Long userId, User us) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User _user = userOptional.get();
            _user.setActivated(true);
            _user.setStations(us.getStations());
            _user.setRoles(us.getRoles());
            _user.setId(userId);
            _user.setLogin(us.getLogin());
            _user.setPassword(us.getUsername());
            _user.setEmail(us.getEmail());
            _user.setContact(us.getContact());
            userRepository.save(_user);
            return _user;
        } else {
            return null;
        }
    }
}
