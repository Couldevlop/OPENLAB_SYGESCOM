package ci.doci.sygescom.controller;


import ci.doci.sygescom.domaine.StockStation;
import ci.doci.sygescom.repository.IndexesRepository;
import ci.doci.sygescom.repository.StockStationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
    @RequestMapping("/superadmin/stat/stock")
    public String getPiechartData(){
        return "graphique";
    }
}
