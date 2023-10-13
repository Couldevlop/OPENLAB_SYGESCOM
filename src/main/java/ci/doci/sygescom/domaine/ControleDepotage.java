package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ControleDepotage {
    @Id
    private Long id;
    private LocalDate dateDepot;
    private LocalDate dateJour;
    private double EssenceAvantDepot;
    private double EssenceApresDepot;
    private double GasoilAvantDepot;
    private double  GasoilApresDepot;
    private String station;
    private Long id_;
}
