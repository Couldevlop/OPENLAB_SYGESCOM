package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doci_stockgestoci")
@Entity
public class Stockgestoci {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double qteGazDepot;
    private double qteEsDepot;
    private double qteGlobaleGaz;
    private double  qteGlobalEs;
    @ManyToOne
    @JoinColumn(name = "stock_init_gestoci_id")
    private StockInitGestoci stockInitGestoci;
}
