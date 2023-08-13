package ci.doci.sygescom.domaine.dto;

import ci.doci.sygescom.domaine.Stations;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "doci_indexdto")
@Entity
public class IndexDTO {
    @Id
    @GeneratedValue
    private Long id;
    private String station;
    private LocalDate dateJour;
    private int totalPrise;
}
