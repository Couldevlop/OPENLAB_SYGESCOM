package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "doci_Corporate_directe")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorporateDirecte {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private String numBL;
    private LocalDate dateBL;
    private String refGaz;
    private String refEs;
    private String designationGaz;
    private String designationEs;
    private long qteGaz;
    private long  qteEs;
    private String modeTransport;
    private String immatriculation;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    private ClientsCorporates corporates;
    @ManyToOne
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;
    @ManyToOne
    @JoinColumn(name = "doc_id")
    private Doc doc;
  /*  @ManyToOne
    @JoinColumn(name = "gerant_id")
    private User gerant;*/
    private Boolean accepter=false;
    private String motif;
    private long saisieEssence;
    private long saisieGazoil;
    private Boolean rejeter=false;
    private Boolean hierachie = false;
    private long ecartEssence;
    private long ecartGazoil;
    @NotNull(message = "il doit toujours avoir un statut")
    private Boolean cloturer=false;


}
