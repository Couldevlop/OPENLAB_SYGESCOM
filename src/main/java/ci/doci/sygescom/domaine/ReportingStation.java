package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doci_reporting_station")
public class ReportingStation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double globalEssence;
    private double globalGasoil;
    private double sortieEseence;
    private double sortieGsoil;
    private double ecartEssenceJour;
    private double ecatGasoilJour;
    private LocalDate dateJour;
    @OneToOne
    private Stations stations;
    private int nbrIndex;
}
