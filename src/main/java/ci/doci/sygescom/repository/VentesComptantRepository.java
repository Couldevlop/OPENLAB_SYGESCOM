package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.ClientsComptants;
import ci.doci.sygescom.domaine.VentesComptant;
import ci.doci.sygescom.domaine.Stations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentesComptantRepository extends JpaRepository<VentesComptant, Long> {
    List<VentesComptant> findVentesComptantByClientsComptants(ClientsComptants cli);
    //List<VentesComptant> findVentesComptantByStations(Stations st);

}
