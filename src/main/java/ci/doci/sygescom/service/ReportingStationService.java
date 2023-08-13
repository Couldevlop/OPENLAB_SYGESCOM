package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.ReportingStation;
import ci.doci.sygescom.domaine.Stations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ReportingStationService {
    ReportingStation save(ReportingStation report);
    List<ReportingStation> findAll();
    ReportingStation findById(Long id);
    void delete(Long id);

}
