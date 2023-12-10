package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Indexes;
import ci.doci.sygescom.domaine.IndexesTemp;
import ci.doci.sygescom.domaine.Stations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface IndexesTempRepository extends JpaRepository<IndexesTemp, Long> {
    List<IndexesTemp> findIndexesByStationsIdAndDateJour(Long id, LocalDate jour);
    List<IndexesTemp> findIndexesByDateJour(LocalDate jour);

    @Query(nativeQuery = true, value = "SELECT * FROM doci_indexes_tmp where date_jour between :d1 and :d2")
    List<IndexesTemp> findIndexesByDateJours(@Param("d1") LocalDate jour1, @Param("d2") LocalDate jour2);
    IndexesTemp findEssenceIndexeFinAndGazoilIndexeFinAndCuveEssenceAndCuveGazoilOrderByStations(Stations st);
    IndexesTemp findDistinctByStations(Stations stations);
    @Query(nativeQuery = true,  value = "SELECT max(I.Id) from doci_indexes_tmp I where I.stations_id =:id")
    Long lastId(@Param("id")long st);
    @Query(nativeQuery = true,  value = "SELECT max(I.Id) from doci_indexes_tmp I where I.stations_id =:id")
    long lastIdInd(@Param("id") long st);

    @Query(value = " SELECT * FROM doci_indexes_tmp L WHERE L.date_jour BETWEEN :date1  AND :date2", nativeQuery = true)
    List<IndexesTemp> DataOfMonth(@Param("date2") String date2, @Param("date1") String date1);

    @Query(nativeQuery = true,  value = "SELECT max(I.Id) from doci_indexes_tmp I")
    Long lastId();
    @Query(nativeQuery = true,  value = "SELECT max(I.Id)FROM `doci_indexes_tmp`as I WHERE I.stations_id =:id and I.date_creation=CURRENT_DATE;")
    long lastIdIndex(@Param("id")long st);

    List<IndexesTemp> findIndexesByStationsId(long id);

    List<Indexes>findIndexesByStationsAndDateCreation(Stations st, LocalDate date);
    @Query(value = "SELECT * FROM `doci_indexes_tmp` WHERE date_creation = CURRENT_DATE ", nativeQuery = true)
    List<IndexesTemp>listeIndexes();


    List<IndexesTemp> findIndexesByDateJourBetween(LocalDate date1, LocalDate dta2);

    //*****************************fonction pour la tache planifiée de rapatriement des données de index_tmp vers index********************************

    @Query(value = "SELECT stations_id FROM divineoil.doci_indexes_tmp group by stations_id", nativeQuery = true)
    List<Long>lesIndex();

    @Query(value = "SELECT * FROM divineoil.doci_indexes_tmp where date_jour between :d1 AND :d2 ", nativeQuery = true)
    List<IndexesTemp>listeIndexSemaine(LocalDate d1, LocalDate d2);

    @Query(value = "SELECT * FROM divineoil.doci_indexes_tmp where date_jour between :d1 AND :d2 AND stations_id= :idStation", nativeQuery = true)
    List<IndexesTemp>listeIndexSemaineParStationSelectionnee(LocalDate d1, LocalDate d2, Long idStation);

   List<IndexesTemp> findByStationsIdAndDateJour(Long idStation, LocalDate dateJour);


}
