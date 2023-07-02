package ci.doci.sygescom.domaine.dto;

import ci.doci.sygescom.domaine.Stations;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexeDTO {
    private Long id;
    private double cuveEssence;
    private double cuveGazoil;
    private String description;
    private double essenceIndexeFin;
    private double gazoilIndexeFin;
    private LocalDate dateCreation;
    private String createBy;
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
    private LocalDate dateJour;
}
