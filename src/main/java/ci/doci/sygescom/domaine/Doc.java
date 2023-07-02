package ci.doci.sygescom.domaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="doci_docs")
public class Doc {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String docName;
	private String docType;
	
	@Lob
	private byte[] data;

	public Doc(String docname, String contentType, byte[] bytes) {
	}
	//@OneToOne
	//private Keepself  keepself;


}
