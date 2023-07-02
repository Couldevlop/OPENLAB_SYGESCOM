package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.StockInitGestoci;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockInitGestociRepository extends JpaRepository<StockInitGestoci, Long> {
    @Query(value = "select max(i.id) as maxid from StockInitGestoci i")
    Long getLastId();
}
