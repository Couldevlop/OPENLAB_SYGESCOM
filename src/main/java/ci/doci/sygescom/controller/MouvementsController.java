package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import ci.doci.sygescom.repository.MouvementsRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MouvementsController {
    private final MouvementsRepository mouvementsRepository;

    public MouvementsController(MouvementsRepository mouvementsRepository) {
        this.mouvementsRepository = mouvementsRepository;
    }

    @GetMapping("/allMouvements")
    public String getAllMouvement(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("mouvements", mouvementsRepository.findAll());
        return null;
    }

    @GetMapping("/mvtByStation")
    public String getByStation(Model model, @AuthenticationPrincipal User user){
        mouvementsRepository.findMouvementsByStations(user.getStations());
        return null;
    }


    @GetMapping("/mvtByStation/{id}")
    public String getByStationParam(Model model, @AuthenticationPrincipal User user, @PathVariable("id") long id){
        mouvementsRepository.findMouvementsByStationsId(id);
        return null;
    }
}
