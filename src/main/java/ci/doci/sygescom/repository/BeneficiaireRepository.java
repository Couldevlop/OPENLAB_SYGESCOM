package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Beneficiaire;
import ci.doci.sygescom.domaine.ClientsCorporates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaireRepository extends JpaRepository<Beneficiaire, Long> {

    List<Beneficiaire> findBeneficiaireByClientsCorporates(ClientsCorporates clientsCorporates);

    Beneficiaire findBeneficiaireByContact(String contact);


}
