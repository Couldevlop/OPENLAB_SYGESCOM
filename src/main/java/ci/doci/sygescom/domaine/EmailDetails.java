package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Lob;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Class
public class EmailDetails {

    // Class data members
    private String recipient;
    @Lob
    private String msgBody;
    private String subject;
    private String attachment;
}