package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.Versement;

import java.util.List;
import java.util.Optional;

public interface VersementService {
    Versement addVersement(Versement versement);
    List<Versement> getAllVersement();
    Optional <Versement> findById(Long id);
}
