package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.StockStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StockStationRepository extends JpaRepository<StockStation, Long> {
    @Query(value = "select max(i.id) as maxid from StockStation i")
    Long getLastId();


   StockStation findStockStationByStations(Stations stations);
    List<StockStation> findStockStationByDateJour(LocalDate localDate);
    StockStation findStockStationByStationsId(Long id);



}
