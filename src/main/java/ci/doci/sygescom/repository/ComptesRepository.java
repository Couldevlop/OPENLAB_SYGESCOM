package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Comptes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComptesRepository extends JpaRepository<Comptes, Long> {
    Optional<Comptes>findByNumeroCompte(String numeroCompte);
}
