package ci.doci.sygescom.domaine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table( name = "doci_stations")
public class Stations {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private String essence;
    private String gasoil;
    @ManyToOne
    Zone zone;
    @OneToMany
    private List<Indexes> indexeList;
    private LocalDate date;
    private String contactGerant;
    private int numEmploye;
    private int numPompe;
    private int nbrPompeEssence;
    private int nbrPompeGazoil;

    private double stockAlerteEssence;
    private double stockAlerteGazoil;
    private double stockCritiqueEssence;
    private  double stockCritiqueGazoil;
    private boolean statut;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private List <Mouvements>mouvementsList;




}
