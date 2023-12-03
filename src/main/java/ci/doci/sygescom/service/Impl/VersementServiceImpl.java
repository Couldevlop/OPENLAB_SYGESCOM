package ci.doci.sygescom.service.Impl;

import ci.doci.sygescom.domaine.Comptes;
import ci.doci.sygescom.domaine.Versement;
import ci.doci.sygescom.repository.ComptesRepository;
import ci.doci.sygescom.repository.VersementRepository;
import ci.doci.sygescom.service.VersementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VersementServiceImpl implements VersementService {
    private final VersementRepository versementRepository;
    private final ComptesRepository comptesRepository;

    public VersementServiceImpl(VersementRepository versementRepository, ComptesRepository comptesRepository) {
        this.versementRepository = versementRepository;
        this.comptesRepository = comptesRepository;
    }


    @Override
    public Versement addVersement(Versement versement) {
        if(versement == null){
            throw new RuntimeException("L'objet {} fournit est null");
        }
        List<Versement> versementList = new ArrayList<>();
        Versement vs = versementRepository.save(versement);
        Comptes cp = vs.getCompte();
        versementList.add(vs);
        cp.setVersementList(versementList);
        comptesRepository.save(cp);
        return vs;
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
