package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Builder
@Table(name = "doci_ecarts_stations")
@AllArgsConstructor
@NoArgsConstructor
public class EcartStations {
    @Id
    @GeneratedValue
    private long id;

    @OneToOne
    private Stations stations;

    private double ecartEssence;

    private double ecartGazoil;


    private double firstCuveEssence;

    private double firstCuveGazoil;

    private double lastCuveGazoil;

    private double lastCuveEssence;

    private LocalDate dateJour;

    private double totalIndexGazoil;

    private double totalIndexEssence;
}
