package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Indexes;
import ci.doci.sygescom.domaine.Stations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface IndexesRepository extends JpaRepository<Indexes, Long> {
    List<Indexes> findIndexesByStationsIdAndDateJour(Long id, LocalDate jour);
    List<Indexes> findIndexesByDateJour(LocalDate jour);
    Indexes findEssenceIndexeFinAndGazoilIndexeFinAndCuveEssenceAndCuveGazoilOrderByStations(Stations st);
    Indexes findDistinctByStations(Stations stations);
    @Query(nativeQuery = true,  value = "SELECT max(I.Id) from doci_indexes I where I.stations_id =:id")
    Long lastId(@Param("id")long st);
    @Query(nativeQuery = true,  value = "SELECT max(I.Id) from doci_indexes I where I.stations_id =:id")
    long lastIdInd(@Param("id") long st);

    @Query(nativeQuery = true,  value = "SELECT max(I.Id) from doci_indexes I")
    Long lastId();
    @Query(nativeQuery = true,  value = "SELECT max(I.Id)FROM `doci_indexes`as I WHERE I.stations_id =:id and I.date_creation=CURRENT_DATE;")
    long lastIdIndex(@Param("id")long st);

    List<Indexes> findIndexesByStationsId(long id);

    List<Indexes>findIndexesByStationsAndDateCreation(Stations st, LocalDate date);
    @Query(value = "SELECT * FROM `doci_indexes` WHERE date_creation = CURRENT_DATE ", nativeQuery = true)
    List<Indexes>listeIndexes();


    List<Indexes> findIndexesByDateJourBetween(LocalDate date1, LocalDate dta2);

}
