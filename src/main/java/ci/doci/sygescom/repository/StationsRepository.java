package ci.doci.sygescom.repository;


import ci.doci.sygescom.domaine.Stations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StationsRepository extends JpaRepository<Stations, Long> {
    //@Query( name = "select  S.*, Z.*  from  doci_stations S, doci_zone  Z where S.ZONE_ID =Z.ID", nativeQuery = true)
    //Zone trouverZoneStation(Long id);
    @Query(value = "select max(i.id) as maxid from Stations i")
    Long getLastId();
}
