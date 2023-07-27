package ci.doci.sygescom.service.Impl;

import ci.doci.sygescom.domaine.ReportingStation;
import ci.doci.sygescom.repository.ReportinStationRepository;
import ci.doci.sygescom.service.ReportingStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReportStationServiceImpl implements ReportingStationService {
    private final ReportinStationRepository service;

    public ReportStationServiceImpl(ReportinStationRepository service) {
        this.service = service;
    }


    @Override
    public ReportingStation save(ReportingStation report) {
        if(report == null){
            log.info("L'objet est null");
            return null;
        }
        return service.save(report);
    }

    @Override
    public List<ReportingStation> findAll() {
        return service.findAll();
    }

    @Override
    public ReportingStation findById(Long id) {
        if(id == null){
            log.error("l'id est null");
            return  null;
        }
        Optional<ReportingStation>reportingStation = service.findById(id);
        if(reportingStation.isPresent()){
            ReportingStation reportSaved = reportingStation.get();
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        if(id == null){
            log.error("l'id est null");

        }
         service.deleteById(id);
    }
}
