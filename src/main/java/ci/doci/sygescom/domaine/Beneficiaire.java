package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "doci_beneficiaire")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Beneficiaire {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String nom;
    private String prenom;
    private String fonction;
    private String contact;
    private String identification;
    private double plafonageEssence;
    private double plafonageGazoil;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private ClientsCorporates clientsCorporates;
}
