package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doci_indexes")
@Builder
public class Indexes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double cuveEssence;
    private double cuveGazoil;
    //private String description;
    //private double essenceIndexeFin;
   // private double gazoilIndexeFin;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateCreation;
    private double stockCuveEssence;
    private double stockCuveGazoil;
    private String createBy;
    @ManyToOne
    private Stations stations;
    private double super1;
    private double super2;
    private  double super3;
    private double super4;
    private double gazoil1;
    private double gazoil2;
    private double gazoil3;
    private double gazoil4;
    private double DifPriseEss;//difference Cuve fin - debut fin Essence
    private double DifPriseGaz;//difference Cuve fin - cuve debut Gazoil
    private double QtiteRestantEssence; //Quantité globale restante essence
    private double QtiteRestantGasoil; //Quantité globale restante gasoil
    private boolean initIndex = true;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateJour;
    private int prise=0;
    private boolean etat = true;
}
