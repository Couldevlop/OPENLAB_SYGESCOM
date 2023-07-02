package ci.doci.sygescom.util.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mailer {
    private String messageObject;
    private String cc;
    private String subject;
    private String messageType;
    private String recipientEmail;
    private String applicationId;
    private String[] baseString;
    private String[] filename;
}
