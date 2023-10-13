package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.ControleDepotage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface ControlDepotageRepository extends JpaRepository<ControleDepotage, Long> {

    @Query(value = "SELECT DISTINCT *  from divineoil.controle_depotage WHERE date_jour=:jour  AND station=:station", nativeQuery = true)
    List<ControleDepotage> dataQueryPOfStation(@Param("jour") LocalDate jour, @Param("station") String station);

    ControleDepotage findByDateJourAndStationAndDateDepot(LocalDate d, String station, LocalDate d2);

    @Query(value = "DELETE FROM divineoil.doci_indexes WHERE stations_id =:id AND date_jour >=:localDate", nativeQuery = true)
    void deleteligneIndexOfStation(@Param("id") Long id, @Param("localDate") LocalDate localDate);

    @Query(value = "DELETE FROM divineoil.doci_historystockstation WHERE stations_id =:id AND date_jour >=:localDate", nativeQuery = true)
    void deleteligneHistoOfStation(@Param("id") Long id, @Param("localDate") LocalDate localDate);

    @Query(value = "DELETE FROM divineoil.doci_data_index WHERE stations_id =:id AND date_jour >=:localDate", nativeQuery = true)
    void deleteligneInDataIndexOfStation(@Param("id") Long id, @Param("localDate") LocalDate localDate);

    @Query(value = "DELETE FROM divineoil.doci_ecarts_stations WHERE stations_id =:id AND date_jour >=:localDate", nativeQuery = true)
    void deleteligneEcartOfStation(@Param("id") Long id, @Param("localDate") LocalDate localDate);

    @Query(value = "select * from divineoil.controle_depotage where id_ =:id", nativeQuery = true)
    ControleDepotage dataQuery(@Param("id") Long id);
}
