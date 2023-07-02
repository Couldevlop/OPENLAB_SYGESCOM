/*package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.Pompe;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.domaine.Zone;
//import ci.doci.sygescom.repository.PompeRepository;
import ci.doci.sygescom.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;


@Controller
@RequestMapping("/admin")
public class PompeController {

    @Autowired
    ZoneRepository zoneRepository;

    private final Logger log = LoggerFactory.getLogger(PompeController.class);



   /* @GetMapping("/pompe")
    public String getAllAgences(Model model){
        model.addAttribute("pompe", pompeRepository.findAll());
       return "pompes";
    }*/
/*
    @GetMapping("/newpompe")
    public String agence(Model model, @AuthenticationPrincipal User use) {
        model.addAttribute("pompe", new Pompe());
        model.addAttribute("zone", zoneRepository.findAll());
        return "pompeform";
    }

    @PostMapping("/newpompe")
    public String createPompe(@Valid @ModelAttribute("pompe") Pompe pompe, Errors errors, @RequestParam("zoneagence") String zoneagence, @AuthenticationPrincipal User user){
        if(null!= errors && errors.getErrorCount()>0) {
            return "pompeform";
        }else{
        pompe.setCreateBy(user.getUsername());
        Zone zone = zoneRepository.findById(Long.parseLong(zoneagence)).orElse(null);
        pompeRepository.save(pompe);
        zoneRepository.save(zone);
        return "redirect:/admin/pompe";
        }
    }

    @PostMapping("/updatePompe")
    public String updatePompe(@RequestParam("zoneagence") String zoneagence,
                              @RequestParam("id") String id,
                              @AuthenticationPrincipal User user)  {
       Pompe pompes = pompeRepository.findById(Long.parseLong(id)).orElse(null);
       Zone zone = zoneRepository.findById(Long.parseLong(zoneagence)).orElse(null);
       pompes.setCreateBy(user.getUsername());
       pompeRepository.save(pompes);
       zoneRepository.save(zone);
        return "redirect:/admin/pompe";
    }

    @GetMapping("/pompe/{id}")
    public String voirPompe(Model model, @PathVariable String id) {
        Pompe pompes = pompeRepository.findById(Long.parseLong(id)).orElse(null);
        Zone zone = zoneRepository.trouverZoneParPompe((pompes.getId()));
        model.addAttribute("pompe", pompes);
        log.info(zoneRepository.toString());
        model.addAttribute("zone", zone);
        return "pompedetails";
    }

    @GetMapping("/pompe/{id}/delete")
    public String delete(@PathVariable String id) {
        pompeRepository.deleteById(Long.parseLong(id));
        return "redirect:/admin/pompe";
    }
}
*/