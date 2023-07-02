package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

//@Entity
//@Data @NoArgsConstructor @AllArgsConstructor
//@Table(name="doci_Facturation_corporate")
public class FacturationCorporate {
    //@Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;
    private LocalDate datebl;
   // @Column(name = "refessence")
    private String refEs;
    private String refGaz;
    private String desEs;
    private String desGaz;
    private Double qteEs;
    private Double qteGaz;
    private String client;
    private Double remiseEssence;
    private Double remiseGazoil;
    private String prestataire;
    private String NomChauffeur;
    private String contact1;
    private String localisation;
    private String dfe;
    private Double pue;
    private Double pug;

}
