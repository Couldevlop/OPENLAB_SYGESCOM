package ci.doci.sygescom.mapper;

import ci.doci.sygescom.domaine.Comptes;
import ci.doci.sygescom.domaine.Depenses;
import ci.doci.sygescom.domaine.dto.DepensesDto;
import ci.doci.sygescom.domaine.dto.DepensesSendDto;

public class DepensesMapper {

    public static DepensesDto mapToDepensesDto(Depenses depenses){
        return DepensesDto.builder()
                .auteur(depenses.getAuteur())
                .commentaire(depenses.getCommentaire())
                .dateEnreg(depenses.getDateEnreg())
                .doc(depenses.getDoc())
                .libelle(depenses.getLibelle())
                .montant(depenses.getMontant())
                .numTransaction(depenses.getNumTransaction())
                .id(depenses.getId())
                .banque(depenses.getBanque())
                .compte(depenses.getCompte())
                .build();
    }



    public static Depenses mapToDepenses(DepensesSendDto dto){
        return Depenses.builder()
                .auteur(dto.getAuteur())
                .commentaire(dto.getCommentaire())
                .dateEnreg(dto.getDateEnreg())
                .libelle(dto.getLibelle())
                .montant(dto.getMontant())
                .numTransaction(dto.getNumTransaction())
                .doc(dto.getDoc())
                .id(dto.getId())
                .banque(dto.getBanque())
                .compte(Comptes.builder()
                        .id(dto.getCompte())
                        .build())
                .build();
    }


}
