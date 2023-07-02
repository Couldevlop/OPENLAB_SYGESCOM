package ci.doci.sygescom.domaine;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "doci_Stockinitgestoci")
public class StockInitGestoci {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double qteGazPhysique;
    private double qteEsPhysique;
    private double qteTheoriqueGaz;
    private double qteTheoriqueEs;
    private LocalDate dateEng;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private boolean parametre=false;

}
