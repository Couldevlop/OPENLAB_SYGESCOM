package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogAction {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private LocalDate localDate;
    private String user;
    private String role;
    private String nomAction;
    private String actionRealisee;
    private String impactAction;
    private String station;
    

    @Override
    public String toString() {
        return "LogAction{" +
                "id=" + id +
                ", localDate=" + localDate +
                ", user='" + user + '\'' +
                ", role='" + role + '\'' +
                ", nomAction='" + nomAction + '\'' +
                ", actionRealisee='" + actionRealisee + '\'' +
                ", impactAction='" + impactAction + '\'' +
                ", station='" + station + '\'' +
                '}';
    }
}
