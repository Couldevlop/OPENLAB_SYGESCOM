package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.Depenses;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.domaine.dto.DepensesSendDto;
import ci.doci.sygescom.service.ComptesService;
import ci.doci.sygescom.service.DepensesService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("raf/depenses")
public class DepensesController {
    private final DepensesService depensesService;
    private final ComptesService comptesService;

    public DepensesController(DepensesService depensesService, ComptesService comptesService) {
        this.depensesService = depensesService;
        this.comptesService = comptesService;
    }


    @GetMapping("/home")
    public String depenseHomePage(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("depense", new Depenses());
        model.addAttribute("listeDepense", depensesService.getAullDepenses());
        model.addAttribute("comptes", comptesService.getAllAccount());
        model.addAttribute("user", user);
        return "depensesPage";
    }

    @PostMapping("/saveDepense")
    public String saveDepenses(@AuthenticationPrincipal User user, Model model,
                              @Valid @ModelAttribute("dto") DepensesSendDto dto){
        dto.setAuteur(user.getUsername());
         depensesService.saveDepenses(dto);
        return "redirect:/raf/depenses/home";
    }
}
