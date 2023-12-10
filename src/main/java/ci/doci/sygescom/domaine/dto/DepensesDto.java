package ci.doci.sygescom.domaine.dto;

import ci.doci.sygescom.domaine.Comptes;
import ci.doci.sygescom.domaine.Doc;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepensesDto {
    private Long id;
    private String libelle;
    private double montant;
    private String commentaire;
    private String auteur;
    private LocalDate dateEnreg;
    private String numTransaction;
    private Doc doc;
    private Comptes compte;
    private String banque;
}
