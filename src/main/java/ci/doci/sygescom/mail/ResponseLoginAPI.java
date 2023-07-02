package ci.doci.sygescom.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLoginAPI {
    private String Login;
    private String Nom;
    private String Password;
    private String Token;
    private String DateCreation;
    private String isActive;
    private String Profil;
    private String TokenExpire;
    private Map responseMsg;
}
