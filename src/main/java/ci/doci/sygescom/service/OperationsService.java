package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.exception.BadActionException;
import ci.doci.sygescom.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class OperationsService {
    private final ClientCorporateRepository clientCorporateRepository;
    private final StockStationRepository stockStationRepository;
    private final BeneficiaireRepository beneficiaireRepository;
    private final UserService userService;
    private final DocStorageService docStorageService;
    private final VentesCorporateRepository ventesCorporateRepository;
    private final PrixInitRepository prixInitRepository;

    private final DocRepository docRepository;



    public OperationsService(ClientCorporateRepository clientCorporateRepository, StockStationRepository stockStationRepository, BeneficiaireRepository beneficiaireRepository, UserService userService, DocStorageService docStorageService, VentesCorporateRepository ventesCorporateRepository, PrixInitRepository prixInitRepository, DocRepository docRepository) {
        this.clientCorporateRepository = clientCorporateRepository;
        this.stockStationRepository = stockStationRepository;
        this.beneficiaireRepository = beneficiaireRepository;
        this.userService = userService;
        this.docStorageService = docStorageService;
        this.ventesCorporateRepository = ventesCorporateRepository;
        this.prixInitRepository = prixInitRepository;
        this.docRepository = docRepository;
    }
    /*
       Methode pour faire les actions suivantes:
       1- verifie le plafond du corporate
       2- Mets à jour le plafond
       3- Mets à jour le stock
       4- retourne l'objet corporate à jours
     */
    public BadActionException _returnClientCorporateAfterSelling(String contact, double litreEssence, double litreGazoil, Stations st, String telBenef,  MultipartFile files){
        if(clientCorporateRepository.findClientsCorporatesByContact1(contact)==null){
            String message=" Le numéro du corporate renseigné est incorrecte";
            return  userService._doDetecteErrorAction(null,message, null);
        }
        ClientsCorporates ccpSave = clientCorporateRepository.findClientsCorporatesByContact1(contact);
        if(ccpSave.getPlafonageEssence() !=0 || ccpSave.getPlafonageGazoil() !=0){

            try {

                List<Beneficiaire> beneficiaireList = beneficiaireRepository.findBeneficiaireByClientsCorporates(ccpSave);
                if(beneficiaireRepository.findBeneficiaireByContact(telBenef) == null){
                    String message=" Le numéro du bénéficiaire renseigné est incorrecte";
                    return  userService._doDetecteErrorAction(null,message, null);
                }
                Beneficiaire beneficiaire = beneficiaireRepository.findBeneficiaireByContact(telBenef);
                if(beneficiaire.getPlafonageEssence()<0 || beneficiaire.getPlafonageGazoil() <0){
                    String message=" La quantité demandée est superieur à votre stock restant";
                    return  userService._doDetecteErrorAction(null,message, null);
                }
                if(beneficiaire.getPlafonageGazoil()<litreGazoil || beneficiaire.getPlafonageEssence()<litreEssence){
                    String message=" Stock insuffisant: quantité essence " + beneficiaire.getPlafonageEssence() + ", quantité gasoil: " + beneficiaire.getPlafonageGazoil();
                    return  userService._doDetecteErrorAction(null,message, null);
                }
                double restePlafondEssenceBeneficiaire = beneficiaire.getPlafonageEssence() - litreEssence;
                double restePlafondGazoilBeneficiaire = beneficiaire.getPlafonageGazoil()-litreGazoil;

                if(beneficiaireList.contains(beneficiaire)){
                    beneficiaire.setPlafonageEssence(restePlafondEssenceBeneficiaire);
                    beneficiaire.setPlafonageGazoil(restePlafondGazoilBeneficiaire);
                    beneficiaireRepository.save(beneficiaire);

                }else{
                    String message=" Ce beneficiaire n'est pas lié à un corporate";
                    return  userService._doDetecteErrorAction(null,message, null);
                }
                if(prixInitRepository.existsById(prixInitRepository.lastId()) == false){
                    String message=" Valeurs des prix initiaux d'essence e de gazoil à la pompe ne sont pas paramétrées";
                    return  userService._doDetecteErrorAction(null,message, null);
                }
                VentesCorporate vc = new VentesCorporate();
                Long id = prixInitRepository.lastId();
                PrixInit pu = prixInitRepository.findById(id).get();

                Doc doc =docStorageService.saveFile(files);
                vc.setDoc(doc);
                doc.setDocName(files.getName());
                doc.setDocType(files.getContentType());
                doc.setData(files.getBytes());
                docRepository.save(doc);
                vc.setClientsCorporates(ccpSave);
                vc.setLitrageEssence(litreEssence);
                vc.setLitrageGazoil(litreGazoil);
                vc.setMntEssenceRemise(ccpSave.getRemiseEssence() * litreEssence );
                vc.setMntGazoilRemise(ccpSave.getRemiseGazoil() * litreGazoil);

                vc.setGerantValidate(true);
                vc.setMntglobalEssence((litreEssence * pu.getPuE())  - ccpSave.getRemiseEssence() * litreEssence );
                vc.setMntglobaGazoil((litreGazoil * pu.getPuG()) - ccpSave.getRemiseGazoil() * litreGazoil);
                vc.setDateEng(LocalDate.now());
                vc.setStations(st);
                ventesCorporateRepository.save(vc);
                //stockStationRepository.save(stockStation);
                //clientCorporateRepository.save(ccpSave);
                String message="Action réalisée avec succès. un mail a été envoyé à l'AVD pour proceder à la validation de cette opération";
                return  userService._doDetecteErrorAction(null,message,ccpSave);
            }catch (Exception e){
                String message="Le numéro du bénéficiaire n'est pas correct";
                return  userService._doDetecteErrorAction(null,message,null);
            }

        }else {
            String message="Le plafonnage de se corporate est atteint";
            return  userService._doDetecteErrorAction(null,message,ccpSave);
        }

    }


    public void _doValidateOperation(String contact, double litreEssence, double litreGazoil, Stations st) {

        ClientsCorporates ccpSave = clientCorporateRepository.findClientsCorporatesByContact1(contact);

            double restePlafondEssenceCorporate = ccpSave.getPlafonageEssence() - litreEssence;
            double restePlafondGazoilCorporate = ccpSave.getPlafonageGazoil() - litreGazoil;

            StockStation stockStation = stockStationRepository.findStockStationByStations(st);
            stockStation.setQteGlobaleEssence(stockStation.getQteGlobaleEssence() - litreEssence);
            stockStation.setQteGlobaleGazoile(stockStation.getQteGlobaleGazoile() - litreGazoil);
            stockStationRepository.save(stockStation);
            ccpSave.setPlafonageEssence(restePlafondEssenceCorporate);
            ccpSave.setPlafonageGazoil(restePlafondGazoilCorporate);
            clientCorporateRepository.save(ccpSave);



    }

}
