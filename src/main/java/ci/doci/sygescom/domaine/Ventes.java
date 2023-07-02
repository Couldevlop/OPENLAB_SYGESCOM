package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
//@Entity
@AllArgsConstructor
@NoArgsConstructor
//@Table(name = "doci_client_doci")
@MappedSuperclass
public class Ventes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double puG;
    private double puE;
    private long litrageEssence;
    private long litrageGazoil;
    private double mntEssenceRemise;
    private double mntGazoilRemise;
    private LocalDate dateEng;
    private double mntglobalEssence;
    private double mntglobaGazoil;
    private double mntTotal;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @Transient
    @JoinColumn(name = "stations_id")
    private Stations stations;
}
