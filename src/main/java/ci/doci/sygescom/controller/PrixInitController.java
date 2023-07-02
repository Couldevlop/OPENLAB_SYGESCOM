package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.LogAction;
import ci.doci.sygescom.domaine.PrixInit;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.repository.LogActionRepository;
import ci.doci.sygescom.repository.PrixInitRepository;
import ci.doci.sygescom.service.ControleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;

@Controller
@Slf4j
public class PrixInitController {
    private final PrixInitRepository prixInitRepository;
    private final ControleService controleService;
    private final LogActionRepository logActionRepository;

    public PrixInitController(PrixInitRepository prixInitRepository, ControleService controleService, LogActionRepository logActionRepository) {
        this.prixInitRepository = prixInitRepository;
        this.controleService = controleService;
        this.logActionRepository = logActionRepository;
    }

    @GetMapping("/parametrages/prixPompe")
    public String parametragePompe(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("listPrixPompe", prixInitRepository.findAll());
        model.addAttribute("prix", new PrixInit());
        model.addAttribute("user", user);
        return "prixInit";
    }

    @PostMapping("/parametrages/addPrixPompeold")
    public String addPrixPompeold(@ModelAttribute("prix") PrixInit prixInit,
                                  @AuthenticationPrincipal User user, Model model) {
        if (controleService._doVerificationBeforeChangePrice(user.getStations())) {
            prixInit.setDateEng(LocalDate.now());
            prixInitRepository.save(prixInit);
            logActionRepository.save(new LogAction(0,LocalDate.now(), user.getUsername(), user.getRoles().get(0).getName(),"Ajout de nouveau prix à la pompe",user.getUsername() + "a ajouter un nouveau prix à la pompe","Nouvelle valeur de prix à la pompe",user.getStations().getNom() ));
            return "redirect:/parametrages/prixPompe";
        } else {
            model.addAttribute("message", "Un ou plusieurs BL non cloturés sont dans le circuit. Merci de cloturer tous les BL avant de parametrer à nouveau les prix à la pompe");
            model.addAttribute("listPrixPompe", prixInitRepository.findAll());
            model.addAttribute("user", user);
            return "prixInit";
        }

    }


    @PostMapping("/parametrages/addPrixPompe")
    public String addPrixPompe(@ModelAttribute("prix") PrixInit prixInit,
                               @AuthenticationPrincipal User user, Model model){
        if(controleService._doVerificationBeforeChangePrice1()){
            prixInit.setDateEng(LocalDate.now());
            LogAction logAction = new LogAction();
            logAction.setRole(user.getRoles().toString());
            logAction.setActionRealisee(user.getUsername() + "a enregistrer un nouveau prix à la pompe de" + prixInit);
            logAction.setImpactAction("L'application prendra en compte un nouveau prix à la pompe");
            logAction.setLocalDate(LocalDate.now());
            logAction.setStation(user.getStations().getNom());
            logAction.setId(user.getId());
            logAction.setUser(user.getUsername());
            logAction.setNomAction("Ajout de nouveau prix");
            LogAction logAction1 =logActionRepository.save(logAction);
            log.info(logAction1.toString());
            prixInitRepository.save(prixInit);
            return "redirect:/parametrages/prixPompe";
        }else {
            model.addAttribute("message", "Un ou plusieurs BL non cloturés sont dans le circuit. Merci de cloturer tous les BL avant de parametrer à nouveau les prix à la pompe");
            model.addAttribute("listPrixPompe", prixInitRepository.findAll());
            model.addAttribute("user", user);
            return "prixInit";
        }

    }
}
