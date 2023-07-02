package ci.doci.sygescom.controller;

import ci.doci.sygescom.service.LogActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;


@Controller
@RequestMapping("/admin/log")
@RequiredArgsConstructor
public class LogActionController {
    private final LogActionService logActionService;
    Boolean test=true;

@GetMapping("/logs")
public String getLog( Model model){
    Boolean test=true;
    model.addAttribute("test", test);
        return "log";

    }

    @GetMapping("/logs-jour")
    public String getLogOfDay( Model model){
        LocalDate localDate = LocalDate.now();
        model.addAttribute("logs", logActionService.logJour());
        Boolean test=false;
        model.addAttribute("test", test);
        return "log";
    }


    @GetMapping("/logs-semaine")
    public String getLogOfWeek( Model model){
        LocalDate localDate = LocalDate.now();
        model.addAttribute("logs", logActionService.logWeek());
        Boolean test=false;
        model.addAttribute("test", test);
        return "log";
    }

    @GetMapping("/logs-mois")
    public String getLogOfMonth( Model model){
        LocalDate localDate = LocalDate.now();
        model.addAttribute("logs", logActionService.logMois());
        Boolean test=false;
        model.addAttribute("test", test);
        return "log";
    }

}
