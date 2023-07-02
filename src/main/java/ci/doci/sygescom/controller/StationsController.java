package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.repository.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/admin")
public class StationsController {

 //  private PompeRepository pompeRepository;
   private StationsRepository stationsRepository;
   private RoleRepository roleRepository;
   private UserRepository userRepository;
   private IndexesRepository  indexesRepository;
   private ZoneRepository zoneRepository;


    boolean etat = false;

    public StationsController( StationsRepository stationsRepository, RoleRepository roleRepository, UserRepository userRepository, IndexesRepository indexesRepository, ZoneRepository zoneRepository) {
         // PompeRepository pompeRepository,
        //  this.pompeRepository = pompeRepository;
        this.stationsRepository = stationsRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.indexesRepository = indexesRepository;
        this.zoneRepository = zoneRepository;
    }

    @GetMapping("/newstation")
    public String showStation(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("stations", stationsRepository.findAll());
        model.addAttribute("station", new Stations());
        model.addAttribute("zone", zoneRepository.findAll());
      //  model.addAttribute("pompe", pompeRepository.findAll());
        model.addAttribute("indexe",indexesRepository.findAll());
        model.addAttribute("gerant",userRepository.findAll());
        return "stationsForm";


    }

    @PostMapping("/save")
    private String newStation(@AuthenticationPrincipal User user, Stations stations) {
        stations.setUser(user);
        stationsRepository.save(stations);
        return "redirect:/admin/newstation";
    }

    @GetMapping("/station/{id}")
    public String updateStations(@AuthenticationPrincipal User user, Model model, @PathVariable("id") long id, RedirectAttributes redirectAttributes) {
       Optional<Stations> stationDetail = stationsRepository.findById(id);

       if(stationDetail.isPresent()  ){
           Zone zone = zoneRepository.trouverZoneParStation(stationDetail.get().getId());
           model.addAttribute("zone", zone);
           Stations st = stationDetail.get();
           model.addAttribute("station",st);
           model.addAttribute("listZone", zoneRepository.findAll() );
           return "stationsFormUpdate";
       }
       else {
           String message = "Erreur: l'élément recherché n'existe pas";
           redirectAttributes.addFlashAttribute(message);
           return "errors";
       }


    }
    @PostMapping("/updateStation")
    public String updateStation(@RequestParam("id") Long id,
                                @RequestParam("nom") String nom,
                                @RequestParam("zone") String zone,
                                @RequestParam("numEmploye") int numEmploye,
                                @RequestParam("numPompe") int numPompe,
                                @RequestParam("nbrPompeGazoil") Integer nbrPompeGazoil,
                                @RequestParam("nbrPompeEssence") Integer nbrPompeEssence,
                                @RequestParam("stockAlerteEssence") double stockAlerteEssence,
                                @RequestParam("stockAlerteGazoil") double stockAlerteGazoil,
                                @RequestParam("stockCritiqueEssence") double stockCritiqueEssence,
                                @RequestParam("stockCritiqueGazoil") double stockCritiqueGazoil,
                                @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes)  {
        Optional<Stations> updatStation = stationsRepository.findById(id);
        Zone zone1 = zoneRepository.findById(Long.parseLong(zone)).orElse(null);
        if(updatStation.isPresent()){
            Stations updatStat = updatStation.get();
            updatStat.setUser(user);
            updatStat.setNom(nom);
            updatStat.setNumEmploye(numEmploye);
            updatStat.setNumPompe(numPompe);
            updatStat.setZone(zone1);
            updatStat.setDate(LocalDate.now());
            updatStat.setContactGerant(updatStat.getContactGerant());
            updatStat.setNbrPompeEssence(nbrPompeEssence);
            updatStat.setNbrPompeGazoil(nbrPompeGazoil);
            updatStat.setStockAlerteEssence(stockAlerteEssence);
            updatStat.setStockAlerteGazoil(stockAlerteGazoil);
            updatStat.setStockCritiqueEssence(stockCritiqueEssence);
            updatStat.setStockCritiqueGazoil(stockCritiqueGazoil);
            stationsRepository.save(updatStat);
            return "redirect:/admin/newstation";
        }
       else {
           String message = "Erreur: l'élément recherché n'existe pas";
           redirectAttributes.addFlashAttribute(message);
            return "errors";
        }


    }

    @GetMapping("/admin/station/{id}/delete")
    public String delete(@PathVariable String id) {
        stationsRepository.deleteById(Long.parseLong(id));
        return "redirect:/admin/newstation";
    }

}





