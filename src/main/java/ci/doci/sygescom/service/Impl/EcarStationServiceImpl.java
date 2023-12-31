package ci.doci.sygescom.service.Impl;

import ci.doci.sygescom.domaine.EcartStations;
import ci.doci.sygescom.domaine.Indexes;
import ci.doci.sygescom.repository.EcartStationsRepository;
import ci.doci.sygescom.repository.IndexesRepository;
import ci.doci.sygescom.service.EcartStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EcarStationServiceImpl implements EcartStationService {
    private final EcartStationsRepository ecartStationsRepository;
    private final IndexesRepository indexesRepository;

    public EcarStationServiceImpl(EcartStationsRepository ecartStationsRepository, IndexesRepository indexesRepository) {
        this.ecartStationsRepository = ecartStationsRepository;
        this.indexesRepository = indexesRepository;
    }

    @Override
    public EcartStations addEcarStation(EcartStations ecartStations) {
        return ecartStationsRepository.save(ecartStations);
    }

    @Override
    public EcartStations findById(Long id) {
        Optional<EcartStations>ecartStations = ecartStationsRepository.findById(id);
        if(ecartStations.isPresent()){
            EcartStations ecartStations1 =ecartStations.get();
            return ecartStations1;
        }else {
            return null;
        }

    }
}
