package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.ClientsComptants;
import ci.doci.sygescom.domaine.Stations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientsComptantsRepository extends JpaRepository<ClientsComptants, Long> {
    List<ClientsComptants> findClientsComptantsByStations(Stations st);
}
