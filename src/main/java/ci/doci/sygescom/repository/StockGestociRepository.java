package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Stockgestoci;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockGestociRepository extends JpaRepository<Stockgestoci, Long> {
    @Query(value = "select max(i.id) as maxid from Stockgestoci i")
    long getLastId();
}
