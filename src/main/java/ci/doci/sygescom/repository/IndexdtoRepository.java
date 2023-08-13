package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.dto.IndexDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IndexdtoRepository extends JpaRepository<IndexDTO, Long> {
    @Query(nativeQuery = true, value = "SELECT * from doci_indexdto")
    List<IndexDTO> reportingJour();

}
