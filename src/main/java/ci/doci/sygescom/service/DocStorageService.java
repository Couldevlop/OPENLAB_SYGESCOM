package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.Doc;
import ci.doci.sygescom.repository.DocRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class DocStorageService {
  @Autowired
  private DocRepository docRepository;

	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private final Path rootLocation = Paths.get("C:\\Users\\Public\\webservice\\sysgescom");
	//private final Path rootLocation = Paths.get("filestorage");




  
  public Doc saveFile(MultipartFile file) {
	  String docname = file.getOriginalFilename();
	  try {
		  Files.copy(file.getInputStream(),
				  this.rootLocation.resolve(file.getOriginalFilename()),
				  StandardCopyOption.REPLACE_EXISTING);
		  Doc doc = new Doc(docname,file.getContentType(),file.getBytes());
		  return docRepository.save(doc);
	  }
	  catch(Exception e) {
		  e.printStackTrace();
	  }
	  return null;
  }
  public Optional<Doc> getFile(Integer fileId)
  {
	  return docRepository.findById(fileId);
  }
  public List<Doc> getFiles(){
	  return docRepository.findAll();
  }
}
