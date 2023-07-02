package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "doci_versement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Versement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double montant;
    private String commentaire;
    @ManyToOne
    private User user;
    private LocalDate dateEnreg;
}
