package ci.doci.sygescom.service.Impl;

import ci.doci.sygescom.domaine.Indexes;
import ci.doci.sygescom.domaine.ResponseEntity;
import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.dto.IndexeDTO;
import ci.doci.sygescom.repository.IndexesRepository;
import ci.doci.sygescom.repository.StationsRepository;
import ci.doci.sygescom.service.OperationIndexesService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service
public class OperationsIndexesServiceImpl implements OperationIndexesService {
    private final  IndexesRepository indexesRepository;
    private final StationsRepository stationsRepository;
    private final ModelMapper modelMapper;

    public OperationsIndexesServiceImpl(IndexesRepository indexesRepository, StationsRepository stationsRepository, ModelMapper modelMapper) {
        this.indexesRepository = indexesRepository;
        this.stationsRepository = stationsRepository;
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
        if(indexesRepository.findAll().isEmpty()){
            error = "Aun indexe du jour n'a été trouvé";
            return  new ResponseEntity(error, null, null);
        }else{
            List<Indexes> indexesList = indexesRepository.findAll();
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

}
