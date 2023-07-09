package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.repository.*;
import ci.doci.sygescom.service.DocStorageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Optional;

@Controller
public class FacturationController {
    private final DocStorageService docStorageService;
    private final LogActionRepository logActionRepository;
    private final DocRepository docRepository;

    private final CorporateDirecteRepository corporateDirecteRepository;
    private final ClientCorporateRepository clientCorporateRepository;
    private final PrestataireRepository prestataireRepository;
    private final StockGestociRepository stockGestociRepository;
    private final BonlivraisonRepository bonlivraisonRepository;
    //private final FacturationCorporateRepository facturationCorporateRepository;

    public FacturationController(DocStorageService docStorageService, LogActionRepository logActionRepository, DocRepository docRepository,
                                 CorporateDirecteRepository corporateDirecteRepository,
                                 ClientCorporateRepository clientCorporateRepository,
                                 PrestataireRepository prestataireRepository, StockGestociRepository stockGestociRepository, BonlivraisonRepository bonlivraisonRepository) {
        this.docStorageService = docStorageService;
        this.logActionRepository = logActionRepository;
        this.docRepository = docRepository;
        this.corporateDirecteRepository = corporateDirecteRepository;
        this.clientCorporateRepository = clientCorporateRepository;
        this.prestataireRepository = prestataireRepository;
        this.stockGestociRepository = stockGestociRepository;
        this.bonlivraisonRepository = bonlivraisonRepository;
       // this.facturationCorporateRepository = facturationCorporateRepository;
    }

    @GetMapping("/facturation/caisse")
    public String voirCaisse() {
        return "versement";
    }

    @GetMapping("/facturation/corporate/directe")
    public String coporateDirect(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("bliDirect", corporateDirecteRepository.findCorporateDirecteByAccepterIsFalse());
        model.addAttribute("bl", new CorporateDirecte());
        model.addAttribute("corpo", clientCorporateRepository.findClientsCorporatesByTypeClient("DIRECTE"));
        model.addAttribute("p", prestataireRepository.findAll());
        return "corporateDirecte";
    }

    @PostMapping("/facturation/saveBl")
    public String saveBlCorporate(@ModelAttribute("bl") CorporateDirecte cd,
                                  @AuthenticationPrincipal User user, Model model,
                                  @RequestParam MultipartFile files) {
        if (cd == null) {
            return "corporateDirecte";
        } else {
            Doc doc = docStorageService.saveFile(files);
            cd.setDoc(doc);
            cd.setDateBL(LocalDate.now());
            cd.setUser(user);
            corporateDirecteRepository.save(cd);
            return "redirect:/facturation/corporate/directe";
        }


    }

    @GetMapping("/facturation/blCorporateAccepte")
    public String blValides(Model model) {
        model.addAttribute("blAccepte", corporateDirecteRepository.findCorporateDirecteByAccepterIsTrue());
        //model.addAttribute("blAccepte", facturationCorporateRepository.findAll());
        return "blCorporateValides";
    }

    @GetMapping("/facturation/ecart/station")
    public String factureEcartStation(Model model){
        model.addAttribute("ecartStation", bonlivraisonRepository.findBonLivraisonByHierachieTrueAndRejeterTrue());
        return "blStationEcart";
    }

    @GetMapping("/facturation/corporate/station")
    public String corporateStation() {
        return null;
    }

    @GetMapping("/facturation/bl/corporate/{id}")
    public String ValidationBlCorporate(Model model, @PathVariable("id") long id, @AuthenticationPrincipal User user) {
        Optional<CorporateDirecte> BlC = corporateDirecteRepository.findById(id);
        if (BlC.isPresent()) {
            /**------ Validation du BL Corporate --------*/
            CorporateDirecte corporateDirecte = BlC.get();
            corporateDirecte.setAccepter(true);
            corporateDirecteRepository.save(corporateDirecte);

            /**-----Renseignement de la table des logs ---*/
            LogAction lga = new LogAction();
            lga.setActionRealisee("Acceptation du BL Corporate");
            lga.setImpactAction("Impact du stock de GESTOCI");
            lga.setLocalDate(LocalDate.now());
            lga.setRole(user.getRoles().get(0).getName());
            lga.setStation(user.getStations().getNom());
            lga.setUser(user.getUsername());
            lga.setNomAction("Validation BL Corporate");
            logActionRepository.save(lga);

            /**-------- Mise à jour du stock de GESTOCI -----*/
            long stId = stockGestociRepository.getLastId();
            Stockgestoci stockgestoci = stockGestociRepository.findById(stId).orElse(null);
            double qteEssence = stockgestoci.getQteGlobalEs() - corporateDirecte.getQteEs();
            double qteGaz = stockgestoci.getQteGlobaleGaz() - corporateDirecte.getQteGaz();
            stockgestoci.setQteGlobalEs(qteEssence);
            stockgestoci.setQteGlobalEs(qteGaz);
            stockGestociRepository.save(stockgestoci);

            //*************** Calcul des ecarts ************************
            long ecartEssence = corporateDirecte.getQteEs() - corporateDirecte.getSaisieEssence();
            long ecartGazoil = corporateDirecte.getQteGaz() - corporateDirecte.getSaisieGazoil();
            corporateDirecte.setEcartEssence(ecartEssence);
            corporateDirecte.setEcartGazoil(ecartGazoil);
            corporateDirecteRepository.save(corporateDirecte);

            return "corporateDirecte";
        } else {
            model.addAttribute("message", "Le BL recherché n'existe pas en base");
            return "error";
        }
    }

    @GetMapping("/facturation/{id}/ecart")
    public String ecartBlCorporate(Model model, @PathVariable("id") long id, @AuthenticationPrincipal User user) {
        Optional<CorporateDirecte> cp = corporateDirecteRepository.findById(id);
        if (cp.isPresent()) {
            /**------ Validation du BL Corporate --------*/
            CorporateDirecte corporateDirecte = cp.get();
            model.addAttribute("corporateDirecte", corporateDirecte);
            return "blCorporateRejeter";
        }
        return "blCorporateRejeter";

    }

    @PostMapping("/facturation/blCorporateRejeter")
    public String update(
            @RequestParam("motif") String motif,
            @RequestParam("id") long id,
            @RequestParam("saisieEssence") long saisieEssence,
            @RequestParam("saisieGazoil") long saisieGazoil,
            @AuthenticationPrincipal User user) {
        CorporateDirecte livraison = corporateDirecteRepository.findById(id).orElse(null);
        livraison.setMotif(motif);
        livraison.setRejeter(true);

        /**-------- Mise à jour du stock de GESTOCI -----*/
        long stId = stockGestociRepository.getLastId();
        Stockgestoci stockgestoci = stockGestociRepository.findById(stId).orElse(null);
        double qteEssence = stockgestoci.getQteGlobalEs() - livraison.getQteEs();
        double qteGaz = stockgestoci.getQteGlobaleGaz() - livraison.getQteGaz();
        stockgestoci.setQteGlobalEs(qteEssence);
        stockgestoci.setQteGlobaleGaz(qteGaz);
        stockGestociRepository.save(stockgestoci);

        //*************** Mise  à jour ecarts et quantités livrées ************************
        livraison.setSaisieEssence(saisieEssence);
        livraison.setEcartEssence(saisieEssence);
        livraison.setSaisieGazoil(saisieGazoil);
        livraison.setEcartGazoil(saisieGazoil);
        livraison.setQteEs(livraison.getQteEs() - livraison.getEcartEssence());
        livraison.setQteGaz(livraison.getQteGaz() - livraison.getEcartGazoil());
        livraison.setAccepter(true);
        corporateDirecteRepository.save(livraison);

        /**-----Renseignement de la table des logs ---*/
        LogAction lga = new LogAction();
        lga.setActionRealisee("Acceptation du BL Corporate avec ecart");
        lga.setImpactAction("Mise à jour qte livrée et Impact du stock de GESTOCI");
        lga.setLocalDate(LocalDate.now());
        lga.setRole(user.getRoles().get(0).getName());
        lga.setStation(user.getStations().getNom());
        lga.setUser(user.getUsername());
        lga.setNomAction("Validation BL Corporate avec ecart");
        logActionRepository.save(lga);
        return "redirect:/facturation/corporate/directe";
    }

    @GetMapping("/facturation/bl/corporate/{id}/directe")
    public String updateBlCorporateDirecte(@PathVariable("id")long id, @AuthenticationPrincipal User user, Model model, HttpServletRequest request){
        Optional<CorporateDirecte> cor = corporateDirecteRepository.findById(id);
        if(cor.isPresent()){
            CorporateDirecte directe = cor.get();
            directe.setAccepter(true);

            /**-------- Mise à jour du stock de GESTOCI -----*/
            long stId = stockGestociRepository.getLastId();
            Stockgestoci stockgestoci = stockGestociRepository.findById(stId).orElse(null);
            double qteEssence = stockgestoci.getQteGlobalEs() - directe.getQteEs();
            double qteGaz = stockgestoci.getQteGlobaleGaz() - directe.getQteGaz();
            stockgestoci.setQteGlobalEs(qteEssence);
            stockgestoci.setQteGlobaleGaz(qteGaz);
            corporateDirecteRepository.save(directe);
            stockGestociRepository.save(stockgestoci);
            model.addAttribute("bl", new CorporateDirecte());
            String redirectUrl = "/facturation/corporate/directe";
            return "redirect:" + redirectUrl;
        }else{
            model.addAttribute("message", "Le BL récherché n'existe pas");
            return "errors";
        }

    }

    @GetMapping("/facturation/corporateDirecte/ecart")
    public String ecarteCorporate(Model model){
        model.addAttribute("ecartList", corporateDirecteRepository.ecartCorporateDirecte());
        return "ecartCorporateDirecte";
    }

}
