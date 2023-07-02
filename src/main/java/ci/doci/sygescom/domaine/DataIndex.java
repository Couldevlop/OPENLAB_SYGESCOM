package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doci_data_index")
public class DataIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double cuveEssence;
    private double cuveGazoil;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateCreation;
    private String createBy;
    @ManyToOne
    private Stations stations;
    private double super1;
    private double super2;
    private  double super3;
    private double super4;
    private double gazoil1;
    private double gazoil2;
    private double gazoil3;
    private double gazoil4;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate dateJour;
}
