package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.ClientsCorporates;
import ci.doci.sygescom.domaine.VentesCorporate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VentesCorporateRepository extends JpaRepository<VentesCorporate, Long> {

  List<VentesCorporate>findVentesCorporateByClientsCorporates(ClientsCorporates cli);
 List<VentesCorporate> findByAdvValidateIsFalse();



}
