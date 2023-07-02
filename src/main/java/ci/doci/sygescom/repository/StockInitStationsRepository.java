package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.StockInitStation;
import ci.doci.sygescom.domaine.StockStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockInitStationsRepository extends JpaRepository<StockInitStation, Long>{
    @Query(value = "select max(i.id) as maxid from StockInitStation i")
    Long getLastId();

    StockInitStation findStockInitStationByStations(Stations st);
}
