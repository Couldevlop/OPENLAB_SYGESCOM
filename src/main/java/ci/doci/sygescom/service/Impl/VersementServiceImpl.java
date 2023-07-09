package ci.doci.sygescom.service.Impl;

import ci.doci.sygescom.domaine.Versement;
import ci.doci.sygescom.repository.VersementRepository;
import ci.doci.sygescom.service.VersementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VersementServiceImpl implements VersementService {
    private final VersementRepository versementRepository;

    public VersementServiceImpl(VersementRepository versementRepository) {
        this.versementRepository = versementRepository;
    }


    @Override
    public Versement addVersement(Versement versement) {
        return versementRepository.save(versement);
    }

    @Override
    public List<Versement> getAllVersement() {
        return versementRepository.findAll();
    }

    @Override
    public Optional<Versement> findById(Long id) {
        Optional<Versement> optionalVersement = versementRepository.findById(id);
        if(optionalVersement.isPresent()){
            Versement versement = optionalVersement.get();
        }
        return null ;
    }


}
