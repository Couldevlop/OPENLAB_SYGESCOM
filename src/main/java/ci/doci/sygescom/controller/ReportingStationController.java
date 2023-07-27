package ci.doci.sygescom.controller;

import ci.doci.sygescom.domaine.EcartStations;
import ci.doci.sygescom.domaine.ReportingStation;
import ci.doci.sygescom.domaine.StockStation;
import ci.doci.sygescom.repository.EcartStationsRepository;
import ci.doci.sygescom.repository.StockStationRepository;
import ci.doci.sygescom.service.EcartStationService;
import ci.doci.sygescom.service.ReportingStationService;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Controller
@Slf4j
public class ReportingStationController {

    private final ReportingStationService reportingStationService;
    private final EcartStationService ecartStationService;
    private final EcartStationsRepository ecartStationsRepository;
    private final StockStationRepository stockStationRepository;




    public ReportingStationController(ReportingStationService reportingStationService, EcartStationService ecartStationService, EcartStationsRepository ecartStationsRepository, StockStationRepository stockStationRepository) {
        this.reportingStationService = reportingStationService;
        this.ecartStationService = ecartStationService;
        this.ecartStationsRepository = ecartStationsRepository;
        this.stockStationRepository = stockStationRepository;
    }





    @Scheduled(cron = "0 55 23 * * ? ")
    public void _doCronJob(){
        List<StockStation>stockStationListReport = stockStationRepository.findStockStationByDateJour(LocalDate.now());
        List<EcartStations> ecartStationsListReport = ecartStationsRepository.findEcartStationsByDateJour(LocalDate.now());
        List<Long> reportSavedId=  new ArrayList<>();

        for (StockStation st:  stockStationListReport) {
            ReportingStation report = ReportingStation.builder()
                                        .stations(st.getStations())
                                        .dateJour(st.getDateJour())
                                        .ecartEssenceJour(st.getEcartEssence())
                                        .ecatGasoilJour(st.getEcartGazoil())
                                        .globalEssence(st.getQteGlobaleEssence())
                                        .globalGasoil(st.getQteGlobaleGazoile())
                                        .nbrIndex(st.getNbrIndex())
                                        .id(st.getId())
                                        .sortieEseence(0)
                                        .sortieGsoil(0)
                                        .build();
           ReportingStation reportingStationSaved = reportingStationService.save(report);
            if(ecartStationsRepository.lastIdInd(reportingStationSaved.getStations().getId()) !=null){
                Long id = ecartStationsRepository.lastIdInd(reportingStationSaved.getStations().getId());
                EcartStations ecartStations = ecartStationService.findById(id);
                reportingStationSaved.setSortieGsoil(ecartStations.getTotalIndexGazoil());
                reportingStationSaved.setSortieEseence(ecartStations.getTotalIndexEssence());
                reportingStationSaved.setEcatGasoilJour(ecartStations.getEcartTotalGasoil());
                reportingStationSaved.setEcartEssenceJour(ecartStations.getEcartTotalEssence());
                reportingStationService.save(reportingStationSaved);
                System.out.println("Cron Task :: Execution Time - {}" +  LocalDateTime.now());
            }



        }


        }


    }








