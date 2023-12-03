package ci.doci.sygescom.domaine;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "doci_depenses")
public class Depenses implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String libelle;
    private double montant;
    private String commentaire;
    private String auteur;
    @CreationTimestamp
    private LocalDate dateEnreg;
    @Column(nullable = true)
    private String numTransaction;
    @ManyToOne
    @JoinColumn(name = "doc_id", nullable = true)
    private Doc doc;
    @ManyToOne
    @JoinColumn(name = "compte_id", nullable = false)
    private Comptes compte;
    private String banque;
}
