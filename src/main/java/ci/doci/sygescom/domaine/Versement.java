package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
    private String banque;
    private String station;
    private String commentaire;
    private String deposant;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEnreg;
    private String numBorderau;
    @ManyToOne
    @JoinColumn(name = "doc_id")
    private Doc doc;
}
