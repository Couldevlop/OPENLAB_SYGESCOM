package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.Comptes;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.domaine.dto.ComptesDto;
import ci.doci.sygescom.domaine.dto.ComptesSendDto;
import ci.doci.sygescom.service.ComptesService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("raf/comptes")
public class ComptesController {
    private final ComptesService comptesService;

    public ComptesController(ComptesService comptesService) {
        this.comptesService = comptesService;
    }


    @GetMapping("/home")
    public String accounHomePage(@AuthenticationPrincipal User user, Model model){
        model.addAttribute("compte", new Comptes());
        model.addAttribute("listeCompte", comptesService.getAllAccount());
        return "comptesPage";
    }

    @PostMapping("/saveAccount")
    public String saveComptes(@AuthenticationPrincipal User user, Model model,
                              @Valid @ModelAttribute("dto") ComptesSendDto dto){
         comptesService.saveACcount(dto);
        return "redirect:/raf/comptes/home";
    }
}
