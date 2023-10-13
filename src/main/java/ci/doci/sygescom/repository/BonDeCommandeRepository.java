package ci.doci.sygescom.repository;


import ci.doci.sygescom.domaine.BonDeCommande;
import ci.doci.sygescom.domaine.BonLivraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface BonDeCommandeRepository extends JpaRepository<BonDeCommande, Long> {
    BonDeCommande findBonDeCommandeByNumBCAndAaccepteFalse(String numbc);
    BonDeCommande findBonDeCommandeById(long id);
    List<BonDeCommande> findBonDeCommandeByAaccepteIsFalse();
    List<BonDeCommande> findBonDeCommandeByAaccepteIsTrue();
    @Query(nativeQuery = true, value = "SELECT * FROM divineoil.doci_bondecommande where datebc between :d1  AND :d2;")
    List<BonDeCommande>MonthBc(String d1, String d2);


    @Query(nativeQuery = true, value = "SELECT * FROM divineoil.doci_bondecommande where  datebc between :d1  AND :d2 AND aaccepte IS true;")
    List<BonDeCommande>MonthBlByAccepterIsTrue(String d1, String d2);

    @Query(nativeQuery = true, value = "SELECT * FROM divineoil.doci_bondecommande where  datebc between :d1  AND :d2 AND aaccepte IS false;")
    List<BonDeCommande>MonthBlByAccepterIsFalse(String d1, String d2);

    @Query(nativeQuery = true, value = "SELECT * FROM divineoil.doci_bondecommande where  datebc between :d1  AND :d2 AND aaccepte IS true;")
    List<BonDeCommande>MonthBlByHierachieTrue(Date d1, Date d2);

}
