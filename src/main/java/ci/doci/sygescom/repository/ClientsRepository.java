package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.ClientsComptants;
import ci.doci.sygescom.domaine.ClientsCorporates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientsRepository extends JpaRepository<ClientsCorporates, Long> {
}
