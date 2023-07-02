package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.User;
import org.springframework.stereotype.Service;

@Service
public interface UtilisateurService {
    User update(Long userId, User us);
}
