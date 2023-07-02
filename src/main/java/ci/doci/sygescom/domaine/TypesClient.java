package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "doci_type_client")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class TypesClient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
}
