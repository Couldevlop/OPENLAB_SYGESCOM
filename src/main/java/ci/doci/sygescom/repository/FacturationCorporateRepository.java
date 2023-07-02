/*
package ci.doci.sygescom.repository;


import ci.doci.sygescom.domaine.FacturationCorporate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacturationCorporateRepository extends JpaRepository<FacturationCorporate, Long> {
    //List<FacturationCorporate> TfindFacturationClorporateById(long id);
   */
/* @Query(value="select B.id, B.datebl, B.ref_es , B.ref_gaz," +
            " B.designation_es as des_es, B.designation_gaz as des_gaz," +
            " B.qte_es, B.qte_gaz, C.nom as client,C.remise_essence," +
            "C.remise_gazoil, P.nom as prestataire, P.nom_chauffeur, " +
            "C.contact1, C.localisation, C.num_doc as DFE, I.pue, I.pug " +
            "from doci_prixpompe_init I, doci_bonlivraison B," +
            " doci_clientcorporates C," +
            " doci_prestataire P where C.stations_id = B.station_id " +
            "and B.prestataire_id=P.id and B.hierachie=0 and B.accepter=1" +
            " and B.id=:id",nativeQuery = true)*//*

    @Query(value = "select DISTINCT  D.id , D.datebl, D.ref_es , D.ref_gaz, " +
            "D.designation_es as des_es, D.designation_gaz as des_gaz," +
            "D.qte_es, D.qte_gaz, D.corporates_id, P.nom as prestataire, P.nom_chauffeur, " +
            " C.nom as client,C.remise_essence, C.remise_gazoil, C.contact1, C.localisation," +
            "C.id ,C.num_doc as DFE, I.pue, I.pug " +
            "from doci_prixpompe_init I, doci_bonlivraison B, " +
            "doci_clientcorporates C, doci_corporate_directe D, " +
            "doci_prestataire P where C.id = D.corporates_id " +
            "and D.accepter=1 and P.id = D.prestataire_id and D.id=:id",nativeQuery =true)
    List<FacturationCorporate> findFacturationCorp(@Param("id") long id);



}
*/
