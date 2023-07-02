package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.repository.*;
import ci.doci.sygescom.service.DocStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
public class BonlivraisonControlleur {
    private final BonlivraisonRepository bonlivraisonRepository;
    private final DocStorageService docStorageService;
    private final LogActionRepository logActionRepository;
    private final DocRepository docRepository;
    private final StationsRepository stationsRepository;
    private final PrestataireRepository prestataireRepository;
    private final UserRepository userRepository;
    private final StockStationRepository stockStationRepository;
    private final BonDeCommandeRepository bonDeCommandeRepository;
    private final StockGestociRepository stockGestociRepository;
    private final StockInitStationsRepository stockInitStationsRepository;
    private final VersementRepository versementRepository;
    private final HistoryStockStationRepository historyStockStationRepository;

    private final Path rootLocation = Paths.get("filestorage");
    private  final  IndexesRepository indexesRepository;

    public BonlivraisonControlleur(BonlivraisonRepository bonlivraisonRepository, DocStorageService docStorageService, LogActionRepository logActionRepository, DocRepository docRepository, StationsRepository stationsRepository, PrestataireRepository prestataireRepository, UserRepository userRepository, StockStationRepository stockStationRepository, BonDeCommandeRepository bonDeCommandeRepository, StockGestociRepository stockGestociRepository, StockInitStationsRepository stockInitStationsRepository, VersementRepository versementRepository, HistoryStockStationRepository historyStockStationRepository, IndexesRepository indexesRepository) {
        this.bonlivraisonRepository = bonlivraisonRepository;
        this.docStorageService = docStorageService;
        this.logActionRepository = logActionRepository;
        this.docRepository = docRepository;
        this.stationsRepository = stationsRepository;
        this.prestataireRepository = prestataireRepository;
        this.userRepository = userRepository;
        this.stockStationRepository = stockStationRepository;
        this.bonDeCommandeRepository = bonDeCommandeRepository;
        this.stockGestociRepository = stockGestociRepository;
        this.stockInitStationsRepository = stockInitStationsRepository;
        this.versementRepository = versementRepository;
        this.historyStockStationRepository = historyStockStationRepository;

        this.indexesRepository = indexesRepository;
    }

    @GetMapping("/superviseur/listbl")
    private String bl(Model model, @AuthenticationPrincipal User user){
        LocalDate jj = LocalDate.now();
        model.addAttribute("bl", bonlivraisonRepository.findAll());
        model.addAttribute("BonLivraison", new BonLivraison());
        model.addAttribute("station", stationsRepository.findAll());
        model.addAttribute("p", prestataireRepository.findAll());
        model.addAttribute("user", userRepository.findByStations(user.getStations()));
        model.addAttribute("bc", bonDeCommandeRepository.findAll());
        model.addAttribute("bonlivre", bonlivraisonRepository.findBonLivraisonByStationsAndAccepterFalseAndRejeterIsFalse(user.getStations()));
        return "bl";
    }

    @GetMapping("/gerant/gerantlistbl")    private String blGerant(Model model, @AuthenticationPrincipal User user){
       // model.addAttribute("bl", bonlivraisonRepository.findAll());
        model.addAttribute("bonlivraison", bonlivraisonRepository.findBonLivraisonByStationsAndAccepterFalseAndRejeterFalse(user.getStations()));
        return "blgerant";
    }

    @PostMapping("/superviseur/saveBl")
    public String saveBl(@AuthenticationPrincipal User user,
                         @RequestParam MultipartFile files,
                         @ModelAttribute("BonLivraison") BonLivraison bl,
                         @RequestParam("gerant") User gerant,
                         @RequestParam("stations") Stations stations,
                         Model model) throws IOException{

        Doc doc = docStorageService.saveFile(files);
        bl.setDoc(doc);
        bl.setDateBL(LocalDate.now());
        bl.setGerant(gerant);
        bl.setStations(stations);
        bl.setCloturer(false);
        doc.setDocName(files.getName());
        doc.setDocType(files.getContentType());
        doc.setData(files.getBytes());
        LogAction logAction = new LogAction();
        logAction.setRole(user.getRoles().toString());
        logAction.setActionRealisee(user.getUsername() + "A créé un BL" );
        logAction.setImpactAction("Un nouveau BL sera pris en compte dans l'application");
        logAction.setLocalDate(LocalDate.now());
        logAction.setStation(user.getStations().getNom());
        logAction.setId(user.getId());
        logAction.setNomAction("Création de BL");
        LogAction logAction1 = logActionRepository.save(logAction);
        log.info(logAction1.toString());
        docRepository.save(doc);
        bonlivraisonRepository.save(bl);

        return "redirect:/superviseur/listbl";
    }

    @GetMapping("/superviseur/aValider")
    public String blAvalider(@AuthenticationPrincipal User user, Model model){
        Stations stations = user.getStations();
       List<BonLivraison> ListBL = bonlivraisonRepository.findByStations(stations);
        model.addAttribute("blist", ListBL);
        model.addAttribute("user", user);
        return "bl";
    }

    @PostMapping("/gerant/bl/{id}")
    public String gerantValidateBl(@PathVariable("id") String id,
                            @AuthenticationPrincipal User user){
        BonLivraison bonLivraison = bonlivraisonRepository.findById(Long.parseLong(id)).orElse(null);
        bonLivraison.setAccepter(true);

        return "redirect:/gerant/gerantlistbl";
    }

    @GetMapping("/superviseur/downloadFile/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Integer fileId){
        if(fileId == null){

        }
        Doc doc = docStorageService.getFile(fileId).get();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getDocType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment:filename=\""+doc.getDocName()+"\"")
                .body(new ByteArrayResource(doc.getData()));
    }

    @GetMapping("/superviseur/files/{filename:.+}")
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


    /*
     Validation du BL par le gérant de la station
     dépotage conforme avec le BL
    */
    @GetMapping("/gerant/bl/{id}")
    public String validerBl(@PathVariable("id") long id,
                            Model model, @AuthenticationPrincipal User user){
        BonLivraison livraison = bonlivraisonRepository.findById(id).orElse(null);
        long stId = stockGestociRepository.getLastId(); //recuperation Id du dernier stock gestoci
        Stockgestoci stockgestoci = stockGestociRepository.findById(stId).orElse(null); //impact stock GESTOCI
        long stockStationId = stockInitStationsRepository.getLastId();// Get le stock de la station
        long indexId = indexesRepository.lastId(user.getStations().getId());//dernière saisie de prise d'indexe
        Optional<StockInitStation> stInit = stockInitStationsRepository.findById(stockStationId);//Stock de parametrage
       if(stInit.isPresent() && id !=0){
           StockInitStation stockInitStation = stInit.get();
           StockStation st = new StockStation();
           st.setEssenceDepot(livraison.getQteEs());
           st.setGazoilDepot(livraison.getQteGaz());
           st.setEssenceInit(stockInitStation.getQteinites());
           st.setGazoilInit(stockInitStation.getQteinitgaz());

           //*********** Mise à jour des soldes de la station*****************
           double gazTotal= stockInitStation.getQteinitgaz() + livraison.getQteGaz();
           double eseenceTotal = stockInitStation.getQteinites() + livraison.getQteEs();
           //st.setQteGlobaleGazoile(gazTotal);//stock réel après dépotage gasoil
           //st.setQteGlobaleEssence(eseenceTotal);//stock réel après dépotage essence
           st.setDateDepot(LocalDate.now());
           st.setStockInitStation(stockInitStation);//???
           st.setStations(user.getStations());
           //saisie
           double qteEssenceDuBl = livraison.getQteEs(); //qte Essence presente sur le BL
           double qteGazDuBl = livraison.getQteGaz(); //qte Gazoil present sur le BL
           double qteEssenceDepot = livraison.getQteEs();//Saisie de la quantité déposé
           double qteGazoilDepot = livraison.getQteGaz();//Saisie de la quantité de Gasoile

           //*************** Calcul des ecarts ************************
           double ecartEssence = qteEssenceDuBl - qteEssenceDepot;//calcul écart essence s'il y en a
           double ecartGazoil = qteGazDuBl - qteGazoilDepot; //calcul écart gasoil s'il y en a
           st.setEcartEssence(ecartEssence);
           st.setEcartGazoil(ecartGazoil);
           stockInitStation.setParametre(true);
           stockInitStationsRepository.save(stockInitStation);
           Indexes indexes = indexesRepository.findById(indexId).orElse(null);
          // indexes.setEssenceIndexeFin(eseenceTotal);//Mise à jour table indexe de la station
          // indexes.setGazoilIndexeFin(gazTotal);//Mise à jour table indexe
           indexesRepository.save(indexes);
           stockStationRepository.save(st);
           livraison.setUser(user);
           bonlivraisonRepository.save(livraison);

           //*************  Mise à jour du solde de gestoci *************************
           double qteEssence =   stockgestoci.getQteGlobalEs() - qteEssenceDepot;
           double qteGaz = stockgestoci.getQteGlobaleGaz() - qteGazoilDepot;
           stockgestoci.setQteGlobalEs(qteEssence);
           stockgestoci.setQteGlobaleGaz(qteGaz);
           stockGestociRepository.save(stockgestoci);
           livraison.setAccepter(true);
           bonlivraisonRepository.save(livraison);
           model.addAttribute("bonLivre", livraison);
           model.addAttribute("user",user);
           model.addAttribute("bonlivraison", bonlivraisonRepository.findBonLivraisonByStationsAndAccepterFalseAndRejeterFalse(user.getStations()));
           return "blgerant";
       }else if(!stInit.isPresent()){
           model.addAttribute("message","le paramètrage initial des indexes pour cette station n'est pas fait ou le BL n'existe pas");
           return "blgerant";
       }
        // La station à un stock initial et n'a jamais reçu de dépotage
           // else si dépotage deja effectué
        StockStation stockstation = stockStationRepository.findStockStationByStations(user.getStations());
        StockStation  dernierStockStation = stockStationRepository.findById(stockstation.getId()).orElse(null);
        //*********** Mise à jour des soldes de la station*****************
        double gazTotal= dernierStockStation.getQteGlobaleGazoile() + livraison.getQteGaz();
        double eseenceTotal = dernierStockStation.getQteGlobaleEssence() + livraison.getQteEs();
        double qteEssenceDuBl = livraison.getQteEs(); //qte Essence presente sur le BL
        double qteGazDuBl = livraison.getQteGaz(); //qte Gazoil present sur le BL
        double qteEssenceDepot = livraison.getQteEs();
        double qteGazoilDepot = livraison.getQteGaz();
        Indexes indexes = indexesRepository.findById(indexId).orElse(null);

        //**********Mise à jour de la table des indexes pour impacter le stock global après dépotage*****************
      //  indexes.setEssenceIndexeFin(eseenceTotal);//Mise à jour table indexe de la station
       // indexes.setGazoilIndexeFin(gazTotal);//Mise à jour table indexe
       // indexesRepository.save(indexes);

        //*************** Calcul des ecarts ************************
        double ecartEssence = qteEssenceDuBl - qteEssenceDepot;
        double ecartGazoil = qteGazDuBl - qteGazoilDepot;
        dernierStockStation.setEcartEssence(ecartEssence);
        dernierStockStation.setEcartGazoil(ecartGazoil);
        dernierStockStation.setEssenceDepot(livraison.getSaisieEssence());
        dernierStockStation.setGazoilDepot(livraison.getSaisieGazoil());
        dernierStockStation.setDateDepot(LocalDate.now());
        dernierStockStation.setQteGlobaleEssence(eseenceTotal); //Mise à jour du stock de la station en essence dans la table stock station
        dernierStockStation.setQteGlobaleGazoile(gazTotal); //Mise à jour du stock de la station en gasoil dans la table stock station
        dernierStockStation.setStockInitStation(stInit.get());
        stockStationRepository.save(dernierStockStation);
        livraison.setAccepter(true);
        livraison.setUser(user);
        bonlivraisonRepository.save(livraison);
        //*************  Mise à jour du solde de gestoci *************************
        stockgestoci.setQteGlobalEs( stockgestoci.getQteGlobalEs() - livraison.getQteEs());
        stockgestoci.setQteGlobaleGaz(stockgestoci.getQteGlobaleGaz() - livraison.getQteGaz());
        stockgestoci.setQteGazDepot(livraison.getQteGaz());
        stockgestoci.setQteEsDepot(livraison.getQteEs());
        stockGestociRepository.save(stockgestoci);
        livraison.setAccepter(true);
        bonlivraisonRepository.save(livraison);
        model.addAttribute("bonLivre", livraison);
        model.addAttribute("user",user);

        //*********** Historisation de l'ancien stock de la station
        HistoryStockStation historyStockStation = new HistoryStockStation();
        historyStockStation.setStations(dernierStockStation.getStations());
        historyStockStation.setEssenceInit(dernierStockStation.getEssenceInit());
        historyStockStation.setDateDepot(dernierStockStation.getDateDepot());
        historyStockStation.setEssenceDepot(dernierStockStation.getEssenceDepot());
        historyStockStation.setGazoilInit(dernierStockStation.getGazoilInit());
        historyStockStation.setGazoilDepot(dernierStockStation.getGazoilDepot());
        historyStockStation.setEcartEssence(dernierStockStation.getEcartEssence());
        historyStockStation.setEcartGazoil(dernierStockStation.getEcartGazoil());
        historyStockStation.setQteGlobaleEssence(dernierStockStation.getQteGlobaleEssence());
        historyStockStation.setQteGlobaleGazoile(dernierStockStation.getQteGlobaleGazoile());
        historyStockStation.setStations(dernierStockStation.getStations());
        historyStockStation.setMotif("Sauvegarde de stock avant dépotage de carburant en station");
        historyStockStationRepository.save(historyStockStation);
        model.addAttribute("bonlivraison", bonlivraisonRepository.findBonLivraisonByStationsAndAccepterFalseAndRejeterFalse(user.getStations()));
        return "blgerant";
    }


    @GetMapping("/gerant/bl/{id}/rejeter")
    public String rejeterBl(@PathVariable("id") long id,
                            Model model, @AuthenticationPrincipal User user){
        if(id != 0){
            LogAction logAction = new LogAction();
            logAction.setRole(user.getRoles().get(0).getName());
            logAction.setActionRealisee(user.getUsername() + "rejet de bl" + bonlivraisonRepository.findById(id).get());
            logAction.setImpactAction("Le Bl a été réjété");
            logAction.setLocalDate(LocalDate.now());
            logAction.setStation(user.getStations().getNom());
            logAction.setId(user.getId());
            logAction.setUser(user.getUsername());
            logAction.setNomAction("Rejet de BL");
            LogAction logAction1 = logActionRepository.save(logAction);
            log.info("Nom de l'acteur: " +  logAction1.getUser() + "<br>" +
                    "Action réalisée: " + logAction1.getActionRealisee() + "<br>" +
                    "Auteur: " + logAction1.getUser() + "<br>" + "Role de l'auteur: " + logAction1.getRole() + "<br>" +
                    "Date Operation: " + logAction1.getLocalDate() + "<br>" + logAction1.getNomAction() + "<br>" +
                    "Impacte de l'action" + logAction1.getImpactAction() + "<br>" + logAction1.getStation());

            BonLivraison bli = bonlivraisonRepository.findById(id).orElse(null);
           /* bli.setAccepter(false);
            bli.setRejeter(true); */
            model.addAttribute("bonLivraison", bli);
            return "blRejeter";
        }
        return "listbl";
    }
        /*
        BL  rejeté par le gérant
        */

    @PostMapping("/gerant/blValider")
    public String addCommentaire(@RequestParam("motif") String motif,
                                 @RequestParam("id") long id,
                                 @AuthenticationPrincipal User user){
        BonLivraison livraison = bonlivraisonRepository.findById(id).orElse(null);
        long stId = stockGestociRepository.getLastId();
        Stockgestoci stockgestoci = stockGestociRepository.findById(stId).orElse(null);
        StockInitStation stockStation = stockInitStationsRepository.findStockInitStationByStations(user.getStations());
        StockInitStation stockInitStation = stockInitStationsRepository.findById(stockStation.getId()).orElse(null);

        //livraison.setMotif(motif);
        livraison.setAccepter(true);
        //Premier dépotage;
        if(stockInitStation != null && stockInitStation.isParametre() == false){
            StockStation st = new StockStation();
            st.setEssenceDepot(livraison.getQteEs());
            st.setGazoilDepot(livraison.getQteGaz());
            st.setEssenceInit(stockInitStation.getQteinites());
            st.setGazoilInit(stockInitStation.getQteinitgaz());

            //*********** Mise à jour des soldes de la station*****************
            double gazTotal= stockInitStation.getQteinitgaz() + livraison.getQteGaz();
            double eseenceTotal = stockInitStation.getQteinites() + livraison.getQteEs();
            st.setQteGlobaleGazoile(gazTotal);
            st.setQteGlobaleEssence(eseenceTotal);
            st.setDateDepot(LocalDate.now());
            st.setStations(user.getStations());
            double qteEssenceDuBl = livraison.getQteEs(); //qte Essence presente sur le BL
            double qteGazDuBl = livraison.getQteGaz(); //qte Gazoil present sur le BL
            double qteEssenceDepot = livraison.getSaisieEssence();
            double qteGazoilDepot = livraison.getSaisieGazoil();

            //*************** Calcul des ecarts ************************
            double ecartEssence = qteEssenceDuBl - qteEssenceDepot;
            double ecartGazoil = qteGazDuBl - qteGazoilDepot;
            st.setEcartEssence(ecartEssence);
            st.setEcartGazoil(ecartGazoil);
            stockInitStation.setParametre(true);
            stockInitStationsRepository.save(stockInitStation);
            stockStationRepository.save(st);
            bonlivraisonRepository.save(livraison);
            //Mise à jour des indexes
            Indexes ind = indexesRepository.findById(user.getId()).orElse(null);
            ind.setCuveEssence(ind.getCuveEssence()+qteEssenceDepot);
            ind.setCuveGazoil(ind.getCuveGazoil()+qteEssenceDepot);
            indexesRepository.save(ind);

            //*************  Mise à jour du solde de gestoci *************************
            double qteEssence =   stockgestoci.getQteGlobalEs() - qteEssenceDepot;
            double qteGaz = stockgestoci.getQteGlobaleGaz() - qteGazoilDepot;
            stockgestoci.setQteGlobalEs(qteEssence);
            stockgestoci.setQteGlobaleGaz(qteGaz);
            stockGestociRepository.save(stockgestoci);
            return "redirect:listbl";
        }else{
            StockStation stockstation = stockStationRepository.findStockStationByStations(user.getStations());
            StockStation  dernierStockStation = stockStationRepository.findById(stockstation.getId()).orElse(null);
            //*********** Mise à jour des soldes de la station*****************
            double gazTotal= dernierStockStation.getQteGlobaleGazoile() + livraison.getQteGaz();
            double eseenceTotal = dernierStockStation.getQteGlobaleEssence() + livraison.getQteEs();
            double qteEssenceDuBl = livraison.getQteEs(); //qte Essence presente sur le BL
            double qteGazDuBl = livraison.getQteGaz(); //qte Gazoil present sur le BL
            double qteEssenceDepot = livraison.getQteEs();
            double qteGazoilDepot = livraison.getQteGaz();

            //*************** Calcul des ecarts ************************
            double ecartEssence = qteEssenceDuBl - qteEssenceDepot;
            double ecartGazoil = qteGazDuBl - qteGazoilDepot;
            dernierStockStation.setEcartEssence(ecartEssence);
            dernierStockStation.setEcartGazoil(ecartGazoil);
            dernierStockStation.setEssenceDepot(livraison.getQteEs());
            dernierStockStation.setGazoilDepot(livraison.getQteGaz());
            dernierStockStation.setDateDepot(LocalDate.now());
            dernierStockStation.setQteGlobaleEssence(eseenceTotal);
            dernierStockStation.setQteGlobaleGazoile(gazTotal);
            dernierStockStation.setStockInitStation(stockInitStation);
            stockStationRepository.save(dernierStockStation);
            bonlivraisonRepository.save(livraison);
            //Mise à jour des indexes
            Indexes ind = indexesRepository.findById(user.getId()).orElse(null);
            ind.setCuveEssence(ind.getCuveEssence()+qteEssenceDepot);
            ind.setCuveGazoil(ind.getCuveGazoil()+qteEssenceDepot);
            indexesRepository.save(ind);

            /************* Recuoeration des dernières valeurs de stock de gestoci*******************/
            long stgestoId = stockGestociRepository.getLastId();
            Stockgestoci stgesto = stockGestociRepository.findById(stgestoId).orElse(null);
            double lastGobalEssence = stgesto.getQteGlobalEs();
            double lastGlobalGazoil = stgesto.getQteGlobaleGaz();
            stgesto.setQteGlobalEs(lastGobalEssence - livraison.getQteEs());
            stgesto.setQteGlobaleGaz(lastGlobalGazoil - livraison.getQteGaz());
            stockGestociRepository.save(stgesto);


            //*********** Historisation de l'ancien stock de la station
            HistoryStockStation historyStockStation = new HistoryStockStation();
            historyStockStation.setStations(dernierStockStation.getStations());
            historyStockStation.setEssenceInit(dernierStockStation.getEssenceInit());
            historyStockStation.setDateDepot(dernierStockStation.getDateDepot());
            historyStockStation.setEssenceDepot(dernierStockStation.getEssenceDepot());
            historyStockStation.setGazoilInit(dernierStockStation.getGazoilInit());
            historyStockStation.setGazoilDepot(dernierStockStation.getGazoilDepot());
            historyStockStation.setEcartEssence(dernierStockStation.getEcartEssence());
            historyStockStation.setEcartGazoil(dernierStockStation.getEcartGazoil());
            historyStockStation.setQteGlobaleEssence(dernierStockStation.getQteGlobaleEssence());
            historyStockStation.setQteGlobaleGazoile(dernierStockStation.getQteGlobaleGazoile());
            historyStockStation.setStations(dernierStockStation.getStations());
            historyStockStationRepository.save(historyStockStation);

            return "redirect:/superviseur/listbl";


        }


    }

    @PostMapping("/gerant/blRejeter")
    public String addCommentaireVsaisie(@RequestParam("motif") String motif,
                                 @RequestParam("id") long id,
                                 @RequestParam("saisieEssence") double saisieEssence,
                                        @RequestParam("saisieGazoil") double saisieGazoil,
                                 @AuthenticationPrincipal User user){
        BonLivraison livraison = bonlivraisonRepository.findById(id).orElse(null);
        livraison.setMotif(motif);
        livraison.setRejeter(true);
        livraison.setSaisieEssence(saisieEssence);
        livraison.setSaisieGazoil(saisieGazoil);
        livraison.setHierachie(true);
        bonlivraisonRepository.save(livraison);
        return "redirect:/gerant/gerantlistbl";
    }

    @GetMapping("/superviseur/aValidersuperviseur")
    public String blavaliderSuper(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("bliv", bonlivraisonRepository.findBonLivraisonByAccepterFalse());
    ///superviseur/aValidersuperviseur/
        return "validerOk";
    }

    @GetMapping("/superviseur/aValidersuperviseur/{id}")
    public String blavaliderSuperId(@AuthenticationPrincipal User user, Model model, @PathVariable ("id") long id){
        Optional<BonLivraison> bl = bonlivraisonRepository.findById(id);
        Stations st = bl.get().getStations();
        if(bl.isPresent()){
            BonLivraison bonLivraison = bl.get();
            bonLivraison.setAccepter(true);
            bonLivraison.setRejeter(true);
            InpactValidSuperviseur(bonLivraison.getId(),user, st);//appel de la methode
           // bonlivraisonRepository.save(bonLivraison);
            model.addAttribute("bliv", bonlivraisonRepository.findBonLivraisonByAccepterFalse());
            return "validerOk";
        }else {
            model.addAttribute("bliv", bonlivraisonRepository.findBonLivraisonByAccepterFalse());
            return "validerOk";
        }

    }

    //*******Action sur la validation du superviseur sur un BL rejeté****
    public String InpactValidSuperviseur(long id, User user, Stations stationId ) {
        BonLivraison livraison = bonlivraisonRepository.findById(id).orElse(null);
        long stId = stockGestociRepository.getLastId();
        Stockgestoci stockgestoci = stockGestociRepository.findById(stId).orElse(null);
        StockInitStation stockStation = stockInitStationsRepository.findStockInitStationByStations(stationId);
        StockInitStation stockInitStation = stockInitStationsRepository.findById(stockStation.getId()).orElse(null);

        //Premier dépotage;
        if (stockInitStation != null && stockInitStation.isParametre() == false) {
            StockStation st = new StockStation();
            st.setEssenceDepot(livraison.getQteEs());
            st.setGazoilDepot(livraison.getQteGaz());
            st.setEssenceInit(stockInitStation.getQteinites());
            st.setGazoilInit(stockInitStation.getQteinitgaz());

            //*********** Mise à jour des soldes de la station*****************
            double gazTotal = stockInitStation.getQteinitgaz() + livraison.getSaisieGazoil();
            double eseenceTotal = stockInitStation.getQteinites() + livraison.getSaisieEssence();
            st.setQteGlobaleGazoile(gazTotal);
            st.setQteGlobaleEssence(eseenceTotal);
            st.setDateDepot(LocalDate.now());
            st.setStations(stationId);
            double qteEssenceDuBl = livraison.getQteEs(); //qte Essence presente sur le BL
            double qteGazDuBl = livraison.getQteGaz(); //qte Gazoil present sur le BL
            double qteEssenceDepot = livraison.getSaisieEssence();
            double qteGazoilDepot = livraison.getSaisieGazoil();

            //*************** Calcul des ecarts ************************
            double ecartEssence = qteEssenceDuBl - qteEssenceDepot;
            double ecartGazoil = qteGazDuBl - qteGazoilDepot;
            st.setEcartEssence(ecartEssence);
            st.setEcartGazoil(ecartGazoil);
            stockInitStation.setParametre(true);
            stockInitStationsRepository.save(stockInitStation);
            stockStationRepository.save(st);
            bonlivraisonRepository.save(livraison);

            //*************  Mise à jour du solde de gestoci *************************
            double qteEssence = stockgestoci.getQteGlobalEs() - qteEssenceDepot;
            double qteGaz = stockgestoci.getQteGlobaleGaz() - qteGazoilDepot;
            stockgestoci.setQteGlobalEs(qteEssence);
            stockgestoci.setQteGlobaleGaz(qteGaz);
            stockGestociRepository.save(stockgestoci);
            //Mise à jour des indexes
            long idd = indexesRepository.lastIdInd(stationId.getId());
            Indexes ind = indexesRepository.findById(idd).orElse(null);
            ind.setCuveEssence(ind.getCuveEssence()+qteEssenceDepot);
            ind.setCuveGazoil(ind.getCuveGazoil()+qteGazoilDepot);
            indexesRepository.save(ind);
            return "redirect:listbl";
        } else {
            StockStation stockstation = stockStationRepository.findStockStationByStations(stationId);
            StockStation dernierStockStation = stockStationRepository.findById(stockstation.getId()).orElse(null);
            //*********** Mise à jour des soldes de la station*****************
            double gazTotal = dernierStockStation.getQteGlobaleGazoile() + livraison.getSaisieGazoil();
            double eseenceTotal = dernierStockStation.getQteGlobaleEssence() + livraison.getSaisieEssence();
            double qteEssenceDuBl = livraison.getQteEs(); //qte Essence presente sur le BL
            double qteGazDuBl = livraison.getQteGaz(); //qte Gazoil present sur le BL
            double qteEssenceDepot = livraison.getSaisieEssence();
            double qteGazoilDepot = livraison.getSaisieGazoil();

            //*************** Calcul des ecarts ************************
            double ecartEssence = qteEssenceDuBl - qteEssenceDepot;
            double ecartGazoil = qteGazDuBl - qteGazoilDepot;
            dernierStockStation.setEcartEssence(ecartEssence);
            dernierStockStation.setEcartGazoil(ecartGazoil);
            dernierStockStation.setEssenceDepot(livraison.getSaisieEssence());
            dernierStockStation.setGazoilDepot(livraison.getSaisieGazoil());
            dernierStockStation.setDateDepot(LocalDate.now());
            dernierStockStation.setQteGlobaleEssence(eseenceTotal);
            dernierStockStation.setQteGlobaleGazoile(gazTotal);
            dernierStockStation.setStockInitStation(stockInitStation);
            stockStationRepository.save(dernierStockStation);
            bonlivraisonRepository.save(livraison);
            //Mise à jour des indexes
            if(indexesRepository.existsById(stationId.getId())){
                long idd = indexesRepository.lastIdInd(stationId.getId());
                Indexes ind = indexesRepository.findById(idd).orElse(null);
                ind.setCuveEssence(ind.getCuveEssence()+qteEssenceDepot);
                ind.setCuveGazoil(ind.getCuveGazoil()+qteGazoilDepot);
                indexesRepository.save(ind);
            }


            /************* Recuoeration des dernières valeurs de stock de gestoci*******************/
            long stgestoId = stockGestociRepository.getLastId();
            Stockgestoci stgesto = stockGestociRepository.findById(stgestoId).orElse(null);
            double lastGobalEssence = stgesto.getQteGlobalEs();
            double lastGlobalGazoil = stgesto.getQteGlobaleGaz();
            stgesto.setQteGlobalEs(lastGobalEssence - livraison.getQteEs());
            stgesto.setQteGlobaleGaz(lastGlobalGazoil - livraison.getQteGaz());
            stockGestociRepository.save(stgesto);


            //*********** Historisation de l'ancien stock de la station
            HistoryStockStation historyStockStation = new HistoryStockStation();
            historyStockStation.setStations(dernierStockStation.getStations());
            historyStockStation.setEssenceInit(dernierStockStation.getEssenceInit());
            historyStockStation.setDateDepot(dernierStockStation.getDateDepot());
            historyStockStation.setEssenceDepot(dernierStockStation.getEssenceDepot());
            historyStockStation.setGazoilInit(dernierStockStation.getGazoilInit());
            historyStockStation.setGazoilDepot(dernierStockStation.getGazoilDepot());
            historyStockStation.setEcartEssence(dernierStockStation.getEcartEssence());
            historyStockStation.setEcartGazoil(dernierStockStation.getEcartGazoil());
            historyStockStation.setQteGlobaleEssence(dernierStockStation.getQteGlobaleEssence());
            historyStockStation.setQteGlobaleGazoile(dernierStockStation.getQteGlobaleGazoile());
            historyStockStation.setStations(dernierStockStation.getStations());
            historyStockStationRepository.save(historyStockStation);

            return "redirect:/superviseur/listbl";
        }
    }
    @GetMapping("/gerant/blValiderParHierachie")
    public String blValiderParHierachie(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("blvalider", bonlivraisonRepository.findByStationsAndHierachieTrueAndMotifIsNotNull(user.getStations()));
        return "validerParHierachie";
    }

    @GetMapping("/gerant/blAccepteGerant")
    public String blAccepteGerant(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("blValideGerant", bonlivraisonRepository.findByStationsAndAccepterTrueAndHierachieFalse(user.getStations()));
        return "validerParGerant";
    }

    @GetMapping("/superviseur/bl/{id}")
    public String modilfierBlParSuperviseur(Model model, @AuthenticationPrincipal User user,
                                            @PathVariable("id") long id){
        Optional<BonLivraison> bl = bonlivraisonRepository.findById(id);
        if(bl.isPresent()){
            BonLivraison bonLivraison = bl.get();
            model.addAttribute("bl", bonLivraison);
            return "blmodif";
        }else {
            return "listbl";
        }

 }

    @GetMapping("/superviseur/ecartStation")
    public String getEacrtStation(Model model){
      //  model.addAttribute("listEcartSation", stockStationRepository.findStockStationByStationsAndEcartEssenceNotContainsAndEcartGazoilNotContains());
      return "ecartBlStation";
    }

    @GetMapping( "/superviseur/bl/{id}/delete")
    public String deleteBl(@PathVariable("id") long id, Model model){
        bonlivraisonRepository.deleteById(id);
        model.addAttribute("bl", bonlivraisonRepository.findAll());
        return "redirect:/superviseur/listbl";
    }

    @GetMapping("/comptable/bl-liste")
    public String listBlaClotures(Model model){
        //model.addAttribute("blliste", bonlivraisonRepository.findBonLivraisonByCloturerIsTrue());
        model.addAttribute("blliste", bonlivraisonRepository.findAll());
        return "listBlaValider";
    }

    @GetMapping("/comptable/bl/{id}")
    public String cloturerBl(@PathVariable("id")long id, Model model){
        Optional<BonLivraison>bonLivraison = bonlivraisonRepository.findById(id);
        if(bonLivraison.isPresent()){
           BonLivraison bl= bonLivraison.get();
           bl.setCloturer(true);
           bonlivraisonRepository.save(bl);
            model.addAttribute("blliste", bonlivraisonRepository.findAll());
           return "listBlaValider";
        }else{
            model.addAttribute("message","le bon de livraison n'existe pas");
            return "errors-action";
        }

    }

    @GetMapping("/comptable/caisse")
    public String caisseDashboard(Model model){
        model.addAttribute("versement", new Versement());
        model.addAttribute("versementList", versementRepository.findAll());
        return "caisseStation";
    }

}
