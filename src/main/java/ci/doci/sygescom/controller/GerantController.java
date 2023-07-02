package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.BonLivraison;
import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.repository.BonlivraisonRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/gerant")
public class GerantController {
    private final BonlivraisonRepository bonlivraisonRepository;

    public GerantController(BonlivraisonRepository bonlivraisonRepository) {
        this.bonlivraisonRepository = bonlivraisonRepository;
    }


    @GetMapping("/aValider")
    public String blAvalider(@AuthenticationPrincipal User user, Model model){
        Stations stations = user.getStations();
        List<BonLivraison> ListBL = bonlivraisonRepository.findByStations(stations);
        model.addAttribute("blist", ListBL);
        model.addAttribute("user", user);
        return "/listBlAvalider";
    }
}
