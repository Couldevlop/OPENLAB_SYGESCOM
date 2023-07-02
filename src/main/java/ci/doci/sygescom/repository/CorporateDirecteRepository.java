package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CorporateDirecteRepository extends JpaRepository<CorporateDirecte, Long> {
    List<CorporateDirecte>findCorporateDirecteByAccepterIsTrue();
    List<CorporateDirecte>findCorporateDirecteByAccepterIsFalse();
    List<CorporateDirecte>findCorporateDirecteByAccepterIsTrueAndEcartEssenceNotNullOrEcartGazoilIsNotNull();
    @Query(value = "SELECT * FROM doci_corporate_directe WHERE ACCEPTER=1 AND ecart_essence !=0 AND ecart_gazoil != 0", nativeQuery = true)
    List<CorporateDirecte>ecartCorporateDirecte();

    // List<FacturationCorporate> findById();
}
