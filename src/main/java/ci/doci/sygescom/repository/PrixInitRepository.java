package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.PrixInit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PrixInitRepository extends JpaRepository<PrixInit, Long> {
    @Query(nativeQuery = true, value = "SELECT max(T.id) as LastId  FROM doci_prixpompe_init T")
    Long lastId();
}
