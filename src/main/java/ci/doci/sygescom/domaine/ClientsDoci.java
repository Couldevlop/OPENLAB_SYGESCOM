package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
//@Entity
@AllArgsConstructor
@NoArgsConstructor
//@Table(name = "doci_client_doci")
@MappedSuperclass
public  class  ClientsDoci {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String nom;
    private String email;
    private String contact1;
    private LocalDate dateEnreg;
    private String localisation;
    private String typeClient;
    private double remiseEssence;
    private double remiseGazoil;
    //private String address;
    @ManyToOne
    private Stations stations;
    @ManyToOne
    private User user;
    @NotNull(message = "il doit toujours avoir un statut")
    private Boolean cloturer = false;


}
