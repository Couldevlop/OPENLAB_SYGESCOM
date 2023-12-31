package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.exception.BadActionException;
import ci.doci.sygescom.repository.*;
import ci.doci.sygescom.service.EmailService;
import ci.doci.sygescom.service.OperationsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller

public class VentesController {
    private final VentesRepository ventesRepository;
    private final StationsRepository stationsRepository;
    private final StockGestociRepository stockGestociRepository;
    private final PrixInitRepository prixInitRepository;
    private final VentesComptantRepository ventesComptantRepository;
    private final StockStationRepository stockStationRepository;
    private final ClientsComptantsRepository clientsComptantsRepository;
    private final EmailService emailService;
    private final ClientCorporateRepository clientCorporateRepository;
    private final ClientsRepository clientsRepository;
    private HistoryStockStationRepository historyStockStationRepository;
    private  final VentesCorporateRepository ventesCorporateRepository;
    private final LogActionRepository logActionRepository;
    private final MouvementsRepository mouvementsRepository;
    private  final IndexesRepository indexesRepository;
    private final OperationsService operationsService;
    private final BeneficiaireRepository beneficiaireRepository;

    public VentesController(VentesRepository ventesRepository, StationsRepository stationsRepository, StockGestociRepository stockGestociRepository, PrixInitRepository prixInitRepository, VentesComptantRepository ventesComptantRepository, StockStationRepository stockStationRepository, ClientsComptantsRepository clientsComptantsRepository, EmailService emailService, ClientCorporateRepository clientCorporateRepository, ClientsRepository clientsRepository, HistoryStockStationRepository historyStockStationRepository, VentesCorporateRepository ventesCorporateRepository, LogActionRepository logActionRepository, MouvementsRepository mouvementsRepository, IndexesRepository indexesRepository, OperationsService operationsService, BeneficiaireRepository beneficiaireRepository) {
        this.ventesRepository = ventesRepository;
        this.stationsRepository = stationsRepository;
        this.stockGestociRepository = stockGestociRepository;
        this.prixInitRepository = prixInitRepository;
        this.ventesComptantRepository = ventesComptantRepository;
        this.stockStationRepository = stockStationRepository;
        this.clientsComptantsRepository = clientsComptantsRepository;
        this.emailService = emailService;
        this.clientCorporateRepository = clientCorporateRepository;
        this.clientsRepository = clientsRepository;
        this.historyStockStationRepository = historyStockStationRepository;
        this.ventesCorporateRepository = ventesCorporateRepository;
        this.logActionRepository = logActionRepository;
        this.mouvementsRepository = mouvementsRepository;
        this.indexesRepository = indexesRepository;
        this.operationsService = operationsService;
        this.beneficiaireRepository = beneficiaireRepository;
    }

    @GetMapping("/gerant/ventes")
    public String ventes(){
        return "ventes";
    }

    @GetMapping("/superviseur/StatVentes")
    public String StatVentes(){
        return "StatVentes";
    }


    @GetMapping("/gerant/newventecomptant")
    public String venteComptant(Model model, @AuthenticationPrincipal User user,
                                @ModelAttribute("idcli") String idcli
                                /*RedirectAttributes redirectAttributes,
                                @RequestParam(name = "idcli") String idcli*/){
        model.addAttribute("vComptant", ventesComptantRepository.findAll());
        //model.addAttribute("venteComptant", new VentesComptant());
        model.addAttribute("ventesComptant", new VentesComptant());
        model.addAttribute("station",user.getStations().getNom());
        long id= prixInitRepository.lastId();
        PrixInit pri=prixInitRepository.findById(id).orElse(null);
        double puE = pri.getPuE();
        double puG = pri.getPuG();
        model.addAttribute("puE", puE);
        model.addAttribute("puG",puG);
        model.addAttribute("gerant", user.getUsername());
        model.addAttribute("id", user.getStations().getId());
        model.addAttribute("user", user);
        String re = "/gerant/ventes/operation/" + idcli;
        return "redirect:" + re;
    }
    @GetMapping("/gerant/newventecorporate")
    public String venteCorporate(Model model, @AuthenticationPrincipal User user,
                                @ModelAttribute("idcli") String idcli){
        model.addAttribute("vCorporate", ventesCorporateRepository.findAll());
        model.addAttribute("ventesCorporate", new VentesCorporate());
        model.addAttribute("station",user.getStations().getNom());
        long id= prixInitRepository.lastId();
        Optional<PrixInit> pri=prixInitRepository.findById(id);
        if(pri == null){
            model.addAttribute("message", "Désolé une erreur s'est produite ");
            return "error";
        }else {
            PrixInit p = pri.get();
            double puE = p.getPuE();
            double puG = p.getPuG();
            model.addAttribute("puE", puE);
            model.addAttribute("puG",puG);
            model.addAttribute("gerant", user.getUsername());
            model.addAttribute("id", user.getStations().getId());
            model.addAttribute("user", user);
            model.addAttribute("idcli", idcli);
            String re = "/gerant/ventesCorporate/operation/" + idcli;
            return "redirect:" + re;
        }

    }

    @GetMapping("/gerant/globalVentecomptant")
    public String globalVentecomptant(Model model, @AuthenticationPrincipal User user){
       // model.addAttribute("vComptant", ventesComptantRepository.findAll());
        //model.addAttribute("vComptant", ventesComptantRepository.findVentesComptantByStations(user.getStations()));
        model.addAttribute("ventesComptant", new VentesComptant());
        model.addAttribute("station",user.getStations().getNom());
        if(prixInitRepository.lastId() == null){
            model.addAttribute("messageParam", "Désolé, les prix initiaux ne sont pas encore paremetrés");
            return "globalVenteComptant";
        }
        Long id= prixInitRepository.lastId();
        Optional<PrixInit> pri=prixInitRepository.findById(id);
        if(pri.isPresent()){
            PrixInit prixInit = pri.get();
            double puE = prixInit.getPuE();
            double puG = prixInit.getPuG();
            model.addAttribute("puE", puE);
            model.addAttribute("puG",puG);
            model.addAttribute("gerant", user.getUsername());
            model.addAttribute("id", user.getStations().getId());
            model.addAttribute("user", user);
            return "globalVenteComptant";

        }else {
            model.addAttribute("message", "Désolé une erreur s'est produite à l'ajout de la pièces jointe");
            return "errors-actions";
        }

    }

    @PostMapping("/gerant/saveVenteComptant")
    public String saveVenteComptant(@ModelAttribute("ventesComptant") VentesComptant ventesComptant,
                                    Model model, @AuthenticationPrincipal User user,
                                    @RequestParam("puE") double puE,
                                    @RequestParam("puG")double puG,
                                    @RequestParam("mntEssenceRemise") double mntEssenceRemise,
                                    @RequestParam("mntGazoilRemise") double mntGazoilRemise,
                                    @RequestParam("litrageGazoil")long litrageGazoil,
                                    @RequestParam("litrageEssence") long litrageEssence,
                                    @RequestParam("idClient") String idClient,
                                    RedirectAttributes redirectAttributes){
        long indexId = indexesRepository.lastIdIndex(user.getStations().getId());//mise à jour de l'index de la station
       if(idClient.equals(null)){
           return "ClientComptant";
       }else{
         //----------Recuperation du client courant-------------------
         ClientsComptants clicpt = clientsComptantsRepository.findById(Long.parseLong(idClient)).orElse(null) ;
        ventesComptant.setLitrageEssence(litrageEssence);//litre d'éssence vendue
        ventesComptant.setLitrageGazoil(litrageGazoil);  //litre de gasoil vendue
        ventesComptant.setDateEng(LocalDate.now());
        ventesComptant.setMntEssenceRemise(mntEssenceRemise);//Montant de la remise essence
        ventesComptant.setMntGazoilRemise(mntGazoilRemise); //
        double  mntFinalEssenceR = ventesComptant.getPuE() - ventesComptant.getMntEssenceRemise();//Le prix de la vente
        mntFinalEssenceR = Math.round(mntFinalEssenceR);//montant arrondi
        double mntFinalGazoilR = ventesComptant.getPuG()- ventesComptant.getMntGazoilRemise();//Le prix de la vente
        mntFinalGazoilR = Math.round(mntFinalGazoilR);//montant arrondi
        ventesComptant.setPuE(mntFinalEssenceR);
        ventesComptant.setClientsComptants(clicpt);
        ventesComptant.setPuG(mntFinalGazoilR);
        double mntglobalEssence = ventesComptant.getLitrageEssence() * mntFinalEssenceR;//Le montant de la vente essence
        mntglobalEssence=Math.round(mntglobalEssence);//montant arrondi
        double mntglobaGazoil = ventesComptant.getLitrageGazoil() * mntFinalGazoilR;//Le montant de la vente
        mntglobaGazoil=Math.round(mntglobaGazoil);
        double mntTotal = mntglobalEssence+mntglobaGazoil;//avoir le montant total de la vente (solde de la facture) du client comptant
        mntTotal=Math.round(mntTotal);//montant arrondi
        ventesComptant.setMntglobalEssence(mntglobalEssence);
        ventesComptant.setMntglobaGazoil(mntglobaGazoil);
        ventesComptant.setMntTotal(mntTotal);
        ventesComptant.setStations(user.getStations());
        //Impacter le solde de la station après vente
        Long id = stockStationRepository.getLastId();
        StockStation st = stockStationRepository.findStockStationByStations(user.getStations());
        double EssenceEnStock=  st.getQteGlobaleEssence();
        double GazoilEnStock = st.getQteGlobaleGazoile();
        st.setQteGlobaleEssence(EssenceEnStock - litrageEssence);
        st.setQteGlobaleGazoile(GazoilEnStock - litrageGazoil);
        stockStationRepository.save(st);
          // Indexes indexes = indexesRepository.findById(indexId).orElse(null);
         //  indexes.setEssenceIndexeFin(st.getQteGlobaleEssence());//Mise à jour table indexe de la station
         //  indexes.setGazoilIndexeFin(st.getQteGlobaleGazoile());//Mise à jour table indexe
          // indexesRepository.save(indexes);
        //ventesComptantRepository.save(ventesComptant);

        //------------Ajout dans la liste des mouvements de l'objet--------------//
           Long rep = mouvementsRepository.lastId();
          Mouvements mvt = mouvementsRepository.findById(rep).orElse(null);
          List<VentesComptant> vc = new ArrayList<VentesComptant>();
          vc.add(ventesComptant);
          mvt.setVentesComptantList(vc);
           mvt.setEtat(true);
          mouvementsRepository.save(mvt);
        redirectAttributes.addFlashAttribute("idcli", clicpt.getId());

           //*********** Historisation de l'ancien stock de la station
           //StockStation stat = stockStationRepository.findById(user.getStations().getId()).orElse(null);
           StockStation stockstation = stockStationRepository.findStockStationByStations(user.getStations());
           StockStation  dernierStockStation = stockStationRepository.findById(stockstation.getId()).orElse(null);
           HistoryStockStation historyStockStation = new HistoryStockStation();
           historyStockStation.setStations(dernierStockStation.getStations());
           historyStockStation.setEssenceInit(dernierStockStation.getEssenceInit());
           historyStockStation.setDateDepot(dernierStockStation.getDateDepot());
           historyStockStation.setEssenceDepot(dernierStockStation.getEssenceDepot());
           historyStockStation.setGazoilInit(dernierStockStation.getGazoilInit());
           historyStockStation.setGazoilDepot(dernierStockStation.getGazoilDepot());
           historyStockStation.setEcartEssence(dernierStockStation.getEcartEssence());
           historyStockStation.setEcartGazoil(GazoilEnStock - litrageGazoil);
           historyStockStation.setQteGlobaleEssence(EssenceEnStock - litrageEssence);
           historyStockStation.setQteGlobaleGazoile(dernierStockStation.getQteGlobaleGazoile());
           historyStockStation.setStations(dernierStockStation.getStations());
           historyStockStation.setMotif("Sauvegarde stock après vente au comptant");
           historyStockStationRepository.save(historyStockStation);
        return "redirect:newventecomptant";
      }
    }
    @PostMapping("/gerant/saveVenteCorporate")
    public String saveVenteCorporate(@ModelAttribute("ventesCorporate") VentesCorporate  ventesCorporate,
                                    Model model, @AuthenticationPrincipal User user,
                                    @RequestParam("puE") double puE,
                                    @RequestParam("puG")double puG,
                                    @RequestParam("mntEssenceRemise") double mntEssenceRemise,
                                    @RequestParam("mntGazoilRemise") double mntGazoilRemise,
                                    @RequestParam("litrageGazoil")long litrageGazoil,
                                    @RequestParam("litrageEssence") long litrageEssence,
                                    @RequestParam("idClient") String idClient,
                                    RedirectAttributes redirectAttributes){
        long indexId = indexesRepository.lastIdIndex(user.getStations().getId());//mise à jour de l'index de la station
        if(idClient.equals(null)){
            return "ClientCorporate";
        }else{
            //----------Recuperation du client courant-------------------
            ClientsCorporates clicpt = clientCorporateRepository.findById(Long.parseLong(idClient)).orElse(null) ;
            ventesCorporate.setLitrageEssence(litrageEssence);//litre d'éssence vendue
            ventesCorporate.setLitrageGazoil(litrageGazoil);  //litre de gasoil vendue
            ventesCorporate.setDateEng(LocalDate.now());
            ventesCorporate.setMntEssenceRemise(mntEssenceRemise);//Montant de la remise essence
            ventesCorporate.setMntGazoilRemise(mntGazoilRemise); //
            double  mntFinalEssenceR = ventesCorporate.getPuE() - ventesCorporate.getMntEssenceRemise();//Le prix de la vente
            mntFinalEssenceR = Math.round(mntFinalEssenceR);//montant arrondi
            double mntFinalGazoilR = ventesCorporate.getPuG()- ventesCorporate.getMntGazoilRemise();//Le prix de la vente
            mntFinalGazoilR = Math.round(mntFinalGazoilR);//montant arrondi
            ventesCorporate.setPuE(mntFinalEssenceR);
            ventesCorporate.setClientsCorporates(clicpt);
            ventesCorporate.setPuG(mntFinalGazoilR);
            double mntglobalEssence = ventesCorporate.getLitrageEssence() * mntFinalEssenceR;//Le montant de la vente essence
            mntglobalEssence=Math.round(mntglobalEssence);//montant arrondi
            double mntglobaGazoil = ventesCorporate.getLitrageGazoil() * mntFinalGazoilR;//Le montant de la vente
            mntglobaGazoil=Math.round(mntglobaGazoil);
            double mntTotal = mntglobalEssence+mntglobaGazoil;//avoir le montant total de la vente (solde de la facture) du client comptant
            mntTotal=Math.round(mntTotal);//montant arrondi
            ventesCorporate.setMntglobalEssence(mntglobalEssence);
            ventesCorporate.setMntglobaGazoil(mntglobaGazoil);
            ventesCorporate.setMntTotal(mntTotal);
            //Impacter le solde de la station après vente
            Long id = stockStationRepository.getLastId();
            StockStation st = stockStationRepository.findStockStationByStations(user.getStations());
            double EssenceEnStock=  st.getQteGlobaleEssence();
            double GazoilEnStock = st.getQteGlobaleGazoile();
            st.setQteGlobaleEssence(EssenceEnStock - litrageEssence);
            st.setQteGlobaleGazoile(GazoilEnStock - litrageGazoil);
            stockStationRepository.save(st);
            /*Indexes indexes = indexesRepository.findById(indexId).orElse(null);
            indexes.setEssenceIndexeFin(st.getQteGlobaleEssence());//Mise à jour table indexe de la station
            indexes.setGazoilIndexeFin(st.getQteGlobaleGazoile());//Mise à jour table indexe
            indexesRepository.save(indexes);*/
            ventesCorporateRepository.save(ventesCorporate);

            //------------Ajout dans la liste des mouvements de l'objet--------------//
            Long rep = mouvementsRepository.lastId();
            Mouvements mvt = mouvementsRepository.findById(rep).orElse(null);
            List<VentesCorporate> vc = new ArrayList<VentesCorporate>();
            vc.add(ventesCorporate);
            mvt.setVentesCorporateList(vc);
            mvt.setEtat(true);
            mouvementsRepository.save(mvt);
            redirectAttributes.addFlashAttribute("idcli", clicpt.getId());

            //*********** Historisation de l'ancien stock de la station
            StockStation stockstation = stockStationRepository.findStockStationByStations(user.getStations());
           // StockStation  stat = stockStationRepository.findById(user.getStations().getId()).orElse(null);
            StockStation  dernierStockStation = stockStationRepository.findById(stockstation.getId()).orElse(null);
            HistoryStockStation historyStockStation = new HistoryStockStation();
            historyStockStation.setStations(dernierStockStation.getStations());
            historyStockStation.setEssenceInit(dernierStockStation.getEssenceInit());
            historyStockStation.setDateDepot(dernierStockStation.getDateDepot());
            historyStockStation.setEssenceDepot(dernierStockStation.getEssenceDepot());
            historyStockStation.setGazoilInit(dernierStockStation.getGazoilInit());
            historyStockStation.setGazoilDepot(dernierStockStation.getGazoilDepot());
            historyStockStation.setEcartEssence(dernierStockStation.getEcartEssence());
            historyStockStation.setEcartGazoil(GazoilEnStock - litrageGazoil);
            historyStockStation.setQteGlobaleEssence(EssenceEnStock - litrageEssence);
            historyStockStation.setQteGlobaleGazoile(dernierStockStation.getQteGlobaleGazoile());
            historyStockStation.setStations(dernierStockStation.getStations());
            historyStockStation.setMotif("Sauvegarde stock après vente au comptant");
             System.out.println(historyStockStation.toString());
           // historyStockStationRepository.save(historyStockStation);
            //historyStockStationRepository.save(historyStockStation);
            logActionRepository.save(new LogAction(0,LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Ajout une vente au corporate","Enregistrement d'une nouvelle vente corporate ","la vente ajouter apparait dans la liste de toutes les ventes au corporate",user.getStations().getNom() ));

            return "redirect:newventecorporate";
        }
    }


    @GetMapping("/gerant/ventes/operation/{idClient}")
    public String venteComptantReduction(Model model, @AuthenticationPrincipal User user,
                                         @PathVariable String idClient,RedirectAttributes redirectAttributes){
        Optional<ClientsComptants> cli = clientsComptantsRepository.findById(Long.parseLong(idClient));
        if (cli.isPresent()){
            ClientsComptants clicpt = cli.get();
            model.addAttribute("venteList", ventesComptantRepository.findVentesComptantByClientsComptants(clicpt));
            model.addAttribute("vcorporate", ventesCorporateRepository.findAll());
            model.addAttribute("vente", new Ventes());
            model.addAttribute("station",user.getStations().getNom());
            model.addAttribute("gerant", user.getUsername());
            model.addAttribute("clientComptant", clicpt);
            model.addAttribute("id", user.getStations().getId());
            model.addAttribute("ventesComptant", new VentesComptant());

        }else{
            redirectAttributes.addFlashAttribute("message", "Le client pour lequel vous voulez effectuer la vente d'nexiste pas.");
            return "errors-action";
        }

        if(prixInitRepository.lastId() == null){
            redirectAttributes.addFlashAttribute("message","Les prix unitaires ne sont pas renseigné. Merci de contacter l'administrateur");
            return "errors-action";
        }
        long id= prixInitRepository.lastId();
        Optional<PrixInit> pri=prixInitRepository.findById(id);
        if(pri.isPresent()){
            PrixInit prixInit =pri.get();
            double puE = prixInit.getPuE();
            double puG = prixInit.getPuG();
            model.addAttribute("puE", puE);
            model.addAttribute("puG",puG);
            System.out.println("prix unitaire essebce est: "  + puE + "le prix unitaire du gazoil est: "  + puG);
            model.addAttribute("user", user);
            model.addAttribute("clientCpt", cli);
            return "venteComptant";
        }else {
            redirectAttributes.addFlashAttribute("message", "Les prix unitaires de l'essence te du gazoil ne sont pas renseignés. Merci de contacter l'administrateur");
            return "errors-action";
        }

        //return "redirect:/gerant/saveVenteComptant";

    }

    @GetMapping("/gerant/ventesCorporate/operation/{idClient}")
    public String venteCorporateReduction(Model model, @AuthenticationPrincipal User user,
                                         @PathVariable String idClient){
        ClientsCorporates cli = clientCorporateRepository.findById(Long.parseLong(idClient)).orElse(null);
        //findVentesComptantByClientsComptants(cli));
        //findVentesCorporateByClientsCorporates
        model.addAttribute("venteListCorp", ventesCorporateRepository.findVentesCorporateByClientsCorporates(cli));
        model.addAttribute("listCopr", ventesCorporateRepository.findAll());
        model.addAttribute("vente", new Ventes());
        model.addAttribute("station",user.getStations().getNom());
        model.addAttribute("gerant", user.getUsername());
        model.addAttribute("id", user.getStations().getId());
        model.addAttribute("ventesCorporate", new VentesCorporate());
        long id= prixInitRepository.lastId();
        PrixInit pri=prixInitRepository.findById(id).orElse(null);
        double puE = pri.getPuE();
        double puG = pri.getPuG();
        model.addAttribute("puE", puE);
        model.addAttribute("puG",puG);
        System.out.println("prix unitaire essebce est: "  + puE + "le prix unitaire du gazoil est: "  + puG);
        model.addAttribute("user", user);
        model.addAttribute("clientCpt", cli);
        return "venteCorporate";
        //return "redirect:/gerant/saveVenteComptant";

    }


    @GetMapping("/superviseur/ventes-corporates/validation-adv")
    public String validationAdv(Model model){
        model.addAttribute("vcorporates", ventesCorporateRepository.findByAdvValidateIsFalse());
        model.addAttribute("vcorporatesList", ventesCorporateRepository.findAll());
        return "validation-adv";
    }
    @PostMapping("/gerant/checkCorporate")
    public String checkCorporate(@RequestParam("contact") String contact, Model model,
                                 RedirectAttributes redirectAttributes,
                                 @RequestParam("litreEssence") double litreEssence,
                                 @RequestParam MultipartFile files,
                                 @RequestParam("litreGazoil") double litreGazoil,
                                 // @RequestParam("numBon") String numBon,
                                 @RequestParam("telBenef") String telBenef,
                                 @AuthenticationPrincipal User user ) {
        if (contact != "") {
            String num = contact.replaceAll("\\s", "");
            //ClientsCorporates cp = clientCorporateRepository.findClientsCorporatesByContact1(num);
            BadActionException badActionException = operationsService._returnClientCorporateAfterSelling(contact, litreEssence, litreGazoil, user.getStations(), telBenef, files);
            if (badActionException.getT() != null ) {
                Beneficiaire beneficiaire = beneficiaireRepository.findBeneficiaireByContact(telBenef);
                ClientsCorporates cp1 = clientCorporateRepository.findClientsCorporatesByContact1(num);
                // redirectAttributes.addFlashAttribute("corporate",cp);
                redirectAttributes.addFlashAttribute("beneficiaire", beneficiaire);
                redirectAttributes.addFlashAttribute("litreEssence",litreEssence);
                redirectAttributes.addFlashAttribute("litreGazoil",litreGazoil);
                // redirectAttributes.addFlashAttribute("numBon",numBon);
                redirectAttributes.addFlashAttribute("telBenef",telBenef);
                redirectAttributes.addFlashAttribute("dates",LocalDate.now());
                redirectAttributes.addFlashAttribute("messages",badActionException.getMessage());
                //return "redirect:/gerant/corporateCheck";
                return "redirect:/gerant/searchcorporate";
            } if(badActionException.getT()==null) {
                String message="Corporate non trouvé";
                redirectAttributes.addFlashAttribute("message", badActionException.getMessage());
                return "redirect:/gerant/searchcorporate";
            } if(badActionException.getT1()==null){
                redirectAttributes.addFlashAttribute("message", badActionException.getMessage());

                return "redirect:/gerant/searchcorporate";
            }
            if(badActionException.getT1() !=null){
                redirectAttributes.addFlashAttribute("success", badActionException.getMessage());
                return "redirect:/gerant/searchcorporate";
            }


        } else {
            String message = "veillez renseigner un numero de telephone SVP";
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/gerant/searchcorporate";
        }

        return null;
    }


    //-----------------------------------------------------------------------------------------------------------
    //--------------------------- VALIDATION d4OPERATION DE VENTE A CREDIT AU CORPORATE: VALIDATION AVD ---------
    //------------------------------------------------------------------------------------------------------------

    @GetMapping("/superviseur/adv/{id}")
    public String validationVenteCoporate(@PathVariable  Long id, Model model,
                                          @AuthenticationPrincipal  User user){
        if(id == null){
            model.addAttribute("message","l'identifiant: " + id + "est nul par conséquent il est impossible de trouver une valeur avec cet Id");
            model.addAttribute("vcorporates", ventesCorporateRepository.findByAdvValidateIsFalse());
            return "validation-adv";
        }
        if(ventesComptantRepository.existsById(id)){
            model.addAttribute("message","Aucune ligne de vente ne correspond à l'identifiant: " + id);
            model.addAttribute("vcorporates", ventesCorporateRepository.findByAdvValidateIsFalse());
            return "validation-adv";
        }
        VentesCorporate ventesCorporate = ventesCorporateRepository.findById(id).get();
        operationsService._doValidateOperation(ventesCorporate.getClientsCorporates().getContact1(), ventesCorporate.getLitrageEssence(), ventesCorporate.getLitrageGazoil(),
                                               ventesCorporate.getStations());
        ventesCorporate.setAdvValidate(true);
        ventesCorporate.setNomAvdValider(user.getUsername());
        ventesCorporateRepository.save(ventesCorporate);

        String message = " Bonjour, " +  "\r\n"  +
                "Nous vous informons par ce mail que  la Station de " + ventesCorporate.getStations().getNom() +
                "\r\n"  +
                " a procédé à une vente à crédit au corporate  le : "  +  LocalDate.now() +   " . Voici les détails: " + "\r\n" +
                " - Quantité  essence vendue : "  +     +  ventesCorporate.getLitrageEssence()  +   "\r\n" +
                "- Quantité  gazoil vendue : "    +   ventesCorporate.getLitrageGazoil()    +  "\r\n" +
                "- Le nom du corporate : "    +   ventesCorporate.getClientsCorporates().getNom()    +  "\r\n" +
                "- Le Le contact du corporate : "    +   ventesCorporate.getClientsCorporates().getContact1()    +  "\r\n" +
                "- Le réprésentant du corpoarte : "    +   ventesCorporate.getClientsCorporates().getInterlocuteur()    +  "\r\n"
                +  "\r\n"  +  "\r\n"  +

                "IMPORTANT ! "  +  " Merci de proceder à la validation afin d'équilibrer le stock de la station" + "\r\n" ;


        EmailDetails mail = EmailDetails.builder()
                .subject("NOTIFICATION: OPERATION DE VENTE AU CORPORATE EN ATTENTE DE VALIDATION(TEST)")
                .msgBody(message)
                .attachment(null)
                .recipient("validationstock@doci.ci")
                .build();
        emailService.sendSimpleMail(mail);
        model.addAttribute("vcorporates", ventesCorporateRepository.findByAdvValidateIsFalse());
        model.addAttribute("messageOk", "La vete a été enregistrée avec succes. Un mail est envoyé à votre AVD pour validation de l'opération. Une fois celle-ci validée, vos stock seront mises à jour.");
        return "validation-adv";
    }

}





