package ci.doci.sygescom.repository;


import ci.doci.sygescom.domaine.BonDeCommande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BonDeCommandeRepository extends JpaRepository<BonDeCommande, Long> {
    BonDeCommande findBonDeCommandeByNumBCAndAaccepteFalse(String numbc);
    BonDeCommande findBonDeCommandeById(long id);

}
