package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.BonDeCommande;
import ci.doci.sygescom.repository.BonDeCommandeRepository;
//import ci.doci.sygescom.repository.FacturationCorporateRepository;
import ci.doci.sygescom.service.DocStorageService;
import ci.doci.sygescom.service.JspFacturationCorporateService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping()
public class FactureGeneratedController {
    final private JspFacturationCorporateService jspFacturationCorporateService;
//    final private FacturationCorporateRepository facturationCorporateRepository;
    final private DocStorageService docStorageService;
    final private BonDeCommandeRepository bonDeCommandeRepository;

    private final Path rootTempLocation = Paths.get("C:\\Users\\Public\\webservice\\sysgescom\\Templates");
    private final Path rootPdfLocation = Paths.get("C:\\Users\\Public\\webservice\\sysgescom\\PDF_Genereted");
    String pathTemp = rootTempLocation.toString();
    String pathPdf = rootPdfLocation.toString();

    public FactureGeneratedController(JspFacturationCorporateService jspFacturationCorporateService, DocStorageService docStorageService, BonDeCommandeRepository bonDeCommandeRepository) {
        this.jspFacturationCorporateService = jspFacturationCorporateService;
       // this.facturationCorporateRepository = facturationCorporateRepository;
        this.docStorageService = docStorageService;
        this.bonDeCommandeRepository = bonDeCommandeRepository;
    }



/*    @GetMapping("/facturation/blCorporateAccepte/{id}/imp")
    public ResponseEntity<byte[]> generateReport( @PathVariable("id") long id) throws JRException {

        return jspFacturationCorporateService.generateReport(id, pathTemp, "doci",pathPdf ,"Facture_Corporate_"+id, "Facturation corporate Directe");

    }*/
    /***************** 24/12/2022*/
    @GetMapping( "/assistante/bc/{id}/imp")
    public ResponseEntity<byte[]> generateReportBc(@PathVariable("id") long id) throws JRException {
        // List<BonDeCommande> fact = bonDeCommandeRepository.bonDeCommandeSirById(id);
        //List<Employee> employees = reportRepository.findAll();
        // List<BonDeCommande> bc= bonDeCommandeRepository.bonDeCommandeSirById(id);
        //ne jamais utiliser <Optional> comme type de retour
        BonDeCommande bc = bonDeCommandeRepository.findBonDeCommandeById(id);
        return  jspFacturationCorporateService.generateReportBc(id, pathTemp, "doci_BonCdeSir",pathPdf ,"BON DE COMMANDE"+id, "Bon de commande SIR", bc);
    }
}
