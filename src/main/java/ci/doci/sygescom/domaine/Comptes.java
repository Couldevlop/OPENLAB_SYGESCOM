package ci.doci.sygescom.domaine;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doci_comptes")
@Entity
@Builder
public class Comptes implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "compte")
    private List<Depenses> depensesList;
    @OneToMany(mappedBy = "compte")
    private List<Versement> versementList;
    private Double soldeVeille;
    private Double solde;
    @CreationTimestamp
    private LocalDateTime dateEnreg;
    @Column(nullable = false, unique = true)
    private String numeroCompte;
    @Column(nullable = false)
    private String banque;
}
