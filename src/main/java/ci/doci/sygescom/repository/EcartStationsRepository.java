package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.EcartStations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EcartStationsRepository extends JpaRepository<EcartStations, Long> {
    List<EcartStations> findEcartStationsByDateJour(LocalDate localDate);
    EcartStations findByStationsId(Long id);

    @Query(nativeQuery = true,  value = "SELECT max(I.Id) from doci_ecarts_stations I where I.stations_id =:id")
    Long lastIdInd(@Param("id") long st);
}
