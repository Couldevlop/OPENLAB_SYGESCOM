package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.DataIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataIndexRepository extends JpaRepository<DataIndex, Long> {
    Optional<DataIndex> findById(Long id);
}
