package ci.doci.sygescom.domaine.dto;

import ci.doci.sygescom.domaine.Depenses;
import ci.doci.sygescom.domaine.Versement;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComptesDto implements Serializable {
    private Long id;
    private List<Depenses> depensesList;
    private List<Versement> versementList;
    private Double soldeVeille;
    private Double solde;
    @CreationTimestamp
    private LocalDateTime dateEnreg;
    @Column(nullable = true, unique = true)
    private String numeroCompte;
    @NotEmpty(message = "ce nom de la baque ne dois pas être vide")
    private String banque;
}
