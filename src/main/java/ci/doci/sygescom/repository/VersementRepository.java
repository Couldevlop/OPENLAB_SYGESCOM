package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.Versement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VersementRepository extends JpaRepository<Versement, Long> {
    List<Versement> findVersementByStation(Stations st);

    List<Versement> findVersementByStation(String station);
}
