package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "doci_clientscomptants")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientsComptants extends ClientsDoci{
    private String typePiece;
    private String numPiece;
}
