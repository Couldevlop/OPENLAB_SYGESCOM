package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Table(name = "doci_clientcorporates")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientsCorporates extends ClientsDoci{
    private double plafonageEssence;//Qte de fluide mensuel
    private double plafonageGazoil;
    private String numDoc;
    private int nbre_beneficiaire;
    private String interlocuteur;
    private String typeClient;
    @OneToMany
    private List<Beneficiaire>beneficiaireList;

}
