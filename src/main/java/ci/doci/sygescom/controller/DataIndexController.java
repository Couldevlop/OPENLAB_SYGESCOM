package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.DataIndex;
import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.repository.StationsRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DataIndexController {

    private final StationsRepository stationsRepository;

    public DataIndexController(StationsRepository stationsRepository) {
        this.stationsRepository = stationsRepository;
    }

    @GetMapping("/parametrages/newindexes1")
    public String indexe1(Model model, @AuthenticationPrincipal User use) {
        model.addAttribute("data", new DataIndex());
        model.addAttribute("stations", stationsRepository.findAll());
        return "indexesform1";
    }
}
