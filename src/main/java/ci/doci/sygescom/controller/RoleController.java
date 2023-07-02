package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.LogAction;
import ci.doci.sygescom.domaine.Role;
import ci.doci.sygescom.domaine.User;
import ci.doci.sygescom.repository.LogActionRepository;
import ci.doci.sygescom.repository.RoleRepository;
import ci.doci.sygescom.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@Slf4j
@RequestMapping("/admin")
public class RoleController {

    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final LogActionRepository logActionRepository;

    public RoleController(RoleService roleService, RoleRepository roleRepository, LogActionRepository logActionRepository) {
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.logActionRepository = logActionRepository;
    }

    @GetMapping("/newRole")
    public String newRole(Model model) {
        model.addAttribute("role", new Role());
        return "roleform";
    }

    @PostMapping("/newRole")
    public String newRole(@ModelAttribute("role") Role role) {
        Role roleSaved = roleService.createRole(role);
        return "redirect:/admin/roles";
    }

    @PostMapping("/updateRole")
    public String updateRole(@RequestParam("identifiant") String id,
                             @RequestParam("nom") String nom,
                             @RequestParam("description") String description) {
        Role role = roleService.findById(Long.parseLong(id));
        role.setName(nom);
        role.setDescription(description);
        roleService.createRole(role);
        return "redirect:/admin/roles";
    }

    @GetMapping("/roles")
    public String getRoles(Model model) {
        model.addAttribute("roles", roleService.findAllRoles());
        return "roles";
    }

    @GetMapping("/role/{id}")
    public String getRole(@PathVariable String id, Model model) {
        Role role = roleService.findById(Long.parseLong(id));
        model.addAttribute("role", role);
        return "roledetails";
    }

    @GetMapping("/role/{id}/delete")
    public String delete(@PathVariable String id, @AuthenticationPrincipal User user) {
        LogAction logAction = new LogAction();
        logAction.setRole(user.getRoles().toString());
        logAction.setActionRealisee(user.getUsername() + "a supprimer le role" + roleRepository.findById(Long.parseLong(id)).get());
        logAction.setImpactAction("Tous les utilisateurs habilités avec ce role n'auront plus accès à l'application");
        logAction.setLocalDate(LocalDate.now());
        logAction.setStation(user.getStations().getNom());
        logAction.setId(user.getId());
        logAction.setUser(user.getUsername());
        logAction.setNomAction("Suppression de role");
        LogAction logAction1 =logActionRepository.save(logAction);
        log.info("Nom de l'acteur: " +  logAction1.getUser() + "<br>" +
                "Action réalisée: " + logAction1.getActionRealisee() + "<br>" +
                "Auteur: " + logAction1.getUser() + "<br>" + "Role de l'auteur: " + logAction1.getRole() + "<br>" +
                "Date Operation: " + logAction1.getLocalDate() + "<br>" + logAction1.getNomAction() + "<br>" +
                "Impacte de l'action" + logAction1.getImpactAction() + "<br>" + logAction1.getStation());
        log.info(logAction1.toString());
        roleRepository.deleteById(Long.parseLong(id));
        return "redirect:/admin/roles";
    }
}
