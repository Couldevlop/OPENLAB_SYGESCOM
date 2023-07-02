/*package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Pompe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;


public interface PompeRepository extends JpaRepository<Pompe, Long> {
   // Optional<Agences> findByCodeAgence(String codeAgence);
    Optional<Pompe> findByCreateBy(String login);
    //List<Agences>findAgencesByZone(Zone id);
   // @Query(value = "select * from doci_pompe where nom_agence NOT IN('SERVICE COURRIER 1','SERVICE COURRIER 2','SERVICE COURRIER 3','SERVICE COURRIER 4','SERVICE COURRIER 5','SERVICE COURRIER 6','SERVICE COURRIER 7')", nativeQuery = true)
    List<Pompe> allAgence();





}
*/