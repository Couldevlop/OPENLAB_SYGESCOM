package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.Indexes;
import ci.doci.sygescom.domaine.ResponseEntity;
import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.dto.IndexeDTO;

import java.util.List;

public interface OperationIndexesService {
    ResponseEntity getLastIndexByStation(Stations st);
    ResponseEntity getAllIndexeForAllStations();
    List<Indexes> getAllIndexeByStationToDay();
    IndexeDTO getDetailsForOneStation(Long id);

}
