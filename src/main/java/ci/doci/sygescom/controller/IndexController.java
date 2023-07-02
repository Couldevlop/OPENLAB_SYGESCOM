package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.domaine.dto.IndexeDTO;
import ci.doci.sygescom.service.EmailServiceImpl;
import ci.doci.sygescom.service.OperationIndexesService;
import ci.doci.sygescom.service.OperationsService;
import ci.doci.sygescom.service.UserService;
import ci.doci.sygescom.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class IndexController {

   private StationsRepository stationsRepository;

   private UserRepository userRepository;

    private InterimRepository interimRepository;

    private UserService userservice;
    private StockStationRepository service;
    private final IndexesRepository indexesRepository;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        return "redirect:/home";
    }

    @GetMapping("/cratePassword")
    public String newPasswordPage() {
        return "password-page";
    }
    @GetMapping("/login")
    public String redirecte(){
        return "login";
    }

    @GetMapping("/home")
    public String index(Model model, @AuthenticationPrincipal User user) {

        List<Role> roles = user.getRoles();
        Role role = roles.stream().findFirst().get();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        String d1=s.format(d);


        if(!user.getRoles().contains(role)){
            model.addAttribute("message","Désolé vous n'avez aucun role pour acceder à cette application");
            return "errors-action";
        }
        if(user.isFirstConnection()==false && user.isActivated() && stationsRepository.existsById(user.getStations().getId())){
           if(user.getStations().getNom().equals("DIRECTION") || user.getStations().getNom().equals("STATION DE DIANRA")){
               model.addAttribute("station", user.getStations().getNom());
               return "home";
           }

        }
        if(user.isFirstConnection()==false && user.isActivated() ){
            if(service.findStockStationByStationsId(user.getStations().getId()) == null){

                model.addAttribute("station", user.getStations().getNom());
                return "home";
            }
            StockStation st = service.findStockStationByStations(user.getStations());


            model.addAttribute("station", user.getStations().getNom());
            model.addAttribute("Gazoil",st.getQteGlobaleGazoile());
            model.addAttribute("Essence", st.getQteGlobaleEssence());


            return "home";

        }if(user.isFirstConnection() == true && user.isActivated()){
            return "password-page";

         }

        return "home";
    }


    @PostMapping("/newPassword")
    public String newPassword(
            @RequestParam("oldPassword") String oldPassword,
                              @RequestParam("newPassword") String newPassword,
                              @RequestParam("login") String login,Model model){
        if(oldPassword !="" && newPassword !="" && login !="" ){
            Optional <User> user = userRepository.findByLogin(login);
            if(user.isPresent()){
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                User user1 = user.get();
                if(user1.getPassword().equals(newPassword)){
                    String message= "le nouveau mot de passe doit etre different de l'ancien";
                    model.addAttribute("message",message);
                    return "errors-action";
                }
                user1.setPassword(newPassword);
                //user1.setFirstConnection(false);
                user1.setOldPassword(passwordEncoder.encode(user1.getPassword()));
                User userSaved =  userservice._doCreateNewPassword(user1);
                model.addAttribute("Mot de passe changé avec succès");
                return "redirect:/login";
            }else{
                String message="Mot de passe changé avec succès";
                model.addAttribute("message", message);
                return "errors-action";
            }
        }else{
            model.addAttribute("un des champs est vide");
        }
        return null;
    }


    @GetMapping("/error")
    public String error() {
        return "redirect:/home";
    }
    @GetMapping("/accessDenied")
    public String pageDened(Model model){
        model.addAttribute("message", "Vous n'êtes pas autorisé à acceder à cette page");
        return "accessDenied";
    }


    /*@Scheduled(cron = "0 * * * * ?")
    public void doSomething(){
          Logger logger = LoggerFactory.getLogger(IndexController.class);
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        String d1=s.format(d);
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        List<String> listeDateFin = interimRepository.findAllByFin();
     System.out.println(listeDateFin);

        System.out.println("La date du jour" + d1);
         for(int i=0; i<listeDateFin.size(); i++){
             System.out.println("------------la liste------------"  + listeDateFin.get(i));
             if(listeDateFin.get(i).compareTo(d1) == 0){
                 Interim inter = interimRepository.findByFin(listeDateFin.get(i));//trouver l'intermaire
                 String interimaire = inter.getInterimaire();
                 User utilisateur = userRepository.trouverUtilisateurInterimaire(interimaire);
                 User use = userRepository.findByInterim(inter); //trouver le titulaire
                 use.setActivated(true);
                 use.setStatut(true);
                 use.setRoles(use.getRoles());
                 use.setInterim(null);

                 userRepository.save(use);
                 User use1 = userRepository.findByInterim(inter);
                 //logger.info(use1.toString());
                 deleteIterim(utilisateur.getId());

             }

             System.out.println("Cron Task :: Execution Time - {}" +  dateTimeFormat.format(LocalDateTime.now()));
             logger.info("Cron Task :: Execution Time - {}" ,  dateTimeFormat.format(LocalDateTime.now()));
         }


        }*/

    public void deleteIterim(Long id){
        userRepository.deleteById(id);
    }

}
