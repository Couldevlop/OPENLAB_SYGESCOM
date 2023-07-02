package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.ClientsCorporates;
import ci.doci.sygescom.domaine.ClientsDoci;
import ci.doci.sygescom.domaine.CorporateDirecte;
import ci.doci.sygescom.domaine.Stations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientCorporateRepository extends JpaRepository<ClientsCorporates, Long> {
    List<ClientsCorporates>findClientsCorporatesByStations(Stations st);
    ClientsCorporates findByStations(Stations st);
    ClientsCorporates findClientsCorporatesByContact1(String num);
    List<ClientsCorporates> findClientsCorporatesByTypeClient(String mot);


    //List<ClientsCorporates> findByClientsCorporate(CorporateDirecte cd);

}
