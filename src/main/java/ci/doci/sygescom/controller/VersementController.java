package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.domaine.Versement;
import ci.doci.sygescom.repository.VersementRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
public class VersementController {
    private final VersementRepository versementRepository;

    public VersementController(VersementRepository versementRepository) {
        this.versementRepository = versementRepository;
    }

    @PostMapping("/comptable/newversement")
    public String Versement(@Valid @ModelAttribute("versement")Versement versement,
                            @AuthenticationPrincipal User user,
                            Model model){
        if(versement != null){
            versement.setUser(user);
            versement.setDateEnreg(LocalDate.now());
            versementRepository.save(versement);
            model.addAttribute("errore", "Un problème a survenu lors de l'enregistrement de versement");
            return "redirect:/comptable/caisse";
        }else{
            return "redirect:/comptable/caisse";
        }

    }
}
