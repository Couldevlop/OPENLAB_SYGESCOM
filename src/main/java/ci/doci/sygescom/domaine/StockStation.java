package ci.doci.sygescom.domaine;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "doci_stockStation")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StockStation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double essenceInit;
    private double gazoilInit;
    private double essenceDepot;
    private double gazoilDepot;
    private LocalDate dateDepot;
    private double ecartEssence=0;
    private double ecartGazoil=0;
    @ManyToOne
    @EqualsAndHashCode.Exclude()
    @JoinColumn(name = "stations_id")
    private Stations stations;
    private double qteGlobaleEssence;
    private double qteGlobaleGazoile;
    @ManyToOne
    @JoinColumn(name = "stock_init_station_id")
    private StockInitStation stockInitStation;
    private int alerte = 0;
    private int nbrIndex=0;
    private LocalDate dateJour;

}
