package ci.doci.sygescom.repository;


import ci.doci.sygescom.domaine.Mouvements;
import ci.doci.sygescom.domaine.Stations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MouvementsRepository extends JpaRepository<Mouvements, Long> {
    List<Mouvements> findMouvementsByStations(Stations station);
    List<Mouvements> findMouvementsByStationsId(long id);

    @Query(nativeQuery = true,  value = "SELECT max(I.Id) from doci_mouvements I")
    Long lastId();
}

