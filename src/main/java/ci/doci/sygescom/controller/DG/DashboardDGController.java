package ci.doci.sygescom.controller.DG;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardDGController {

    @GetMapping("/gestion/bons")
    private String gestionBons(){
        return "gestion-bons";
    }

    @GetMapping("/etats/financiers")
    public String etatsFinanciers(){
        return "etats-financiers";
    }


}
