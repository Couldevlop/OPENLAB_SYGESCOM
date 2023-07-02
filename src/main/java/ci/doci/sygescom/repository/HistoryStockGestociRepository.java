package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.HistoryStockgestoci;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryStockGestociRepository extends JpaRepository<HistoryStockgestoci, Long> {
}
