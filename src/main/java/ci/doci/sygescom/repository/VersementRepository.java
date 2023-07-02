package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Versement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersementRepository extends JpaRepository<Versement, Long> {
}
