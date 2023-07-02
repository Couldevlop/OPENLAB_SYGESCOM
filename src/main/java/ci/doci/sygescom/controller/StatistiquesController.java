package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.Indexes;
import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.repository.IndexesRepository;
import ci.doci.sygescom.repository.StationsRepository;
import ci.doci.sygescom.repository.StockStationRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
public class StatistiquesController {
    private final IndexesRepository indexesRepository;
    private final StockStationRepository stockStationRepository;
    private final StationsRepository stationsRepository;

    public StatistiquesController(IndexesRepository indexesRepository, StockStationRepository stockStationRepository, StationsRepository stationsRepository) {
        this.indexesRepository = indexesRepository;
        this.stockStationRepository = stockStationRepository;
        this.stationsRepository = stationsRepository;
    }

    @GetMapping("/superviseur/priseIndexe")
    public String priseIndexe(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("indexes",indexesRepository.listeIndexes() );
        return "priseIndexe";
    }

    @GetMapping("/superviseur/etatStock")
    public String etatStock(Model model){
        model.addAttribute("stockstation", stockStationRepository.findAll());
        return "etatStock";
    }


    @GetMapping("/superviseur/mouvement")
    public String etatMvt(Model model){
        model.addAttribute("mvt", indexesRepository.findAll());
        return "etatMvt";
    }
}
