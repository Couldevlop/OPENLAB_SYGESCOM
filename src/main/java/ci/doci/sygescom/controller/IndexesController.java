package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.domaine.dto.IndexeDTO;
import ci.doci.sygescom.repository.*;
import ci.doci.sygescom.service.*;
import ci.doci.sygescom.util.IndexeDataExcelExport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Math.abs;


@Controller
@Slf4j
public class IndexesController {

    private final   IndexesTempRepository indexesTempRepository;
    private final IndexesRepository indexesRepository;
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
    private final HistoryStockStationRepository historyStockStationRepository;
    private final JavaMailSender mailSendder;

    private final Logger log = LoggerFactory.getLogger(IndexesController.class);
    private final ControlDepotageRepository controlDepotageRepository;
    private final BonlivraisonRepository bonlivraisonRepository;

    private final ModelMapper modelMapper;

    public IndexesController(IndexesTempRepository indexesTempRepository, IndexesRepository indexesRepository, ZoneRepository zoneRepository,
                             StationsRepository stationsRepository, MouvementsRepository mouvementsRepository, StockInitStationsRepository stockInitStationsRepository, StockStationRepository stockStationRepository, LogActionRepository logActionRepository, OperationIndexesService service, DataIndexService dataIndexService, EcartStationService ecartStationService, EmailService emailService, HistoryStockStationRepository historyStockStationRepository, JavaMailSender mailSendder, ControlDepotageRepository controlDepotageRepository, BonlivraisonRepository bonlivraisonRepository, ModelMapper modelMapper) {
        this.indexesTempRepository = indexesTempRepository;
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
        this.historyStockStationRepository = historyStockStationRepository;
        this.mailSendder = mailSendder;
        this.controlDepotageRepository = controlDepotageRepository;
        this.bonlivraisonRepository = bonlivraisonRepository;
        this.modelMapper = modelMapper;

    }


    //PARAMETRAGE INITIAL SUPERVISEUR
    @PostMapping("/parametrages/newindexes1")
    public String createIndexe1(@Valid @ModelAttribute("data") DataIndex data,
                                Errors errors,
                                @AuthenticationPrincipal User user,
                                Model model, RedirectAttributes redirectAttributes){
         LocalDate localDate = LocalDate.now();
         DataIndex indexesSaved = _returnToDataIndexSaved(user, localDate, data);
         IndexesTemp indexes = buildIndexTable(indexesSaved);

        Stations stationParam = stationsRepository.findById(data.getStations().getId()).orElse(null);
        List<IndexesTemp> indd = indexesTempRepository.findAll();
        for (IndexesTemp i: indd) {
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
                indexesTempRepository.save(indexes);
                return "redirect:/parametrages/indexe1";
            }

        }
        indexes.setDateCreation(LocalDate.now());
        indexes.setCreateBy(user.getUsername());
        indexesTempRepository.save(indexes);
        return "redirect:/parametrages/indexe1";

    }


    @GetMapping("/gerant/indexe")
    public String getAllIndexes(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("indexe", indexesTempRepository.findIndexesByStationsId(user.getStations().getId()));
        return "indexes";
    }

    @GetMapping("/gerant/newindexes")
    public String indexe(Model model, @AuthenticationPrincipal User use) {
        model.addAttribute("data", new DataIndex());
        if(indexesTempRepository.lastId(use.getStations().getId()) != 0){
            Long id = indexesTempRepository.lastId(use.getStations().getId());
            Optional<IndexesTemp> ind = indexesTempRepository.findById(id);//recupère le dernier enregistrement
            IndexesTemp indexes = ind.get();
            model.addAttribute("ind",indexes);
        }

        Stations st = use.getStations();


        model.addAttribute("station", st);
        model.addAttribute("zone", zoneRepository.findAll());
        return "indexesform";
    }

    //----------------------------------------------------------------------------------------------------------------
    //------------- CORRECTION DES ERREURS SUR LES PRISES D4INDEX-----------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------

    @GetMapping("/admin/correction-index")
    public String showPageToCorrectIndex(Model model){
        model.addAttribute("stations", stationsRepository.findAll());
        return "indexCorrectionForm";
    }

    @PostMapping("/admin/tocorrect-index")
    public String toCorrectIndex(@RequestParam(value = "date1", required = false) String date1,
                                 @RequestParam(value = "stations", required = true) String id,
                                 @AuthenticationPrincipal User user,
                                 RedirectAttributes model){

        //Date localDate = date1;
        if(id == null){
            model.addFlashAttribute("message", "Aucune données ne correspond à la station choisie. merci de vous assurer que vous avez bien selectionner une station");
            model.addFlashAttribute("stations", stationsRepository.findAll());
        }
        Long idStation = Long.parseLong(id);
        if(stockStationRepository.findStockStationByStationsId(Long.parseLong(id)) != null){
            if(!date1.isEmpty()){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(date1, formatter);
                List<IndexesTemp> indexesTempList = indexesTempRepository.findByStationsIdAndDateJour(idStation, localDate);
                model.addFlashAttribute("nomStation", user.getStations().getNom());
                model.addFlashAttribute("indexSemaine", indexesTempList);
                return "redirect:/admin/correction-index";
            }

            LocalDate d1 = LocalDate.now();
            LocalDate d2 = d1.minusDays(7);
            List<IndexesTemp> indexesTempSemaineList = indexesTempRepository.listeIndexSemaineParStationSelectionnee(d2, d1, idStation);

            if(indexesTempSemaineList.isEmpty()){
                model.addFlashAttribute("message", "Désolé aucun résultat ne correspond à votre demande. Nous vous retournons un tableau vide.");
                return "redirect:/admin/correction-index";
            }
            model.addFlashAttribute("nomStation", user.getStations().getNom());
            model.addFlashAttribute("indexSemaine", indexesTempSemaineList);
            return "redirect:/admin/correction-index";
        }else{
            model.addFlashAttribute("message", "La station selectionnée ne contient aucune données ou elle n'est pas paramétrée");
            model.addFlashAttribute("stations", stationsRepository.findAll());
            return "redirect:/admin/correction-index";
        }

    }

    @GetMapping("/admin/correction/{id}")
    @Transactional
    public String correction(@PathVariable ("id") Long id,
                             @AuthenticationPrincipal User user,
                             Model model){

        if(id == null){
            model.addAttribute("message", "Une erreur s'est produite, merci de rééssayer à nouveau");
            model.addAttribute("stations", stationsRepository.findAll());
            return "indexCorrectionForm";
        }
        IndexesTemp indexesTemp = indexesTempRepository.findById(id).orElse(null);

            model.addAttribute("stations", user.getStations().getNom());
            model.addAttribute("indexAModifier", indexesTemp);
            return "correctionIndexAdmin";

    }



//------------------------------------------------------------------------------------------------------------------------
//---------------------------------- FIN DE CORRECTION DES ERREURS SUR LES INDEX------------------------------------------
//-------------------------------------------------------------------------------------------------------------------------
    @GetMapping("/gerant/newindexesConfirme")
    public String confirmation(){
        return "indexesformConfirme";
    }

    @PostMapping("/gerant/newindexesconfirme")
     public String confirmCreateIndex(@Valid @ModelAttribute("data") DataIndex data,
                                      Model model, RedirectAttributes redirectAttributes,
                                      @AuthenticationPrincipal User user){
            if(data.getSuper1()==0 || data.getGazoil1()==0 || data.getCuveGazoil()==0 || data.getCuveEssence() == 0){
                redirectAttributes.addFlashAttribute("message","Merci de bien renseigner les champs du formulaire");
                return "redirect:/gerant/newindexes";
            }

            /*ObjectMapper objectMapper =  new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            DataIndex dataIndex = objectMapper.readValue(data, DataIndex.class);*/
            data.setStations(user.getStations());
            data.setCreateBy(user.getLogin());
            data.setDateJour(LocalDate.now());
            redirectAttributes.addFlashAttribute("dataIndex", data);
            redirectAttributes.addFlashAttribute("message", "ATTENTION!!!. " +
                    "Nous vous rappellons que toute erreur peut impacter négativement les différents soldes théoriques de votre station. Merci de bien vouloir vérifier les informations saisies. " +
                    "Si elles sont correctes, clicquez sur VALIDER. Sinon, Prière de les corriger et de valider pour terminer votre saisie.");
           // System.out.printf(data.toString());
            return "redirect:/gerant/newindexesConfirme";

     }


    @PostMapping("/gerant/newindexes")
    public String createIndexe(@Valid @ModelAttribute("data") DataIndex data,
                               Errors errors,
                               @AuthenticationPrincipal User user,
                               Model model, RedirectAttributes redirectAttributes){

        //Recupération du dernier enregistrement
        IndexesTemp In = null;
        long index0= 0;

        LocalDate localDate = LocalDate.now();
        DataIndex indexesSaved = _returnToDataIndexSaved(user, localDate, data);
        indexesSaved.setStations(user.getStations());
        //Indexes indexes = buildIndexTable(indexesSaved);
        IndexesTemp indexes = buildIndexTable(indexesSaved);
        long st = user.getStations().getId();
        //long id = indexesRepository.lastId(st);
        if(indexesTempRepository.lastId(st) == null){
            redirectAttributes.addFlashAttribute("message", "Désolé les initiales des indexes de la station n'ont pas été renseignées convenablement");
            return "redirect:/gerant/newindexes";
        }

        //------------------ construction de l'objet ecar station----------------------
        LocalTime time = LocalTime.now();
        LocalTime heurePremierePrise = LocalTime.of(9,00,00,00000);
        LocalTime heureDeuxiemePrise = LocalTime.of(15,00,00,00000);
        LocalTime heureTroiemePrise = LocalTime.of(22,00,00,00000);


        System.out.println(" Premier heruer " + heurePremierePrise + " || " + " seconde prise " + heureDeuxiemePrise + " || " + time);

        if(indexesTempRepository.lastId(st) != null){
            long lastId= indexesTempRepository.lastId(st);
            IndexesTemp dernierIndex = indexesTempRepository.findById(lastId).get();
            if(dernierIndex.isEtat() == false){
                redirectAttributes.addFlashAttribute("message", "Désolé vous avez soit déjà effectué votre prise, soit vous n'êtes pas dans la plage horaire autorisée des saisies.");
                return "redirect:/gerant/newindexes";
            }

           /* if(dernierIndex.isEtat() == false && heureDeuxiemePrise.compareTo(time)<0){
                redirectAttributes.addFlashAttribute("message", "Désolé vous avez déjà fait la seconde prise, merci d'attendre la troisième qui sera entre 17h et 22h");
                return "redirect:/gerant/newindexes";
            }
            if(dernierIndex.isEtat() == false && heureTroiemePrise.compareTo(time)<0){
                redirectAttributes.addFlashAttribute("message", "Désolé vous avez déjà fait la troisieme prise, merci d'attendre le lendemain pour la première prise  qui sera entre 6h et 9h");
                return "redirect:/gerant/newindexes";
            }

            if(dernierIndex.isEtat() == false){
                redirectAttributes.addFlashAttribute("message", "Désolé vous n'ête pas dans un créneaux horaire autorisé: voici les créneau: 6h-9h/12h-15h/17h-22h " + heurePremierePrise + " || " +heureDeuxiemePrise + " || " + heureTroiemePrise + " || " + localDate);
                return "redirect:/gerant/newindexes";
            }
*/

            List <IndexesTemp> index = indexesTempRepository.findIndexesByStationsId(st);
            for(IndexesTemp ind: index){
                long index1 = ind.getId();
                if(index1>index0){
                    index0 =index1;
                }
                In = indexesTempRepository.findById(index0).get();
            }


            if(data.getSuper1()<In.getSuper1()){
                String errorS1 = In.getSuper1() + " est superieur à " + indexes.getSuper1();
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("errorS1", errorS1);
                return "redirect:/gerant/newindexes";
            }
            if(data.getSuper2()<In.getSuper2()){
                String errorS2 = In.getSuper2() + " est superieur à " + indexes.getSuper2();
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("errorS2", errorS2);
                return "redirect:/gerant/newindexes";
            }
            if(data.getSuper3()<In.getSuper3()){
                String errorS3 = In.getSuper3() + " est superieur à " + indexes.getSuper3();
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("errorS3", errorS3);
                return "redirect:/gerant/newindexes";
            }
            if( data.getSuper4()<In.getSuper4()){
                String errorS4 = In.getSuper4() + " est superieur à " + indexes.getSuper4();
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("errorS4", errorS4);
                return "redirect:/gerant/newindexes";
            }
            if(data.getGazoil1()<In.getGazoil1()){
                String errorG1 = In.getSuper1() + " est superieur à " + indexes.getSuper1();
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("errorG1", errorG1);
                return "redirect:/gerant/newindexes";
            }
            if(data.getGazoil2()<In.getGazoil2()){
                String errorG2 = In.getSuper2() + " est superieur à " + indexes.getSuper2();
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("errorG2", errorG2);
                return "redirect:/gerant/newindexes";
            }
            if(data.getGazoil3()<In.getGazoil3()){
                String errorG3 = In.getSuper3() + " est superieur à " + indexes.getSuper3();
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("errorG3", errorG3);
                return "redirect:/gerant/newindexes";
            }
            if(data.getGazoil4()<In.getGazoil4()){
                String errorG4 = In.getSuper4() + " est superieur à " + indexes.getSuper4();
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("errorG4", errorG4);
                return "redirect:/gerant/newindexes";
            }

            //*************** Controle sur les cuve sans depotage******************
            StockStation stock = null;
            if(stockStationRepository.findStockStationByStationsId(user.getStations().getId()) != null){
                stock = stockStationRepository.findStockStationByStationsId(st);
            }

               double maxCuveEssence = stock.getQteGlobaleEssence() + 50;
               double maxCuveGazoil = stock.getQteGlobaleGazoile() + 50;

            if(data.getCuveEssence()>maxCuveEssence ){
                String message = data.getCuveEssence() + "  est superieur à " + maxCuveEssence + "  L'augmentation de cette valeur doit se faire par la validation d'un BL.Regarder dans l'onglet GESTION BL si vous n'avez pas de BL en attente ";
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/gerant/newindexes";
            }

            if(data.getCuveGazoil()>maxCuveGazoil ){
                String message = data.getCuveGazoil() + "  est superieur à " + maxCuveGazoil + "  L'augmentation de cette valeur doit se faire par la validation d'un BL.Regarder dans l'onglet GESTION BL si vous n'avez pas de BL en attentes ";
                redirectAttributes.addFlashAttribute("saisie", In);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/gerant/newindexes";
            }
        }

        Optional<IndexesTemp> ind = indexesTempRepository.findById(indexesTempRepository.lastId(st));
        StockStation stInit = null;
        if(ind.isPresent()){
            IndexesTemp indexes1 = ind.get();
            if(indexes1.getCuveEssence() ==0.0 || indexes1.getCuveGazoil() ==0.0){
                 stInit = stockStationRepository.findStockStationByStations(indexes1.getStations());
                indexes1.setCuveEssence(stInit.getEssenceInit());
                indexes1.setCuveGazoil(stInit.getGazoilInit());
            }
            //double lastCuveEss = indexes1.getCuveEssence();  //Cuve Essence précédent 145550
            StockStation stockStation = stockStationRepository.findStockStationByStations(user.getStations());
            double lastCuveEss = stockStation.getQteGlobaleEssence();
            //double lastCuvGaz = indexes1.getCuveGazoil(); //Cuve Gasoil précédent    3700
            double lastCuvGaz = stockStation.getQteGlobaleGazoile();
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
            List<IndexesTemp> indexesList = indexesTempRepository.findIndexesByDateJour(LocalDate.now());
            if(!indexesList.isEmpty()){
                for(IndexesTemp indx : indexesList){
                    if(indx.getStations().getId() == user.getStations().getId()){
                        ecartStations.setFirstCuveEssence(indx.getCuveEssence()) ;
                        ecartStations.setFirstCuveGazoil(indx.getCuveGazoil());
                        ecartStations.setLastCuveEssence(indexes.getCuveEssence());
                        ecartStations.setLastCuveGazoil(indexes.getCuveGazoil());


                        ecartStations.setEcartEssence(abs(stockCuveEsSenceConso - qtRestanteEss));
                        ecartStations.setEcartGazoil(abs(stockCuveGazoilConso - qtRestanteGa));
                        ecartStations.setEcartTotalEssence(ecartStations.getEcartTotalEssence() + abs(stockCuveEsSenceConso - qtRestanteEss));
                        ecartStations.setEcartTotalGasoil(ecartStations.getEcartTotalGasoil() + abs(stockCuveGazoilConso - qtRestanteGa));

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

            }
            if(sta.getQteGlobaleGazoile() < 1000 || sta.getQteGlobaleGazoile() <1000){

                alerte ++;
            }

            int nbrIndex = 0;
            sta.setAlerte(alerte);
            indexes.setQtiteRestantEssence(qtRestanteEss);
            indexes.setQtiteRestantGasoil(qtRestanteGa);
            indexes.setDifPriseEss(stockCuveEsSenceConso);
            indexes.setDifPriseGaz(stockCuveGazoilConso);
            indexes.setDateJour(LocalDate.now());
            nbrIndex= sta.getNbrIndex();
            if(sta.getDateJour() == LocalDate.now()){
                nbrIndex ++;

            }else {
                nbrIndex = 1;

            }
            //---------------- Sauvegarder les données de la table stockstation dans la table HistoriqueStockStation------------
            HistoryStockStation historyStockStation = HistoryStockStation.builder()
                    .alerte(sta.getAlerte())
                    .dateDepot(sta.getDateDepot())
                    .ecartEssence(sta.getEcartEssence())
                    .ecartGazoil(sta.getEcartGazoil())
                    .essenceDepot(sta.getEssenceDepot())
                    .stations(sta.getStations())
                    .dateJour(sta.getDateJour())
                    .essenceInit(sta.getEssenceInit())
                    .gazoilDepot(sta.getGazoilDepot())
                    .gazoilInit(sta.getGazoilInit())
                    .id(sta.getId())
                    .nbrIndex(sta.getNbrIndex())
                    .qteGlobaleEssence(sta.getQteGlobaleEssence())
                    .qteGlobaleGazoile(sta.getQteGlobaleGazoile())
                    .motif("saveIndex")
                    .build();
            historyStockStationRepository.save(historyStockStation);
            sta.setNbrIndex(nbrIndex);
            sta.setNbrIndex(indexes.getPrise());
            sta.setDateJour(LocalDate.now());
            StockStation savedStockStation = stockStationRepository.save(sta);
            indexes.setPrise(1);
            indexes.setEtat(true);
            indexesTempRepository.save(indexes);

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
        IndexesTemp indexes = indexesTempRepository.findById(Long.parseLong(id)).orElse(null);
        indexes.setCuveEssence(cuveEssence);
        indexes.setCuveGazoil(cuveGazoil);
        indexes.setStations(user.getStations());
        indexes.setDateCreation(indexes.getDateCreation());
        indexes.setDateJour(LocalDate.now());
        indexesTempRepository.save(indexes);
        return "redirect:/parametrages/indexe";
    }

    @GetMapping("/parametrages/indexes/{id}")
    public String voirPompe(Model model,
                            @PathVariable("id") String id,
                            @AuthenticationPrincipal User user) {
        IndexesTemp indexes = indexesTempRepository.findById(Long.parseLong(id)).orElse(null);
        model.addAttribute("indexes", indexes);
        Long idStation = user.getStations().getId();
        Stations st = stationsRepository.findById(idStation).orElse(null);
        model.addAttribute("station", st);
        return "indexesdetails";
    }

    @GetMapping("/parametrages/indexes/{id}/delete")
    public String delete(@PathVariable String id) {
        indexesTempRepository.deleteById(Long.parseLong(id));
        return "redirect:/parametrages/pompe";
    }


    @GetMapping("/parametrages/indexesjour")
    public String indexejourSatation(Model model, @AuthenticationPrincipal User use) {
        Long id = use.getStations().getId();
        Stations st = stationsRepository.findById(id).orElse(null);
        //Indexes in = indexesRepository.findByStations(st);
        List<IndexesTemp> indx = indexesTempRepository.findAll();
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

    @GetMapping("/controle/stat-indexe")
    public String getLog( Model model){
        model.addAttribute("indexeList", service.getAllIndexeForAllStations().getObjects());
        return "stations-indexe-jour";

    }
    @GetMapping("/controle/stat-indexe/jour")
    public String getLastIndexByStation(Model model){
      List<Indexes> resp =  service.getAllIndexeByStationToDay();
          model.addAttribute("indexeListJour", resp);
          return "stations-indexe-jour";

    }
    @GetMapping("/controle/stat-indexe/jour/{id}")
    @ResponseBody
    public IndexeDTO getDetailsIndexByOneStation(@PathVariable("id") Long id){
        IndexeDTO ind = service.getDetailsForOneStation(id);
     return ind;

    }

    @GetMapping("/controle/suvi-journalier")
    public String controleStock(Model model){
        model.addAttribute("nbrPrise", service.getDataIndexByDay());
        return "suiviPriseIndexJour";

    }


    /* ------------------------------------------------------------------*/
    /*                                                                   */
    /*       METHODE POUR EXPORTER DES DONNEES EN FICHIER EXCEL           */
    /*           Affichage tableau de bord du DGA                         */
    /* ------------------------------------------------------------------*/

    @GetMapping("/controle/stat-indexe/excelExport")
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

    private IndexesTemp buildIndexTable(DataIndex in){
        return IndexesTemp.builder()
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
                        .subject("NOTIFICATION: SEUIL ALERTE STOCK STATION")
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
                        .subject("SEUIL ALERTE STOCK STATION")
                        .msgBody( messageGaz)
                        .attachment(null)
                        .recipient("stockstations@doci.ci")
                        .build();
                emailService.sendSimpleMail(mail);
                return;

        }



    }


   @Scheduled(cron =" 0 0 9,11 * * ?")// Fermeture
    public void blockPriseIndex(){
        List<IndexesTemp> indexes = indexesTempRepository.findIndexesByDateJour(LocalDate.now());
        for(IndexesTemp ind: indexes){
            ind.setEtat(false);
            indexesTempRepository.save(ind);
        }
    }

    @Scheduled(cron =" 0 0 12,14 * * ?")//ouverture
    public void ouvrePriseIndex(){
        List<IndexesTemp> indexes = indexesTempRepository.findIndexesByDateJour(LocalDate.now());
        for(IndexesTemp ind: indexes){
            ind.setEtat(true);
            indexesTempRepository.save(ind);
        }
    }


    @Scheduled(cron =" 0 0 15,17 * * ?")
    public void blockPriseIndex1(){
        List<IndexesTemp> indexes = indexesTempRepository.findIndexesByDateJour(LocalDate.now());
        for(IndexesTemp ind: indexes){
            ind.setEtat(false);
            indexesTempRepository.save(ind);
        }
    }

    @Scheduled(cron =" 0 0 18,21 * * ?")
    public void ouvertPriseIndex2(){
        List<IndexesTemp> indexes = indexesTempRepository.findIndexesByDateJour(LocalDate.now());
        for(IndexesTemp ind: indexes){
            ind.setEtat(true);
            indexesTempRepository.save(ind);
        }
    }
    @Scheduled(cron =" 0 0 22,5 * * ?")
    public void blockPriseIndex2(){
        List<IndexesTemp> indexes = indexesTempRepository.findIndexesByDateJour(LocalDate.now());
        for(IndexesTemp ind: indexes){
            ind.setEtat(false);
            indexesTempRepository.save(ind);
        }
    }

    @Scheduled(cron =" 0 0 5,8 * * ?")
    public void ouvertPriseIndex3(){

        // Je prends la date d'aujourd'hui(comme on sera à 6h) et je fait  -1 donc on se positionne à hier
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        calendar.add(Calendar.DATE, -1);
        Date d = calendar.getTime();
        String date1 = sdf.format(d);

        // Je convertis la date de String en localDate et je la passe à ma fonction
        LocalDate localDate = LocalDate.parse(date1, formatter);
        List<IndexesTemp> indexes = indexesTempRepository.findIndexesByDateJour(localDate);

        // je parcours chaque résultat(les 3 enregistrements par station) et pour chaque resultat
        // Pour chaque station je recupère la dernière saisie et je mets le setEtat à true et voila!!!
        for(IndexesTemp index: indexes){
            Long id = index.getId();
            IndexesTemp indexes1 = indexesTempRepository.findById(id).get();
            indexes1.setEtat(true);
            indexesTempRepository.save(indexes1);
        }

    }

    //@Scheduled(cron = "0 0 0 7 * ?")
    //@Scheduled(cron = "10 * * * * ?")
    public void schedulderService(){


        List<IndexesTemp> dernieresSaisies = new ArrayList<>();

        //Recuperation de tous les élements de la table indexesTemps
        LocalDate d1 = LocalDate.now();
        LocalDate d2 = d1.minusDays(7);
        List<IndexesTemp>  indexesTempList = indexesTempRepository.listeIndexSemaine( d2, d1);

        //Parcours de la liste pour recuperer la liste des stations qui ont fait leurs prises
        List<Long> idIndexeTmp = indexesTempRepository.lesIndex();

       //Liste des dernières saisies par station
        idIndexeTmp.forEach(idStation -> {
            Long id = indexesTempRepository.lastIdIndex(idStation);
            IndexesTemp indexesTemp = indexesTempRepository.findById(id).get();
            dernieresSaisies.add(indexesTemp);
        });


        // Liste des dernières saisies par station
        indexesTempList.forEach(indexes1 -> {
            Indexes ind = Indexes.builder()
                    .id(indexes1.getId())
                    .prise(indexes1.getPrise())
                    .stations(indexes1.getStations())
                    .initIndex(indexes1.isInitIndex())
                    .QtiteRestantGasoil(indexes1.getQtiteRestantGasoil())
                    .QtiteRestantEssence(indexes1.getQtiteRestantEssence())
                    .super4(indexes1.getSuper4())
                    .super3(indexes1.getSuper3())
                    .super2(indexes1.getSuper2())
                    .super1(indexes1.getSuper1())
                    .gazoil4(indexes1.getGazoil4())
                    .gazoil3(indexes1.getGazoil3())
                    .gazoil2(indexes1.getGazoil2())
                    .gazoil1(indexes1.getGazoil1())
                    .cuveGazoil(indexes1.getCuveGazoil())
                    .DifPriseGaz(indexes1.getDifPriseGaz())
                    .DifPriseEss(indexes1.getDifPriseEss())
                    .dateJour(indexes1.getDateJour())
                    .dateCreation(indexes1.getDateCreation())
                    .cuveEssence(indexes1.getCuveEssence())
                    .createBy(indexes1.getCreateBy())
                    .etat(indexes1.isEtat())
                    .stockCuveEssence(indexes1.getStockCuveEssence())
                    .stockCuveGazoil(indexes1.getStockCuveGazoil())
                    .build();
            indexesRepository.save(ind);
            indexesTempRepository.deleteById(indexes1.getId());
        });

        // on repositionne les derners enregistrements de chaque station
        indexesTempRepository.saveAll(dernieresSaisies);
//       indexesTempRepository.saveAll(dernieresSaisies);

    }


}
