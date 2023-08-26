package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doci_VentesCorporates")
public class VentesCorporate extends Ventes{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    @JoinColumn(name = "corporate_id")
    private ClientsCorporates clientsCorporates;
    @ManyToOne
    @JoinColumn(name = "doc_id")
    private Doc doc;
    private boolean gerantValidate=false;
    private  boolean advValidate=false;
    private String nomAvdValider;

}
