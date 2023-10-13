package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.Doc;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.domaine.Versement;
import ci.doci.sygescom.repository.DocRepository;
import ci.doci.sygescom.repository.VersementRepository;
import ci.doci.sygescom.service.DocStorageService;
import ci.doci.sygescom.service.VersementService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Controller
public class VersementController {
    private final VersementService versementService;
    private final VersementRepository versementRepository;
    private final DocRepository docRepository;
    private final DocStorageService docStorageService;

    public VersementController(VersementService versementService, VersementRepository versementRepository, DocRepository docRepository, DocStorageService docStorageService) {
        this.versementService = versementService;
        this.versementRepository = versementRepository;
        this.docRepository = docRepository;
        this.docStorageService = docStorageService;
    }


    @GetMapping("/gerant/versement")
    private String getVersementTemplate(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("Versement", new Versement());
        String nomStation = user.getStations().getNom();
        model.addAttribute("listVersement", versementRepository.findVersementByStation(nomStation));
        model.addAttribute("AllVersement", versementRepository.findAll());
        return "versement";
    }


    @PostMapping("/gerant/saveVersement")
    public String saveVersement(@Valid @ModelAttribute Versement versement,
                                @AuthenticationPrincipal User user,
                                @RequestParam MultipartFile files) throws IOException {
        Doc doc =docStorageService.saveFile(files);
        versement.setDoc(doc);
        doc.setDocName(files.getName());
        doc.setDocType(files.getContentType());
        doc.setData(files.getBytes());
        docRepository.save(doc);
        versement.setStation(user.getStations().getNom());
        versementService.addVersement(versement);
        return "redirect:/gerant/versement";
    }


    @GetMapping("/gerant/downloadFile/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Integer fileId){
        if(fileId == null){
           return  null;
        }
        Doc doc = docStorageService.getFile(fileId).get();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getDocType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment:filename=\""+doc.getDocName()+"\"")
                .body(new ByteArrayResource(doc.getData()));
    }

}
