package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.repository.*;
import ci.doci.sygescom.service.DocStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;


@Controller
@Slf4j
public class BonDeCommandeController {

    private final BonDeCommandeRepository bonDeCommandeRepository;
    private final DocStorageService docStorageService;
    private final DocRepository docRepository;
    private final StockGestociRepository stockGestociRepository;
    private final HistoryStockGestociRepository historyStockGestociRepository;
    private final StockInitGestociRepository stockInitGestociRepository;
    private final LogActionRepository logActionRepository;
    private final Path rootLocation = Paths.get("filestorage");

    public BonDeCommandeController(BonDeCommandeRepository bonDeCommandeRepository, DocStorageService docStorageService, DocRepository docRepository, StockGestociRepository stockGestociRepository, StockInitGestociRepository stockInitGestociRepository, HistoryStockGestociRepository historyStockGestociRepository, StockInitGestociRepository stockInitGestociRepository1, LogActionRepository logActionRepository) {
        this.bonDeCommandeRepository = bonDeCommandeRepository;
        this.docStorageService = docStorageService;
        this.docRepository = docRepository;
        this.stockGestociRepository = stockGestociRepository;
        this.historyStockGestociRepository = historyStockGestociRepository;
        this.stockInitGestociRepository = stockInitGestociRepository1;
        this.logActionRepository = logActionRepository;
    }


    @GetMapping("/assistante/listbc")
    public String creerBC(Model model){
        model.addAttribute("bc",bonDeCommandeRepository.findAll());
        model.addAttribute("BonDeCommande", new BonDeCommande());
        return "bc";
    }

    @PostMapping("/assistante/saveBc")
    public String saveBc(@AuthenticationPrincipal User user,
                         @ModelAttribute("BonDeCommande") BonDeCommande bc,
                         @RequestParam MultipartFile files,
                         @RequestParam("qteEs") double qteEs,
                         @RequestParam("puEs") double puEs,
                         @RequestParam("qteGaz")double qteGaz,
                         @RequestParam("puGaz")double puGaz,
                         Model model) throws IOException {
        Doc doc = docStorageService.saveFile(files);
        bc.setDateBC(LocalDate.now());
        bc.setQteEs(qteEs);
        bc.setPuEs(puEs);
        bc.setPuGaz(puGaz);
        bc.setQteGaz(qteGaz);
        double mtnEssence= puEs * qteEs;
        double mtnGaz=puGaz * qteGaz;
        double mtnGlobal = mtnEssence + mtnGaz;
        bc.setMtnEs(mtnEssence);
        bc.setMtnGaz(mtnGaz);
        bc.setMtnGlobal(mtnGlobal);
        bc.setDoc(doc);
        docRepository.save(doc);
        bonDeCommandeRepository.save(bc);
        LogAction logAction1 = logActionRepository.save(new LogAction(0,LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Création de BC","Enregistrement de BC pour la SIR","Ajout de BC dans la base de donnée et afficher dans la liste de tous les BC",user.getStations().getNom() ));
        log.info(logAction1.toString());
        log.info("Nom de l'acteur: " +  logAction1.getUser() + "<br>" +
                "Action réalisée: " + logAction1.getActionRealisee() + "<br>" +
                "Auteur: " + logAction1.getUser() + "<br>" + "Role de l'auteur: " + logAction1.getRole() + "<br>" +
                "Date Operation: " + logAction1.getLocalDate() + "<br>" + logAction1.getNomAction() + "<br>" +
                "Impacte de l'action" + logAction1.getImpactAction() + "<br>" + logAction1.getStation());
        return "redirect:/assistante/listbc";
    }


    @GetMapping(path = "/assistante/bc/{id}/imprimer")
    public String getOrderPage(Model model, @PathVariable("id") String id){
        BonDeCommande bc = bonDeCommandeRepository.findById(Long.parseLong(id)).orElse(null);
        double mnt1 = bc.getMtnEs();
        double mnt2 = bc.getMtnGaz();
        //String mnttt= bc.getMtnEs() + bc.getMtnGaz();
        double Resultat=mnt1+mnt2;
        String res = String.valueOf(Resultat);
        model.addAttribute("bcHelper", bc);
        model.addAttribute("total", res);
        return "order";
    }

    @GetMapping("/assistante/validation")
    public String bcParNumBC(){
        return "validationBLSIR";
    }


    @GetMapping("/assistante/bc/{id}/ok")
    public String validation(Model model, @PathVariable("id") String id){
        BonDeCommande bcOk = bonDeCommandeRepository.findById(Long.parseLong(id)).orElse(null);
        bcOk.setAaccepte(true);
        bonDeCommandeRepository.save(bcOk);
        model.addAttribute("messageOk", "la commande a bien été validée");
        return "validationBLSIR";
    }

    @GetMapping("/assistante/bc/{id}/blsir")
    public String joindreBL(Model model, @PathVariable("id") String id){
        BonDeCommande bcOk = bonDeCommandeRepository.findById(Long.parseLong(id)).orElse(null);
        model.addAttribute("bc", bcOk);
        return "joindreBLSIR";
    }

    @PostMapping("/assistante/jondrebl")
    public String joindreCmd(@RequestParam("id") String id,
                             @RequestParam MultipartFile files,
                             @AuthenticationPrincipal User user, Model model) throws IOException {
        Doc doc = docStorageService.saveFile(files);
        long lasgestoInit = stockInitGestociRepository.getLastId();
        Optional<BonDeCommande> BC = bonDeCommandeRepository.findById(Long.parseLong(id));
        Optional<StockInitGestoci> STGES = stockInitGestociRepository.findById(lasgestoInit);
        if(BC == null && STGES == null){
            model.addAttribute("message", "Désolé une erreur s'est produite à l'ajout de la pièces jointe");
            return "error";
        }else{
            BonDeCommande bc = BC.get();
            StockInitGestoci stockInitGestoci = STGES.get();
            HistoryStockgestoci historyStockGestoci = new HistoryStockgestoci();
            bc.setDoc(doc);
            doc.setDocName(files.getName());
            doc.setDocType(files.getContentType());
            doc.setData(files.getBytes());
            docRepository.save(doc);
            bc.setAaccepte(true);
            bonDeCommandeRepository.save(bc);
            //si la table de parametrage != null
            if (stockInitGestoci != null && stockInitGestoci.isParametre() == false) {
                Stockgestoci stockgestoci = new Stockgestoci();
                stockgestoci.setQteGlobaleGaz(stockInitGestoci.getQteTheoriqueGaz());
                stockgestoci.setQteGlobalEs(stockInitGestoci.getQteTheoriqueEs());
                stockgestoci.setStockInitGestoci(stockInitGestoci);

                /************* Recuoeration des dernières valeurs de stock de gestoci*******************/
                double lastGobalEssence = stockInitGestoci.getQteTheoriqueEs();
                double lastGlobalGazoil = stockInitGestoci.getQteTheoriqueGaz();
                stockgestoci.setQteGlobalEs(bc.getQteEs() + lastGobalEssence);
                stockgestoci.setQteGlobaleGaz(bc.getQteGaz() + lastGlobalGazoil);
                stockgestoci.getStockInitGestoci().setParametre(true);
                stockGestociRepository.save(stockgestoci);

                /***************** Sauvegarde des abciennes valeurs de gestoci ***********************/

                historyStockGestoci.setStockInitGestoci(stockInitGestoci);
                historyStockGestoci.setQteEsDepot(bc.getQteEs());
                historyStockGestoci.setQteGazDepot(bc.getQteGaz());
                historyStockGestoci.setQteGlobaleGaz(lastGlobalGazoil);
                historyStockGestoci.setQteGlobalEs(lastGobalEssence);
                historyStockGestoci.setMotif("Sauvegarde de stock de GESTOCI avant ravitaillement en station");
                historyStockGestociRepository.save(historyStockGestoci);
                logActionRepository.save(new LogAction(0,LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Ajout de pieces jointes","la pièce ajoutéé est le BL livré par la SIR","Validation du BC et impactation des soldes de GESTOCI",user.getStations().getNom() ));
                return "redirect:listbc";
            } else {
                /************* Recuoeration des dernières valeurs de stock de gestoci*******************/
                long stgestoId = stockGestociRepository.getLastId();
                Stockgestoci stgesto = stockGestociRepository.findById(stgestoId).orElse(null);
                double lastGobalEssence = stgesto.getQteGlobalEs();
                double lastGlobalGazoil = stgesto.getQteGlobaleGaz();
                stgesto.setQteGlobalEs(bc.getQteEs() + lastGobalEssence);
                stgesto.setQteGlobaleGaz(bc.getQteGaz() + lastGlobalGazoil);
                stockGestociRepository.save(stgesto);
                /***************** Sauvegarde des abciennes valeurs de gestoci ***********************/
                //HistoryStockgestoci historyStockGestoci = new HistoryStockgestoci();

                historyStockGestoci.setQteEsDepot(bc.getQteEs());
                historyStockGestoci.setQteGazDepot(bc.getQteGaz());
                historyStockGestoci.setQteGlobaleGaz(lastGlobalGazoil);
                historyStockGestoci.setQteGlobalEs(lastGobalEssence);
                historyStockGestoci.setStockInitGestoci(stgesto.getStockInitGestoci());
                historyStockGestociRepository.save(historyStockGestoci);
                logActionRepository.save(new LogAction(0,LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Ajout de pieces jointes","la pièce ajoutéé est le BL livré par la SIR","Validation du BC et impactation des soldes de GESTOCI",user.getStations().getNom() ));
                return "redirect:listbc";
            }
        }
    }


    @PostMapping("/assistante/checkBC")
    public String viewBC(Model model, RedirectAttributes redirectAttributes,
                         @RequestParam("numBC") String numbc){
        if(numbc != null){
            redirectAttributes.addFlashAttribute("bc", bonDeCommandeRepository.findBonDeCommandeByNumBCAndAaccepteFalse(numbc));
            return "redirect:/assistante/validation";
        }else{

            String message="Veillez renseigner une réference de Bon de commande Valide SVP." ;
            redirectAttributes.addFlashAttribute("message",message);
            return "redirect:validation";
        }

    }

    @PostMapping ("/assistante/saveBcModifier")
    public String validerModifBc(@RequestParam("id") long  id,
                                 @RequestParam("numBC") String numBC,
                                 @RequestParam("vendeur") String vendeur,
                                 @RequestParam("refGaz") String refGaz,
                                 @RequestParam("designationGaz") String designationGaz,
                                 @RequestParam("refEs") String refEs,
                                 @RequestParam("designationEs") String designationEs,
                                 @RequestParam("contactVendeur") String contactVendeur,
                                 @RequestParam("puGaz") double puGaz,
                                 @RequestParam("condEs") String condEs,
                                 @RequestParam("qteGaz") double qteGaz,
                                 @RequestParam("condGaz") String condGaz,
                                 @RequestParam("qteEs") double qteEs,
                                 @RequestParam("puEs") double puEs,
                                 @AuthenticationPrincipal User user,
                                 @RequestParam MultipartFile files){
        Doc doc = docStorageService.saveFile(files);
        Optional<BonDeCommande> BC = bonDeCommandeRepository.findById(id);
        if (BC ==null){
            return "error";
        }else{
            BonDeCommande bc =BC.get();
            bc.setId(id);
            if(doc == null){
                bc.setDoc(doc);
                bc.setDoc(bc.getDoc());
            }else{
                docRepository.save(doc) ;
            }
            bc.setMtnGlobal(bc.getMtnGlobal());
            bc.setRefEs(refEs);
            bc.setRefGaz(refGaz);
            bc.setMtnGaz(bc.getMtnGaz());
            bc.setMtnEs(bc.getMtnEs());
            bc.setAaccepte(bc.isAaccepte());
            bc.setQteGaz(qteGaz);
            bc.setPuEs(puEs);
            bc.setPuGaz(puGaz);
            bc.setQteEs(qteEs);
            bc.setVendeur(vendeur);
            bc.setCondEs(condEs);
            bc.setCondGaz(condGaz);
            bonDeCommandeRepository.save(bc);
            LogAction logAction1 = logActionRepository.save(new LogAction(0,LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Modification de BC","apporter un changement dans les valeurs saisies","un BC avec les valeurs modifiées",user.getStations().getNom() ));
            log.info("Nom de l'acteur: " +  logAction1.getUser() + "<br>" +
                    "Action réalisée: " + logAction1.getActionRealisee() + "<br>" +
                    "Auteur: " + logAction1.getUser() + "<br>" + "Role de l'auteur: " + logAction1.getRole() + "<br>" +
                    "Date Operation: " + logAction1.getLocalDate() + "<br>" + logAction1.getNomAction() + "<br>" +
                    "Impacte de l'action" + logAction1.getImpactAction() + "<br>" + logAction1.getStation());
            return "redirect:/assistante/listbc";
        }

    }


    @GetMapping("/assistante/bc-modifier/{id}")
    public String modifierbc(@PathVariable("id") long id, @AuthenticationPrincipal User user, Model model){

        model.addAttribute("bcmodif", bonDeCommandeRepository.findById(id).orElse(null));
        //model.addAttribute("bcmodif", new BonDeCommande());
        return "bcmodif";
    }



    @GetMapping("/assistante/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename)
            throws MalformedURLException {

        Path file = this.rootLocation.resolve(filename);
        Resource resource = new UrlResource(file.toUri());

        return ResponseEntity
                .ok()
                .body(resource);
    }

    public void store(MultipartFile file){
        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

    @GetMapping("/assistante/bc/{id}/")
    public String validerBlSuperviseur(@PathVariable("id") long id, Model model, @AuthenticationPrincipal User user){
        if(id != 0){
            BonDeCommande bc = bonDeCommandeRepository.findById(id).orElse(null);

            model.addAttribute("bc", bc);
            return "blAModifier";
        }
        return "listbc";
    }


}
