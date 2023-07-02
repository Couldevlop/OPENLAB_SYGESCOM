package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    @Query(value = "SELECT Z.id, Z.nom from ci_zone Z,ci_pompe A where Z.id = A.zone_id and A.id =:id", nativeQuery = true)
    Zone trouverZoneParPompe(@Param("id") Long id);

    @Query(value = "SELECT Z.id, Z.nom from doci_zone Z,doci_stations A where Z.id = A.zone_id and A.id =:id", nativeQuery = true)
    Zone trouverZoneParStation(@Param("id") final Long id);

}
