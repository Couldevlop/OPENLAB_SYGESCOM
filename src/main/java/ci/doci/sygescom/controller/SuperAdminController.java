package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.Indexes;
import ci.doci.sygescom.repository.IndexesRepository;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class SuperAdminController {
    private final IndexesRepository indexesRepository;

    public SuperAdminController(IndexesRepository indexesRepository) {
        this.indexesRepository = indexesRepository;
    }
    @GetMapping("/superadmin/correction")
    public String superadminDashboard(){
        return "superadminForm";
    }


    @GetMapping("/superadmin/correction/indexe/affichage")
    public String correctionIndexeStationsAfficherTous(Model model){
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.set(Calendar.WEEK_OF_MONTH, -2);
        Date d = calendar.getTime();
        String date2 = sdf.format(calendar1.getTime());
        String date1 = sdf.format(d);
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate1 = LocalDate.parse(date1, formatter);
        LocalDate localDate2 = LocalDate.parse(date2, formatter);
        System.out.println(localDate1  + " -------------" + localDate2);
      model.addAttribute("LinstIndexStation", indexesRepository.findIndexesByDateJourBetween(localDate1,localDate2));
      //return "index-station-superadmin";
        return "indexe-station-affiche";

    }

    @PostMapping("/superadmin/correction/indexe/update")
    public String updateIndexeBySuperAdmin(@Valid  @ModelAttribute Indexes indexes){
        indexesRepository.save(indexes);
        return "redirect:/superadmin/correction/indexe/affichage";
    }



 /*    @Scheduled(cron = "0 * * * * ?")
    public void doSomething(){
          Logger logger = LoggerFactory.getLogger(IndexController.class);
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        String d1=s.format(d);
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        List<Indexes> listeIndex = indexesRepository.findAll();
        for(Indexes ind : listeIndex){
            LocalDate date0 = null;
            LocalDate date1 = ind.getDateJour() ++;

            if(date0.isAfter(date1) == false){

                date0 = date1;
                Indexes indexesDate0 = indexesRepository.
                double cuveDate0 = ind
            }
        }



*//*
             System.out.println("Cron Task :: Execution Time - {}" +  dateTimeFormat.format(LocalDateTime.now()));
             logger.info("Cron Task :: Execution Time - {}" ,  dateTimeFormat.format(LocalDateTime.now()));*//*
         }


        }*/
}
