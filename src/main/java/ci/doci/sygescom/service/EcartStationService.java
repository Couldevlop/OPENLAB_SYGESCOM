package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.EcartStations;

public interface EcartStationService {

    EcartStations addEcarStation(EcartStations ecartStations);
    EcartStations findById(Long id);

}
