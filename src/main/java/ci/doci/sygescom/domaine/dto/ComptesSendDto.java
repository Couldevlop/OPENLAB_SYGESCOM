package ci.doci.sygescom.domaine.dto;

import ci.doci.sygescom.domaine.Depenses;
import ci.doci.sygescom.domaine.Versement;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComptesSendDto implements Serializable {
    private Long id;
    private Double soldeVeille;
    private Double solde;
    @CreationTimestamp
    private LocalDateTime dateEnreg;
    @Column(nullable = true, unique = true)
    private String numeroCompte;
    private String banque;

}
