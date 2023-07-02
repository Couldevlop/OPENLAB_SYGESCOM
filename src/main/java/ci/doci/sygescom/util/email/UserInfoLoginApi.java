package ci.doci.sygescom.util.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoLoginApi {
    private String login;
    private String password;
}
