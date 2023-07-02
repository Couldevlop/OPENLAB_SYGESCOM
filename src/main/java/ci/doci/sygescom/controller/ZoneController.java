package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.LogAction;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.domaine.Zone;
//import ci.doci.sygescom.repository.PompeRepository;
import ci.doci.sygescom.repository.LogActionRepository;
import ci.doci.sygescom.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class ZoneController {
    @Autowired
    private ZoneRepository zoneRepository;
    @Autowired
    private LogActionRepository logActionRepository;
    //@Autowired
   // PompeRepository pompeRepository;

    @RequestMapping("/zone")
    public String getAllZone(Model model){
        model.addAttribute("zones", zoneRepository.findAll());
        return "zones";
    }

    @GetMapping("/newzone")
    public String createZone(Model model){
        model.addAttribute("Zone", new Zone());
        return "zoneform";
    }

    @PostMapping("/newzone")
    public String newZoneAgence(@ModelAttribute("Zone") Zone zone, Errors errors, @AuthenticationPrincipal User user){
        if(null!= errors && errors.getErrorCount()>0) {
            return "zoneform";
        }else{
          //  List<Agences> agences = zone.getAgences();

            zone.setCreateBy(user.getUsername());
           // zone.setAgences(agences);
            zoneRepository.save(zone);
            LogAction logAction1 = logActionRepository.save(new LogAction(0, LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Création de zone","Ajoute une nouvelle zone dans la BD","La zone ajouté pourra etre affecté à un ou plusieurs utilisateurs",user.getStations().getNom() ));

            return "redirect:/admin/zone";
        }

    }

    @GetMapping("/zone-details/{id}")
    public String voirZone(Model model, @PathVariable String id) {
        Zone  zone = zoneRepository.findById(Long.parseLong(id)).orElse(null);
        model.addAttribute("zone", zone);
        return "zone-details";
    }

    @PostMapping("/updatezone")
    public String updateZone(@RequestParam("nomZone") String nomZone,
                             @RequestParam("id") String id, Model model,
                             @AuthenticationPrincipal User user){
        Zone zone = zoneRepository.findById(Long.parseLong(id)).orElse(null);
        zone.setNom(nomZone);
        zoneRepository.save(zone);
        model.addAttribute("zone", zone);
        LogAction logAction1 = logActionRepository.save(new LogAction(0, LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Mise à jour de zone","Modifier un  zone dans la BD","Tous les utilisateurs qui etaient dans cette zone ne le seront plus",user.getStations().getNom() ));

        return "redirect:/admin/zone";
    }

    @GetMapping("/zone/{id}/delete" )
    public String deleteZone(@PathVariable("id") String id, @AuthenticationPrincipal User user){
        zoneRepository.deleteById(Long.parseLong(id));
        LogAction logAction1 = logActionRepository.save(new LogAction(0, LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Suppression de zone","Supprimer la zone dans la BD","Les utilisateurs de cette zones ne pouront plus se connecter",user.getStations().getNom() ));

        return "redirect:/admin/zone";
    }


}
