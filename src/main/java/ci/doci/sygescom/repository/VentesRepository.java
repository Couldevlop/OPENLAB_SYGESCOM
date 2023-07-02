package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Ventes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentesRepository extends JpaRepository<Ventes, Long> {
}
