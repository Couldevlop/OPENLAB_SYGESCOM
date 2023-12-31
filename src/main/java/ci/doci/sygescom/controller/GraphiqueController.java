package ci.doci.sygescom.controller;


import ci.doci.sygescom.domaine.StockStation;
import ci.doci.sygescom.repository.IndexesRepository;
import ci.doci.sygescom.repository.StockStationRepository;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GraphiqueController {

    private final IndexesRepository indexesRepository;
    private final StockStationRepository stockStationRepository;

    public GraphiqueController(IndexesRepository indexesRepository, StockStationRepository stockStationRepository) {
        this.indexesRepository = indexesRepository;

        this.stockStationRepository = stockStationRepository;
    }


   // @RequestMapping("/stat/stock")

   /* public ResponseEntity<?> getPiechartData(){
        List<StockStation> ListStock = stockStationRepository.findAll();
        return new ResponseEntity<>(ListStock, HttpStatus.OK);

    }
*/
    @GetMapping("/superadmin/stat/stock")
    public String getPiechartData(Model model){
        List<StockStation>stock = stockStationRepository.findAll();


        Map<String, Integer> data = new LinkedHashMap<String, Integer>();
        data.put("Ashish", 30);
        data.put("Ankit", 50);
        data.put("Gurpreet", 70);
        data.put("Mohit", 90);
        data.put("Manish", 25);
        model.addAttribute("keySet", data.keySet());
        model.addAttribute("values", data.values());
        return "graphique";
    }


}
