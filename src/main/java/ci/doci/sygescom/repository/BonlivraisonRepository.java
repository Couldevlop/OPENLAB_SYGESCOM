package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.BonLivraison;
import ci.doci.sygescom.domaine.Stations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface BonlivraisonRepository extends JpaRepository<BonLivraison, Long> {
    List<BonLivraison>findByStations(Stations stations);
    List<BonLivraison>findBonLivraisonByAccepterFalseAndDateBLAndStationsAndMotifNull(LocalDate dt, Stations id);

    List<BonLivraison>findBonLivraisonByAccepterFalse();

    List<BonLivraison>findByAccepterFalseAndRejeterIsFalse();
    List<BonLivraison>findByStationsAndHierachieTrueAndMotifIsNotNull(Stations stations);
    List<BonLivraison>findByStationsAndAccepterTrueAndHierachieFalse(Stations stations);

    List<BonLivraison>findBonLivraisonByStationsAndAccepterFalseAndRejeterIsFalse(Stations stations);

    List<BonLivraison>findBonLivraisonByStationsAndAccepterFalseAndRejeterFalse(Stations st);
    List<BonLivraison>findBonLivraisonByHierachieTrueAndRejeterTrue();
    List<BonLivraison>findBonLivraisonByCloturerIsTrue();


    List<BonLivraison> findBonLivraisonByNumBL(String numbl);

    List<BonLivraison> findBonLivraisonByAccepterIsTrue();

    @Query(nativeQuery = true, value = "SELECT * FROM divineoil.doci_bonlivraison where  datebl between :d1  AND :d2 AND accepter IS true;")
    List<BonLivraison>MonthBlByAccepterIsTrue(String d1, String d2);

    @Query(nativeQuery = true, value = "SELECT * FROM divineoil.doci_bonlivraison where  datebl between :d1  AND :d2 AND accepter IS false;")
    List<BonLivraison>MonthBlByAccepterIsFalse(String d1, String d2);

    @Query(nativeQuery = true, value = "SELECT * FROM divineoil.doci_bonlivraison where  datebl between :d1  AND :d2 AND hierachie IS true;")
    List<BonLivraison>MonthBlByHierachieTrue(String d1, String d2);

}
