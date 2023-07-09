package ci.doci.sygescom.domaine;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
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
}
