package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.domaine.dto.IndexeDTO;
import ci.doci.sygescom.repository.*;
import ci.doci.sygescom.service.DataIndexService;
import ci.doci.sygescom.service.EcartStationService;
import ci.doci.sygescom.service.EmailService;
import ci.doci.sygescom.service.OperationIndexesService;
import ci.doci.sygescom.util.IndexeDataExcelExport;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.abs;


@Controller
@Slf4j
public class IndexesController {

    private final   IndexesRepository indexesRepository;
    private final  ZoneRepository zoneRepository;
    private final  StationsRepository stationsRepository;
    private final MouvementsRepository mouvementsRepository;
    private final StockInitStationsRepository stockInitStationsRepository;
    private final StockStationRepository stockStationRepository;
    private final LogActionRepository logActionRepository;
    private final OperationIndexesService service;
    private final DataIndexService dataIndexService;
    private final EcartStationService ecartStationService;
    private final EmailService emailService;

    private final JavaMailSender mailSendder;

    private final Logger log = LoggerFactory.getLogger(IndexesController.class);

    public IndexesController(IndexesRepository indexesRepository, ZoneRepository zoneRepository,
                             StationsRepository stationsRepository, MouvementsRepository mouvementsRepository, StockInitStationsRepository stockInitStationsRepository, StockStationRepository stockStationRepository, LogActionRepository logActionRepository, OperationIndexesService service, DataIndexService dataIndexService, EcartStationService ecartStationService, EmailService emailService, JavaMailSender mailSendder) {
        this.indexesRepository = indexesRepository;
        this.zoneRepository = zoneRepository;
        this.stationsRepository = stationsRepository;
        this.mouvementsRepository = mouvementsRepository;
        this.stockInitStationsRepository = stockInitStationsRepository;
        this.stockStationRepository = stockStationRepository;
        this.logActionRepository = logActionRepository;
        this.service = service;
        this.dataIndexService = dataIndexService;
        this.ecartStationService = ecartStationService;
        this.emailService = emailService;
        this.mailSendder = mailSendder;
    }


    //PARAMETRAGE INITIAL SUPERVISEUR
    @PostMapping("/parametrages/newindexes1")
    public String createIndexe1(@Valid @ModelAttribute("data") DataIndex data,
                                Errors errors,
                                @AuthenticationPrincipal User user,
                                Model model, RedirectAttributes redirectAttributes){
         LocalDate localDate = LocalDate.now();
         DataIndex indexesSaved = _returnToDataIndexSaved(user, localDate, data);
         Indexes indexes = buildIndexTable(indexesSaved);

        Stations stationParam = stationsRepository.findById(data.getStations().getId()).orElse(null);
        List<Indexes> indd = indexesRepository.findAll();
        for (Indexes i: indd) {
            if(i.getStations().equals(stationParam)){
                redirectAttributes.addFlashAttribute("error", "Indexes déjà paramétrés.");
                return "redirect:indexe1";
            } else {
                indexes.setDateCreation(LocalDate.now());
                indexes.setCreateBy(user.getUsername());
                indexes.setDateJour(LocalDate.now());
                LogAction logAction = new LogAction();
                logAction.setRole(user.getRoles().toString());
                logAction.setActionRealisee(user.getUsername() + "Création d'un indexe");
                logAction.setImpactAction("Le nouveau indexe sera pris en compte immediatement de le recalcule des stock en station");
                logAction.setLocalDate(LocalDate.now());
                logAction.setStation(user.getStations().getNom());
                logAction.setId(user.getId());
                logAction.setNomAction("Prise d'indexe");
                LogAction logAction1 = logActionRepository.save(logAction);
                log.info(logAction1.toString());
                indexesRepository.save(indexes);
                return "redirect:/parametrages/indexe1";
            }

        }
        indexes.setDateCreation(LocalDate.now());
        indexes.setCreateBy(user.getUsername());
        indexesRepository.save(indexes);
        return "redirect:/parametrages/indexe1";

    }


    @GetMapping("/gerant/indexe")
    public String getAllIndexes(Model model, @AuthenticationPrincipal User user){
        List<Indexes> ide = indexesRepository.findAll();
        Long d = user.getStations().getId();
        model.addAttribute("indexe", indexesRepository.findIndexesByStationsId(user.getStations().getId()));
        return "indexes";
    }

    @GetMapping("/gerant/newindexes")
    public String indexe(Model model, @AuthenticationPrincipal User use) {
        model.addAttribute("data", new DataIndex());
        if(indexesRepository.lastId() != null){
            Optional<Indexes> ind = indexesRepository.findById(indexesRepository.lastId());//recupère le dernier enregistrement
            Indexes indexes = ind.get();
            model.addAttribute("ind",ind);
        }

        Stations st = use.getStations();


        model.addAttribute("station", st);
        model.addAttribute("zone", zoneRepository.findAll());
        return "indexesform";
    }

    @PostMapping("/gerant/newindexes")
    public String createIndexe(@Valid @ModelAttribute("data") DataIndex data,
                               Errors errors,
                               @AuthenticationPrincipal User user,
                               Model model, RedirectAttributes redirectAttributes){

        //Recupération du dernier enregistrement
        Indexes In = null;
        long index0= 0;

        LocalDate localDate = LocalDate.now();
        DataIndex indexesSaved = _returnToDataIndexSaved(user, localDate, data);
        Indexes indexes = buildIndexTable(indexesSaved);
        long st = user.getStations().getId();
        //long id = indexesRepository.lastId(st);
        if(indexesRepository.lastId(st) == null){
            redirectAttributes.addFlashAttribute("message", "Désolé les initiales des indexes de la station n'ont pas été renseignées convenablement");
            return "redirect:/gerant/newindexes";
        }

        //------------------ construction de l'objet ecar station----------------------
        //EcartStations ec = buildEcartStation(user.getStations(), LocalDate.now(), new EcartStations(), indexes);



        if(indexesRepository.lastId(st) != null){
            List <Indexes> index = indexesRepository.findIndexesByStationsId(st);
            for(Indexes ind: index){
                long index1 = ind.getId();
                if(index1>index0){
                    index0 =index1;
                }
                In = indexesRepository.findById(index0).get();
            }
            if(In.getSuper1()>indexes.getSuper1() || In.getSuper2()>indexes.getSuper2() || In.getSuper3()>indexes.getSuper3() ||
                    In.getSuper4()>indexes.getSuper4() || In.getGazoil1()>indexes.getGazoil1() || In.getGazoil2()>indexes.getGazoil2()
            || In.getGazoil3()>indexes.getGazoil3() || In.getGazoil4()>indexes.getGazoil4() ){
                String errorS1="nouveau index petit que l'ancien";
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("errorS1", errorS1);
                return "redirect:/gerant/newindexes";
            }
        }

        Optional<Indexes> ind = indexesRepository.findById(indexesRepository.lastId(st));
        if(ind.isPresent()){
            Indexes indexes1 = ind.get();
            if(indexes1.getCuveEssence() ==0.0 || indexes1.getCuveGazoil() ==0.0){
                StockStation stInit = stockStationRepository.findStockStationByStations(indexes1.getStations());
                indexes1.setCuveEssence(stInit.getEssenceInit());
                indexes1.setCuveGazoil(stInit.getGazoilInit());
            }
            double lastCuveEss = indexes1.getCuveEssence();  //Cuve Essence précédent 145550
            double lastCuvGaz = indexes1.getCuveGazoil(); //Cuve Gasoil précédent    3700
            double PriseIndexeEs1 = indexes.getSuper1() - indexes1.getSuper1() ;//   237373.5-236025.7 = 1347.8
            double PriseIndexeEs2 = indexes.getSuper2() - indexes1.getSuper2(); // 297516.06 - 296765.17 = 750.89
            double PriseIndexeEs3 = indexes.getSuper3() - indexes1.getSuper3();// 248982.59 - 248664.89 = 317.7
            double PriseIndexeEs4 = indexes.getSuper4() - indexes1.getSuper4(); //  296380.71 - 295337.98 = 1042.73

            double PriseIndexGaz1 = indexes.getGazoil1() - indexes1.getGazoil1(); // 76788.07 - 76696.17 = 91.9
            double PriseIndexGaz2 = indexes.getGazoil2() - indexes1.getGazoil2(); // 73404.28 - 73204.27 = 200.01
            double PriseIndexGaz3 = indexes.getGazoil3() - indexes1.getGazoil3(); //Différence prise gasoil
            double PriseIndexGaz4 = indexes.getGazoil4() - indexes1.getGazoil4();

            double PriseTotalIndexEss = PriseIndexeEs1 + PriseIndexeEs2 + PriseIndexeEs3 + PriseIndexeEs4; // 3459.12
            double PriseTotalIndexGaz = PriseIndexGaz1 + PriseIndexGaz2 + PriseIndexGaz3 + PriseIndexGaz4; // 2091.91


            double qtRestanteEss = lastCuveEss - PriseTotalIndexEss ;// 145550 - 3459.12 = 11090.88
            double qtRestanteGa = lastCuvGaz - PriseTotalIndexGaz;// 3700-2091.91 = 3408.09

            StockStation sta = stockStationRepository.findStockStationByStations(user.getStations());
            if(sta == null){
                model.addAttribute("message", "La station n'a pas de stock");
                return "errors";
            }


            //---------stock cuve par calcul de prises ---------------
            double stockCuveEsSenceConso = lastCuveEss - indexes.getCuveEssence(); //  145550 - 11050 = 3500
            double stockCuveGazoilConso = lastCuvGaz - indexes.getCuveGazoil();// 3700 - 3400 = 300
            indexes.setStockCuveEssence(indexes.getCuveEssence()); //3500
            indexes.setStockCuveGazoil(indexes.getCuveGazoil());// 300

            // -------------insertion dans EcartStaion(ecart cuve)-------------------

            EcartStations ecartStations = new EcartStations();
            List<Indexes> indexesList = indexesRepository.findIndexesByDateJour(localDate);
            if(!indexesList.isEmpty()){
                for(Indexes indx : indexesList){
                    if(indx.getStations().getId() == user.getStations().getId()){
                        ecartStations.setFirstCuveEssence(indx.getCuveEssence()) ;
                        ecartStations.setFirstCuveGazoil(indx.getCuveGazoil());
                        ecartStations.setLastCuveEssence(indexes.getCuveEssence());
                        ecartStations.setLastCuveGazoil(indexes.getCuveGazoil());


                        ecartStations.setEcartEssence(abs(stockCuveEsSenceConso - qtRestanteEss));
                        ecartStations.setEcartGazoil(abs(stockCuveGazoilConso - qtRestanteGa));

                        // --------------- insertion dans EcartStation(prise totale)----------------------
                        ecartStations.setTotalIndexEssence(PriseTotalIndexEss);
                        ecartStations.setTotalIndexGazoil(PriseTotalIndexGaz);
                        ecartStations.setStations(user.getStations());
                        ecartStations.setDateJour(LocalDate.now());

                        ecartStationService.addEcarStation(ecartStations);
                    }
                }
            }

            indexes.setCreateBy(user.getUsername());
            indexes.setStations(user.getStations());
            indexes.setDateCreation(LocalDate.now());


            sta.setQteGlobaleEssence(qtRestanteEss);
            sta.setQteGlobaleGazoile(qtRestanteGa);
            int alerte = sta.getAlerte();
            if(sta.getQteGlobaleGazoile() > 1000 && sta.getQteGlobaleGazoile() >1000){

                    alerte =0;

            }else{

                alerte ++;
            }


            sta.setAlerte(alerte);
            indexes.setQtiteRestantEssence(qtRestanteEss);
            indexes.setQtiteRestantGasoil(qtRestanteGa);
            indexes.setDifPriseEss(stockCuveEsSenceConso);
            indexes.setDifPriseGaz(stockCuveGazoilConso);
            indexes.setDateJour(LocalDate.now());
            StockStation savedStockStation = stockStationRepository.save(sta);
            indexesRepository.save(indexes);

            //---------- Envoie de mail d'alerte -----------------

            verificationDeSeuilAlerte(savedStockStation);


        }else {
            String message = "La station n'a pas été parametrée avec les valeurs par defaut";
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/gerant/newindexes";
        }


        //--------- Enregitrement du mouvement ----------
        Long rep =mouvementsRepository.lastId();
        if( rep != null ){
            Mouvements lasMvt = mouvementsRepository.findById(mouvementsRepository.lastId()).orElse(null);
            lasMvt.setEtat(false);
            mouvementsRepository.save(lasMvt);
            //----------Creation d'un nouvel objet ------------/
            Mouvements mt = new Mouvements();
            mt.setVentesCorporateList(null);
            mt.setVentesComptantList(null);
            mt.setEtat(false);
            mt.setDateEnrg(LocalDateTime.now());
            mt.setStations(user.getStations());
            mouvementsRepository.save(mt);
            return "redirect:/gerant/indexe";
        }else{
            Mouvements mouvements = new Mouvements();
            mouvements.setDateEnrg(LocalDateTime.now());
            mouvements.setEtat(false);
            mouvements.setStations(user.getStations());
            mouvements.setVentesComptantList(null);
            mouvements.setVentesCorporateList(null);
            mouvementsRepository.save(mouvements);

            return "redirect:/gerant/indexe";
        }

    }

    @PostMapping("/parametrages/updateIndexes")
    public String updatePompe(@RequestParam("cuveEssence") long cuveEssence,
                              @RequestParam("cuveGazoil") long cuveGazoil,
                              @RequestParam("essenceIndexeDebut") long essenceIndexeDebut,
                              @RequestParam("essenceIndexeFin") long essenceIndexeFin,
                              @RequestParam("gazoiIndexeDebut") long gazoiIndexeDebut,
                              @RequestParam("gazoilIndexeFin") long gazoilIndexeFin,
                              @RequestParam("id") String id,
                              @AuthenticationPrincipal User user)  {
        Indexes indexes = indexesRepository.findById(Long.parseLong(id)).orElse(null);
        indexes.setCuveEssence(cuveEssence);
        indexes.setCuveGazoil(cuveGazoil);
        indexes.setStations(user.getStations());
        indexes.setDateCreation(indexes.getDateCreation());
        indexes.setDateJour(LocalDate.now());
        indexesRepository.save(indexes);
        return "redirect:/parametrages/indexe";
    }

    @GetMapping("/parametrages/indexes/{id}")
    public String voirPompe(Model model,
                            @PathVariable("id") String id,
                            @AuthenticationPrincipal User user) {
        Indexes indexes = indexesRepository.findById(Long.parseLong(id)).orElse(null);
        model.addAttribute("indexes", indexes);
        Long idStation = user.getStations().getId();
        Stations st = stationsRepository.findById(idStation).orElse(null);
        model.addAttribute("station", st);
        return "indexesdetails";
    }

    @GetMapping("/parametrages/indexes/{id}/delete")
    public String delete(@PathVariable String id) {
        indexesRepository.deleteById(Long.parseLong(id));
        return "redirect:/parametrages/pompe";
    }


    @GetMapping("/parametrages/indexesjour")
    public String indexejourSatation(Model model, @AuthenticationPrincipal User use) {
        Long id = use.getStations().getId();
        Stations st = stationsRepository.findById(id).orElse(null);
        //Indexes in = indexesRepository.findByStations(st);
        List<Indexes> indx = indexesRepository.findAll();
        // model.addAttribute("indexes", in);
        model.addAttribute("indexeList", indx);
        model.addAttribute("station", st);
        return "indexesJour";
    }

    @GetMapping("/parametrages/newindexesJourStation")
    public String indexejour(Model model, @AuthenticationPrincipal User use) {
        Stations stations = use.getStations();
        // Indexes indexes = indexesRepository.findByStations(stations);
        String zone = use.getStations().getZone().getNom();
        // model.addAttribute("indexesJ", indexes);
        model.addAttribute("stations", stations);
        model.addAttribute("zone", zone);
        return "indexesformJour";
    }



    /* ------------------------------------------------------------------*/
    /*                                                                   */
    /*       RECUPERATION DES DERNIERS INDEXES PAR STATION               */
    /*           Affichage tableau de bord du DGA                         */
     /* ------------------------------------------------------------------*/

    @GetMapping("/dg/stat-indexe")
    public String getLog( Model model){
        model.addAttribute("indexeList", service.getAllIndexeForAllStations().getObjects());
        return "stations-indexe-jour";

    }
    @GetMapping("/dg/stat-indexe/jour")
    public String getLastIndexByStation(Model model){
      List<Indexes> resp =  service.getAllIndexeByStationToDay();
          model.addAttribute("indexeListJour", resp);
          return "stations-indexe-jour";

    }
    @GetMapping("/dg/stat-indexe/jour/{id}")
    @ResponseBody
    public IndexeDTO getDetailsIndexByOneStation(@PathVariable("id") Long id){
        IndexeDTO ind = service.getDetailsForOneStation(id);
     return ind;

    }


    /* ------------------------------------------------------------------*/
    /*                                                                   */
    /*       METHODE POUR EXPORTER DES DONNEES EN FICHIER EXCEL           */
    /*           Affichage tableau de bord du DGA                         */
    /* ------------------------------------------------------------------*/

    @GetMapping("/dg/stat-indexe/excelExport")
    public ModelAndView exportToExcel(){
        ModelAndView mav = new ModelAndView();
        mav.setView(new IndexeDataExcelExport());

        //Lire les données depuis la BD
        List<Indexes> list = service.getAllIndexeByStationToDay();

        // envoyer à la class de traitement
        mav.addObject("list", list);
        return mav;
    }


    //------------------- FONCTIONS UTILIAIRES------------------------

    private Indexes buildIndexTable(DataIndex in){
        Indexes indexes = Indexes.builder()
                .createBy(in.getCreateBy())
                .cuveEssence(in.getCuveEssence())
                .cuveGazoil(in.getCuveGazoil())
                .dateCreation(in.getDateCreation())
                .dateJour(in.getDateJour())
                .DifPriseEss(0.0)
                .DifPriseGaz(0.0)
                .gazoil1(in.getGazoil1())
                .gazoil2(in.getGazoil2())
                .gazoil3(in.getGazoil3())
                .gazoil4(in.getGazoil4())
                .super1(in.getSuper1())
                .super2(in.getSuper2())
                .super3(in.getSuper3())
                .super4(in.getSuper4())
                .initIndex(false)
                .QtiteRestantEssence(0.0)
                .QtiteRestantGasoil(0.0)
                .stations(in.getStations())
                .id(in.getId())
                .build();
        return indexes;
    }

    private DataIndex _returnToDataIndexSaved(User user, LocalDate localDate, DataIndex data){
        data.setCreateBy(user.getUsername());
        data.setDateCreation(localDate);
        data.setDateJour(localDate);
        DataIndex indexesSaved = dataIndexService.add(data);
        return indexesSaved;
    }


   //------------------------- Verification du stock d'alerte ---------------------/

    private void verificationDeSeuilAlerte(StockStation stck){

            //---- on verifie si les seuils des deux cuves est attein si non on verifie cuve par cuve
            if(stck.getQteGlobaleEssence()<= 1000 && stck.getQteGlobaleGazoile()<= 1000){
                String messageGlobal = " Bonjour, " +  "\r\n"  +
                        "Nous vous informons que la quantité de Gazoil et de l'Essence en cuve de la Station de " + stck.getStations().getNom() +
                        "\r\n"  +
                        " a atteint son niveau d'alerte à ce jour : "  +  LocalDate.now() +   " . Voici les détails: " + "\r\n" +
                           " - Quantité  essence actuelle : "  +     +  stck.getQteGlobaleEssence()  +   "\r\n" +
                             "- Quantité  gazoil actuelle : "    +   stck.getQteGlobaleGazoile()    +  "\r\n"
                        +  "\r\n"  +  "\r\n"  +

                          "ALERTE N°"  +  stck.getAlerte()  + "\r\n" ;



                EmailDetails mail = EmailDetails.builder()
                        .subject("NOTIFICATION: SEUIL ALERT STOCK STATION")
                        .msgBody(messageGlobal)
                        .attachment(null)
                        .recipient("stockstations@doci.ci")
                        .build();
                emailService.sendSimpleMail(mail);
                return;
            }
            if(stck.getQteGlobaleEssence()<= 1000){
                String messageEss = " Bonjour, " +  "\r\n"  +
                        "Nous vous informons que la quantité de Gazoil et de l'Essence en cuve de la Station de " + stck.getStations().getNom() +
                        "\r\n"  +
                        " a atteint son niveau d'alerte à ce jour : "  +  LocalDate.now() +   " . Voici les détails: " + "\r\n" +
                        " - Quantité  essence actuelle : "  +     +  stck.getQteGlobaleEssence()

                        +  "\r\n"  +  "\r\n"  +

                        "ALERTE N°"  +  stck.getAlerte()  + "\r\n" ;

                EmailDetails mail = EmailDetails.builder()
                        .subject("SEUIL ALERTE STOCKS EN STATION")
                        .msgBody( messageEss)
                        .attachment(null)
                        .recipient("stockstations@doci.ci")
                        .build();
                emailService.sendSimpleMail(mail);
                return;
            } if(stck.getQteGlobaleGazoile()<= 1000){
                String messageGaz =  " Bonjour, " +  "\r\n"  +
                        "Nous vous informons que la quantité de Gazoil et de l'Essence en cuve de la Station de " + stck.getStations().getNom() +
                        "\r\n"  +
                        " a atteint son niveau d'alerte à ce jour : "  +  LocalDate.now() +   " . Voici les détails: " + "\r\n" +
                        " - Quantité  essence actuelle : "  +     +  stck.getQteGlobaleGazoile()

                        +  "\r\n"  +  "\r\n"  +

                        "ALERTE N°"  +  stck.getAlerte()  + "\r\n" ;


                EmailDetails mail = EmailDetails.builder()
                        .subject("SEUIL ALERT STOCK STATION")
                        .msgBody( messageGaz)
                        .attachment(null)
                        .recipient("stockstations@doci.ci")
                        .build();
                emailService.sendSimpleMail(mail);
                return;

        }



    }
}
