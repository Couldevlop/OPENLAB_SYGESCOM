package ci.doci.sygescom.domaine;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "doci_stockinitstation")
public class StockInitStation {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private long qteinitgaz;
    private long qteinites;
    @ManyToOne
    @JoinColumn(name = "stations_id")
    private Stations stations;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDate dateEng;
    private boolean parametre = false;

}
