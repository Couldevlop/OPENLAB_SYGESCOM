package ci.doci.sygescom.domaine;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doci_bonlivraison")
@Setter
@Getter
public class BonLivraison {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String numBL;
    private LocalDate dateBL;
    private String refGaz;
    private String refEs;
    private String designationGaz;
    private String designationEs;
    private double qteGaz;
    private double  qteEs;
    private String modeTransport;
    private String immatriculation;
    @ManyToOne
    @JoinColumn( name= "station_id")
    private Stations stations;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;
    @ManyToOne
    @JoinColumn(name = "doc_id")
    private Doc doc;
    @ManyToOne
    @JoinColumn(name = "gerant_id")
    private User gerant;
    private Boolean accepter=false;
    private String motif;
    private double saisieEssence;
    private double saisieGazoil;
    private Boolean rejeter=false;
    private Boolean hierachie = false;
   private LocalDate dateValidation;
    private Boolean cloturer = false;
    private Boolean blchauffeurgestoci = false;


}
