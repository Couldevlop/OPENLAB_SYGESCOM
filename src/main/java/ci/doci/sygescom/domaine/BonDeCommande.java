package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doci_Bondecommande")
public class BonDeCommande {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private String numBC;
    private LocalDate dateBC;
    private String vendeur;
    private String contactVendeur;
    private String typeOperation;
    private String adresseLivraison;
    private String refEs;
    private String refGaz;
    private String designationEs;
    private String designationGaz;
    private double puGaz;
    private double puEs;
    private double qteEs;
    private double qteGaz;
    private String condEs;
    private String condGaz;
    private double mtnGaz;
    private double mtnEs;
    private double mtnGlobal;
    @ManyToOne
    @JoinColumn(name = "doc_id")
    private Doc doc;
    private boolean aaccepte = false;

}
