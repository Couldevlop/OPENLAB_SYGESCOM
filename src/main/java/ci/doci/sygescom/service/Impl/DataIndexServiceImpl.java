package ci.doci.sygescom.service.Impl;

import ci.doci.sygescom.domaine.DataIndex;
import ci.doci.sygescom.repository.DataIndexRepository;
import ci.doci.sygescom.service.DataIndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class DataIndexServiceImpl implements DataIndexService {

    private final DataIndexRepository dataIndexRepository;

    public DataIndexServiceImpl(DataIndexRepository dataIndexRepository) {
        this.dataIndexRepository = dataIndexRepository;
    }

    @Override
    public DataIndex add(DataIndex dataIndex) {
        if(dataIndex == null){
            log.info("l'objet est null {} " + dataIndex);
            return null;
        }
        return dataIndexRepository.save(dataIndex);
    }

    @Override
    public DataIndex findById(Long id) {
        if(id == null){
            log.info("l'id fourni est null");
            return null;
        }
        Optional<DataIndex> dataRep = dataIndexRepository.findById(id);
        if(dataRep.isPresent()){
            return dataRep.get();
        }
        return null;
    }

    @Override
    public List<DataIndex> getAll() {
        return dataIndexRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        if(id == null){
            log.info("l'id fourni est null");
        }
        if(dataIndexRepository.existsById(id)){
            dataIndexRepository.deleteById(id);
        }else {
            log.info("l'objet que vous voulez supprimer n'existe pas dans la base de données");
        }
    }
}
