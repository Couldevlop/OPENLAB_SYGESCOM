package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Depenses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepensesRepository extends JpaRepository<Depenses, Long> {
}
