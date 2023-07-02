package ci.doci.sygescom.domaine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String departement;
    private String email;
    private String id;
    private String login;
    private String telBur;
    private String username;
    private String codeExploitation;
}
