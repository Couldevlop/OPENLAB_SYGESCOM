package ci.doci.sygescom.service.Impl;

import ci.doci.sygescom.domaine.*;
import ci.doci.sygescom.domaine.dto.IndexDTO;
import ci.doci.sygescom.domaine.dto.IndexeDTO;
import ci.doci.sygescom.repository.*;
import ci.doci.sygescom.service.OperationIndexesService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;


@Service
public class OperationsIndexesServiceImpl implements OperationIndexesService {
    private final  IndexesRepository indexesRepository;
    private final StationsRepository stationsRepository;
    private final IndexdtoRepository indexdtoRepository;

    private StockStationRepository stockStationRepository;
    private final HistoryStockStationRepository historyStockStationRepository;

    private final ModelMapper modelMapper;

    public OperationsIndexesServiceImpl(IndexesRepository indexesRepository, StationsRepository stationsRepository, IndexdtoRepository indexdtoRepository, HistoryStockStationRepository historyStockStationRepository, ModelMapper modelMapper) {
        this.indexesRepository = indexesRepository;
        this.stationsRepository = stationsRepository;
        this.indexdtoRepository = indexdtoRepository;
        this.historyStockStationRepository = historyStockStationRepository;
        this.modelMapper = modelMapper;
    }

    String error = null;

    //--------- Méthode pour recuperer les derniers enregistrements d'indexe par station-------------//
    //------------------------------------------------------------------------------------------------//

    @Override
    public ResponseEntity getLastIndexByStation(Stations st) {

        //Verifier si le parametre n'est pas null
        if(st==null){
             error = "La station fournie est null";
            return  new ResponseEntity(error, null, null);
        }

        // Verifier si la station existe
         String nbr ="12";
        Long n = Long.parseLong("nbr");
          if(stationsRepository.existsById(n) == false){
              error = "La station n'existe pas en base données.";
              return  new ResponseEntity(error, null, null);
          }
        /*  if(indexesRepository.findIndexesByDateJour(LocalDate.now()).isEmpty()){
              error = "La station n'a aucun indexe";
              return  new ResponseEntity(error, null,null);
          }*/
        List<Indexes> ind = indexesRepository.findIndexesByStationsIdAndDateJour(st.getId(), LocalDate.now());
        String success = "Opération réalisées avec succès";
        return new ResponseEntity(success, null, Collections.singletonList(ind));
    }

    //--------- Méthode pour recuperer tous les enregistrements d'indexe des stations -------------//
    //------------------------------------------------------------------------------------------------//

    @Override
    public ResponseEntity getAllIndexeForAllStations() {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.MONTH, -1);
        Date d = calendar.getTime();
        String d1 = sdf.format(calendar1.getTime());
        String d2 = sdf.format(d);
        if(indexesRepository.findAll().isEmpty()){
            error = "Aun indexe du jour n'a été trouvé";
            return  new ResponseEntity(error, null, null);
        }else{
            List<Indexes> indexesList = indexesRepository.DataOfMonth(d1,d2);
            String success="Opération effectuée avec succes";
            return new ResponseEntity(success, null, Arrays.asList(indexesList.toArray()));
        }


    }

    //--------- Méthode pour recuperer tous les enregistrements d'indexe des stations -------------//
    //------------------------------------------------------------------------------------------------//

    @Override
    public List<Indexes> getAllIndexeByStationToDay() {
            List<Indexes> listIndexes = indexesRepository.findIndexesByDateJour(LocalDate.now());
            return listIndexes;
        }



    //--------- Méthode pour recuperer les details de prise d'indexe d'une station donnée -----------//
    //------------------------------------------------------------------------------------------------//
    @Override
    public IndexeDTO getDetailsForOneStation(Long id) {
        if(id == null){
            return null;
        }
        if(indexesRepository.findIndexesByStationsId(id) == null){
            return null;
        }

        return modelMapper.map(indexesRepository.findIndexesByStationsId(id),IndexeDTO.class);
    }

    @Override
    public List<IndexDTO> getDataIndexByDay() {
        return indexdtoRepository.reportingJour();
    }



    //------------------------------------------------------------------------------------------
    //---------- MODFICATION DE STOCKS EN CAS D'ERREURS DE SAISIES D'INDEXE-------------------
    //-----------------------------------------------------------------------------------------

    public void _doUpdateIndexTableAndStockStationTableByStation(LocalDate d, Stations st, String motif){

        //----------Recuperation des lignes d'index de ce jour(jour de l'erreur)
        List<Indexes> listErreurIndex = indexesRepository.findIndexesByDateJours(d, LocalDate.now());
        for(Indexes ind: listErreurIndex){
            indexdtoRepository.deleteById(ind.getId());
        }


        //----------Recuperer les lignes dans hitoryStockStation à la date avant laquelle l'erreur s'est produite ---------
        LocalDate d1 = d.minusDays(1);
        List<HistoryStockStation> historyStockStations = historyStockStationRepository.findHistoryStockStationByDateJourAndStationsAndMotif(d1, st, motif);
        HistoryStockStation historyStockStation = historyStockStations.get(historyStockStations.size()-1);


        //--------Recuperer la ligne de la table StockStation correspondant et mise à jour-----------------
        StockStation stockStation = stockStationRepository.findStockStationByStationsId(historyStockStation.getStations().getId());
        stockStation.setQteGlobaleGazoile(historyStockStation.getQteGlobaleGazoile());
        stockStation.setQteGlobaleEssence(historyStockStation.getQteGlobaleEssence());
        stockStation.setNbrIndex(historyStockStation.getNbrIndex());
        stockStation.setStations(historyStockStation.getStations());
        stockStation.setGazoilDepot(historyStockStation.getGazoilDepot());
        stockStation.setEcartEssence(historyStockStation.getEcartEssence());
        stockStation.setEcartGazoil(historyStockStation.getEcartGazoil());
        stockStation.setEssenceDepot(historyStockStation.getEssenceDepot());
        stockStation.setAlerte(historyStockStation.getAlerte());
        stockStation.setDateJour(historyStockStation.getDateJour());
        stockStation.setId(historyStockStation.getId());
        stockStation.setEssenceInit(historyStockStation.getEssenceInit());
        stockStation.setGazoilInit(historyStockStation.getGazoilInit());
        stockStationRepository.save(stockStation);
        historyStockStation.setMotif(historyStockStation.getMotif() + " .Erreur constatée le: " + d + " de la station " + historyStockStation.getStations().getNom());

    }

}
