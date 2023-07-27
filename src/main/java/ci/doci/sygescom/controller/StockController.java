package ci.doci.sygescom.controller;

import ci.doci.sygescom.repository.StockGestociRepository;
import ci.doci.sygescom.repository.StockStationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
public class StockController {

    private final StockStationRepository stockStationRepository;
    private final StockGestociRepository stockGestociRepository;

    public StockController(StockStationRepository stockStationRepository, StockGestociRepository stockGestociRepository) {
        this.stockStationRepository = stockStationRepository;
        this.stockGestociRepository = stockGestociRepository;
    }

    @GetMapping("/dg//stock")
    public String graphic(Model model){
        model.addAttribute("st", stockStationRepository.findAll());
        return "stock";
    }

    @GetMapping("/dg/stock/gestoci")
    public String gestoci(Model model){
        model.addAttribute("stgesto", stockGestociRepository.findAll());
        return "stockGestoci";
    }



}
