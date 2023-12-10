package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Controller
@Slf4j
public class SettingController {

    private final StockInitGestociRepository stockInitGestociRepository;
    private final StockInitStationsRepository stockInitStationsRepository;
    private final StockGestociRepository stockGestociRepository;
    private final StationsRepository stationsRepository;
    private final StockStationRepository stockStationRepository;
    private final IndexesTempRepository indexesTempRepository;
    private final ZoneRepository zoneRepository;
    private final LogActionRepository logActionRepository;

    public SettingController(StockInitGestociRepository stockInitGestociRepository, StockInitStationsRepository stockInitStationsRepository, StockGestociRepository stockGestociRepository, StationsRepository stationsRepository, StockStationRepository stockStationRepository, IndexesTempRepository indexesTempRepository, ZoneRepository zoneRepository, LogActionRepository logActionRepository) {
        this.stockInitGestociRepository = stockInitGestociRepository;
        this.stockInitStationsRepository = stockInitStationsRepository;
        this.stockGestociRepository = stockGestociRepository;
        this.stationsRepository = stationsRepository;
        this.stockStationRepository = stockStationRepository;
        this.indexesTempRepository = indexesTempRepository;
        this.zoneRepository = zoneRepository;
        this.logActionRepository = logActionRepository;
    }


    @GetMapping( "/parametrages/quantite_init")
    private String qteInitiales(){
        return "qteinit";
    }


    @GetMapping("/parametrages/Stations")
    private String qteinitStationShow(Model model){
        model.addAttribute("stocksta", stockInitStationsRepository.findAll());
        model.addAttribute("Stockstation", new StockInitStation());
        model.addAttribute("station", stationsRepository.findAll());
        return "qteinitstations";
    }

    @PostMapping("parametrages/Stations/newqte")
    private String qteinitStationAdd(@ModelAttribute("Stockstation") StockInitStation st,
                                     @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes){
        //Verifier si station est parametrée
        StockInitStation stockInitStation = stockInitStationsRepository.findStockInitStationByStations(st.getStations());
        StockStation stockStation = new StockStation();
        if(stockInitStation == null) {
            st.setDateEng(LocalDate.now());
            st.setUser(user);
            st.setStations(st.getStations());
            st.setParametre(true);
            stockStation.setStations(st.getStations());
            stockStation.setStockInitStation(st);
            stockStation.setEssenceInit(st.getQteinites());
            stockStation.setGazoilInit(st.getQteinitgaz());
            stockStation.setQteGlobaleGazoile(st.getQteinitgaz());
            stockStation.setQteGlobaleEssence(st.getQteinites());
            stockInitStationsRepository.save(st);
            LogAction logAction1 = logActionRepository.save(new LogAction(0,LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Nouveau  Parametrage de Station",user.getUsername() + "a enregistrer une nouvelle valeur de station","la nouvelle valeur du parametrage de la station sera prise en compte dans le système",user.getStations().getNom() ));
            log.info(logAction1.toString());
            stockStationRepository.save(stockStation);
            return "redirect:/parametrages/Stations";
        } else if (stockInitStation.isParametre() == true) {

            redirectAttributes.addFlashAttribute("error", "La station déjà parametrée.");
            return "redirect:/parametrages/Stations";
        }
    return null;
    }
@GetMapping("/parametrages/stocksta-details/{idst}")
    public String getStation(@PathVariable  String idst, Model model ){
        StockInitStation initst = stockInitStationsRepository.findById(Long.parseLong(idst)).orElse(null);
        model.addAttribute("stInit", initst);
        model.addAttribute("stat", stationsRepository.findAll());
        return "stationInitUpdate";
    }

    @GetMapping("/parametrages/stockgesto-details/{idst}")
    public String getQteGestociInit(@PathVariable  String idst, Model model ){
        StockInitGestoci initGestoci = stockInitGestociRepository.findById(Long.parseLong(idst)).orElse(null);
        model.addAttribute("sTockGestoInit", initGestoci);
        model.addAttribute("stat", stationsRepository.findAll());
        return "gestociInitUpdate";
    }

    @PostMapping("/parametrage/updateGestoci")
    public String UpdateGestociInit(@AuthenticationPrincipal User user,
                                    @RequestParam("qteTheoriqueGaz") double qteTheoriqueGaz,
                                    @RequestParam("qteGazPhysique") double qteGazPhysique,
                                    @RequestParam("qteEsPhysique") double qteEsPhysique,
                                    @RequestParam("qteTheoriqueEs") double qteTheoriqueEs,
                                    @RequestParam("id") long id) {
        Optional<StockInitGestoci> stg = stockInitGestociRepository.findById(id);
        if (stg.isPresent()) {
            StockInitGestoci stockInitGestoci = stg.get();
            stockInitGestoci.setQteEsPhysique(qteEsPhysique);
            stockInitGestoci.setQteGazPhysique(qteEsPhysique);
            stockInitGestoci.setQteTheoriqueEs(qteTheoriqueEs);
            stockInitGestoci.setQteTheoriqueGaz(qteTheoriqueGaz);
            stockInitGestoci.setDateEng(LocalDate.now());
            LogAction logAction1 = logActionRepository.save(new LogAction(0,LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Modification de Parametrage de Gestoci",user.getUsername() + "a enregistrer une nouvelle valeur de stockgestoci","l'ancienne valeur du parametrage de gestoci sera mise à jour",user.getStations().getNom() ));
            log.info(logAction1.toString());
            log.info("Nom de l'acteur: " +  logAction1.getUser() + "<br>" +
                    "Action réalisée: " + logAction1.getActionRealisee() + "<br>" +
                    "Auteur: " + logAction1.getUser() + "<br>" + "Role de l'auteur: " + logAction1.getRole() + "<br>" +
                    "Date Operation: " + logAction1.getLocalDate() + "<br>" + logAction1.getNomAction() + "<br>" +
                    "Impacte de l'action" + logAction1.getImpactAction() + "<br>" + logAction1.getStation());
            stockInitGestociRepository.save(stockInitGestoci);
            return "redirect:/parametrages/Gestoci";
        }
        return "qteinitgestoci";
    }





    @PostMapping("/parametrages/updateParametrage")
    private String UpdateStationInit(@AuthenticationPrincipal User user, @RequestParam("qteinitgaz") long qteinitgaz,
                                     @RequestParam("qteinites") long qteinites,
                                     @RequestParam("station") Stations station,
                                     @RequestParam("id") long id){
        if(qteinitgaz !=0 && qteinites !=0 ){
            StockInitStation dtInt = stockInitStationsRepository.findById(id).orElse(null);
            //StockInitStation st = new StockInitStation();
            dtInt.setQteinites(qteinites);
            dtInt.setQteinitgaz(qteinitgaz);
            dtInt.setStations(station);
            dtInt.setParametre(dtInt.isParametre());
            dtInt.setDateEng(LocalDate.now());
            LogAction logAction1 = logActionRepository.save(new LogAction(0,LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Modification de Parametrage",user.getUsername() + "a enregistrer une nouvelle valeur de stockgestoci","l'ancienne valeur du parametrage sera mise à jour",user.getStations().getNom() ));
            log.info(logAction1.toString());
            log.info("Nom de l'acteur: " +  logAction1.getUser() + "<br>" +
                    "Action réalisée: " + logAction1.getActionRealisee() + "<br>" +
                    "Auteur: " + logAction1.getUser() + "<br>" + "Role de l'auteur: " + logAction1.getRole() + "<br>" +
                    "Date Operation: " + logAction1.getLocalDate() + "<br>" + logAction1.getNomAction() + "<br>" +
                    "Impacte de l'action" + logAction1.getImpactAction() + "<br>" + logAction1.getStation());
            stockInitStationsRepository.save(dtInt);
            return "redirect:/parametrages/Stations";

        }

        return null;
    }
    @GetMapping("/parametrages/Gestoci")
    private String qteinitGestociShow(Model model){
        model.addAttribute("stockgesto", stockInitGestociRepository.findAll());
        model.addAttribute("Stockgestoci", new StockInitGestoci());
        return "qteinitgestoci";
    }


    @PostMapping("/parametrages/Gestoci/newqte")
    private String qteinitGestociAdd(@ModelAttribute("Stockgestoci") StockInitGestoci initGestoci,
                                     @AuthenticationPrincipal User user){
        LogAction logAction = new LogAction();
        logAction.setRole(user.getRoles().get(0).getName());
        logAction.setActionRealisee(user.getUsername() + "a enregistrer une nouvelle valeur de stockgestoci");
        logAction.setImpactAction("L'application prendra en compte la nouvelle valeur de stockgestoci");
        logAction.setLocalDate(LocalDate.now());
        logAction.setStation(user.getStations().getNom());
        logAction.setId(user.getId());
        logAction.setUser(user.getUsername());
        logAction.setNomAction("Ajout du nouvelle valeur de stockgestoci");
        LogAction logAction1 = logActionRepository.save(logAction);
        log.info("Nom de l'acteur: " +  logAction1.getUser() + "<br>" +
                "Action réalisée: " + logAction1.getActionRealisee() + "<br>" +
                "Auteur: " + logAction1.getUser() + "<br>" + "Role de l'auteur: " + logAction1.getRole() + "<br>" +
                "Date Operation: " + logAction1.getLocalDate() + "<br>" + logAction1.getNomAction() + "<br>" +
                "Impacte de l'action" + logAction1.getImpactAction() + "<br>" + logAction1.getStation());
        stockInitGestociRepository.save(initGestoci);
            return "redirect:/parametrages/Gestoci";
    }


    @GetMapping("/parametrages/indexe1")
    public String getAllIndexes1(Model model){
        List<IndexesTemp> ide = indexesTempRepository.findAll();
        for(IndexesTemp i : ide){
            double CuvEss = i.getCuveEssence();
            if (CuvEss ==0){
                CuvEss = 1;

            }
        }
        model.addAttribute("indexe", indexesTempRepository.findAll());
        return "indexes1";
    }

}
