package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static javax.persistence.FetchType.EAGER;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "doci_zone")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private String localisation;
    private String description;
    private LocalDate dateCreation;
    private String createBy;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Stations> stationsList;


}
