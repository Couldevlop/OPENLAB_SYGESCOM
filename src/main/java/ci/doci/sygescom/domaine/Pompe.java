package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
//@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doci_pompe")
public class Pompe {
    //@Id
   // @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String libellee;
    private String description;
    private LocalDate date;
    private String createBy;
}
