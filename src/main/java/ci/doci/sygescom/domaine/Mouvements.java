package ci.doci.sygescom.domaine;

import ci.doci.sygescom.Enum.PriseIndexe;
import lombok.Data;

import javax.persistence.*;
import ci.doci.sygescom.domaine.VentesComptant;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "doci_mouvements")
@Data
public class Mouvements implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private LocalDateTime dateEnrg;
    @OneToMany
    private List<VentesComptant> ventesComptantList;
    @OneToMany
    private List<VentesCorporate> ventesCorporateList;
    @ManyToOne
    private Stations stations;
    private boolean etat = false;

}
