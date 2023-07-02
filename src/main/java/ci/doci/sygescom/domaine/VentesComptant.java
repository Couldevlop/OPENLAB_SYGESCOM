package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "doci_venteComptant")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VentesComptant extends Ventes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private ClientsComptants clientsComptants;
}
