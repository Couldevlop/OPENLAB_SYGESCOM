package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.BonLivraison;
import ci.doci.sygescom.domaine.ClientsCorporates;
import ci.doci.sygescom.domaine.CorporateDirecte;
import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.repository.BonlivraisonRepository;
import ci.doci.sygescom.repository.ClientCorporateRepository;
import ci.doci.sygescom.repository.CorporateDirecteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ControleService {

    private final BonlivraisonRepository bonlivraisonRepository;
    private final CorporateDirecteRepository corporateDirecteRepository;
    private final ClientCorporateRepository clientCorporateRepository;

    public ControleService(BonlivraisonRepository bonlivraisonRepository, CorporateDirecteRepository corporateDirecteRepository, ClientCorporateRepository clientCorporateRepository) {
        this.bonlivraisonRepository = bonlivraisonRepository;
        this.corporateDirecteRepository = corporateDirecteRepository;
        this.clientCorporateRepository = clientCorporateRepository;
    }

    /*
    Verification si aucun BL non validés existe dans le circuit avant de changer le prix unitaire
     */


   public boolean _doVerificationBeforeChangePrice(Stations st){
       List<BonLivraison> bl = bonlivraisonRepository.findByStations(st);
        for (BonLivraison blivr:bl) {
            if(blivr.getCloturer()==true){
                return true;
            }else
                return false;
        }
        return false;
    }

    public boolean _doVerificationBeforeChangePrice1 (){
        List<CorporateDirecte> cc = corporateDirecteRepository.findAll();
        for (CorporateDirecte blcorp:cc) {
            if(blcorp.getCloturer()==false){
                return false;
            }
        }
        return true;
    }
}
