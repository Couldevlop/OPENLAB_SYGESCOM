package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Interim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InterimRepository extends JpaRepository<Interim, Long> {
 @Query("select u.fin from Interim u")
 List<String> findAllByFin();
 Interim findByFin(String dateFin);
}
