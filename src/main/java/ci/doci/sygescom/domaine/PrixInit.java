package ci.doci.sygescom.domaine;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "doci_prixpompe_init")
public class PrixInit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double puE;
    private double puG;
    private LocalDate dateEng;
}
