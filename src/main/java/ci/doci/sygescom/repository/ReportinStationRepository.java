package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.ReportingStation;
import ci.doci.sygescom.domaine.Stations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportinStationRepository extends JpaRepository<ReportingStation, Long> {

}
