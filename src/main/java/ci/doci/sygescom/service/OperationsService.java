package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.Beneficiaire;
import ci.doci.sygescom.domaine.ClientsCorporates;
import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.StockStation;
import ci.doci.sygescom.exception.BadActionException;
import ci.doci.sygescom.repository.BeneficiaireRepository;
import ci.doci.sygescom.repository.ClientCorporateRepository;
import ci.doci.sygescom.repository.StockStationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Service
public class OperationsService {
    private final ClientCorporateRepository clientCorporateRepository;
    private final StockStationRepository stockStationRepository;
    private final BeneficiaireRepository beneficiaireRepository;
    private final UserService userService;



    public OperationsService(ClientCorporateRepository clientCorporateRepository, StockStationRepository stockStationRepository, BeneficiaireRepository beneficiaireRepository, UserService userService) {
        this.clientCorporateRepository = clientCorporateRepository;
        this.stockStationRepository = stockStationRepository;
        this.beneficiaireRepository = beneficiaireRepository;
        this.userService = userService;
    }
    /*
       Methode pour faire les actions suivantes:
       1- verifie le plafond du corporate
       2- Mets à jour le plafond
       3- Mets à jour le stock
       4- retourne l'objet corporate à jours
     */
    public BadActionException _returnClientCorporateAfterSelling(String contact, double litreEssence, double litreGazoil, Stations st, String telBenef){
        if(clientCorporateRepository.findClientsCorporatesByContact1(contact)==null){
            String message=" Le numéro du corporate renseigné est incorrecte";
            return  userService._doDetecteErrorAction(null,message, null);
        }
        ClientsCorporates ccpSave = clientCorporateRepository.findClientsCorporatesByContact1(contact);
        if(ccpSave.getPlafonageEssence() !=0 || ccpSave.getPlafonageGazoil() !=0){
            double restePlafondEssenceCorporate = ccpSave.getPlafonageEssence() - litreEssence;
            double restePlafondGazoilCorporate = ccpSave.getPlafonageGazoil()-litreGazoil;
            try {
                StockStation stockStation = stockStationRepository.findStockStationByStations(st);
                stockStation.setQteGlobaleEssence(stockStation.getQteGlobaleEssence()-litreEssence);
                stockStation.setQteGlobaleGazoile(stockStation.getQteGlobaleGazoile()-litreGazoil);
                ccpSave.setPlafonageEssence(restePlafondEssenceCorporate);
                ccpSave.setPlafonageGazoil(restePlafondGazoilCorporate);
                //ClientsCorporates corporateSaved = clientCorporateRepository.save(ccpSave);

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
                   /* if(numbon.trim() == (beneficiaire.getIdentification().trim())){
                        String message=" Le numéro du bon renseigné est incorrecte";
                        return  userService._doDetecteErrorAction(beneficiaire,message, null);
                    }*/
                    beneficiaire.setPlafonageEssence(restePlafondEssenceBeneficiaire);
                    beneficiaire.setPlafonageGazoil(restePlafondGazoilBeneficiaire);
                    beneficiaireRepository.save(beneficiaire);

                }else{
                    String message=" Ce beneficiaire n'est pas lié à un corporate";
                    return  userService._doDetecteErrorAction(null,message, null);
                }

                stockStationRepository.save(stockStation);
                clientCorporateRepository.save(ccpSave);
                String message="action réalisée avec succès.";
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




}
