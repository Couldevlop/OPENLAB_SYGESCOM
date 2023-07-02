package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Doc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocRepository extends JpaRepository<Doc, Integer> {

}
