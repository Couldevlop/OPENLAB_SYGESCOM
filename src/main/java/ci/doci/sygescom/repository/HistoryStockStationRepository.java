package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.HistoryStockStation;
import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.StockStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface HistoryStockStationRepository extends JpaRepository<HistoryStockStation, Long> {
    @Query(value = "select max(i.id) as maxid from HistoryStockStation i")
    Long getLastId();
    //Get stock station by station
    HistoryStockStation findHistoryStockStationByStations(Stations stations);

    List<HistoryStockStation> findHistoryStockStationByDateJourAndStationsAndMotif(LocalDate d, Stations st, String motif);
}
