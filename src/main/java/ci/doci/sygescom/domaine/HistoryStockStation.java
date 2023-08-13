package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "doci_historystockstation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class HistoryStockStation {
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
    @JoinColumn(name = "stations_id")
    private Stations stations;
    private double qteGlobaleEssence;
    private double qteGlobaleGazoile;
    private double stockAvanTransacEss;
    private double stockAvanTransacGas;
    private String motif;
    private int alerte = 0;
    private int nbrIndex=0;
    private LocalDate dateJour;

}
