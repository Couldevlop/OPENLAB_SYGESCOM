package ci.doci.sygescom.service.Impl;

import ci.doci.sygescom.domaine.Comptes;
import ci.doci.sygescom.domaine.dto.ComptesDto;
import ci.doci.sygescom.domaine.dto.ComptesSendDto;
import ci.doci.sygescom.exception.comptes.NullResourceException;
import ci.doci.sygescom.exception.comptes.ResourceAlReadyExistsException;
import ci.doci.sygescom.exception.comptes.ResourceNotFoundExistsException;
import ci.doci.sygescom.mapper.ComptesAutoMapper;
import ci.doci.sygescom.repository.ComptesRepository;
import ci.doci.sygescom.service.ComptesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComptesServiceImpl implements ComptesService {
    private final ComptesRepository comptesRepository;

    public ComptesServiceImpl(ComptesRepository comptesRepository) {
        this.comptesRepository = comptesRepository;
    }


    @Override
    public ComptesDto saveACcount(ComptesSendDto dto) {
        if(dto ==  null){
            throw new NullResourceException("L'objet {} fournit est null");
        }
        if(comptesRepository.findByNumeroCompte(dto.getNumeroCompte()).isPresent()){
            throw new ResourceAlReadyExistsException("Un compte avec le numeéro: " + dto.getNumeroCompte() + " existe");
        }
        dto.setBanque(dto.getBanque().toUpperCase());
        return ComptesAutoMapper.INSTANCE.mapToDtoRetrieve(comptesRepository.save(ComptesAutoMapper.INSTANCE.mapToEntityRetrieve(dto)));
    }

    @Override
    public List<ComptesDto> getAllAccount() {
        return comptesRepository.findAll().stream().map(ComptesAutoMapper.INSTANCE::mapToDtoRetrieve).collect(Collectors.toList());
    }

    @Override
    public ComptesDto getAccountById(Long accountId) {
        Comptes comptes = null;
        if(accountId == null){
            throw new NullResourceException("L'id fournit est null");
        }
        Optional<Comptes> comptesOptional = comptesRepository.findById(accountId);
        if(comptesOptional.isPresent()){
            comptes = comptesOptional.get();
        }else {
            throw new ResourceNotFoundExistsException("Accun compte n'a été retrouvé avec cet id: " + accountId);
        }

        return ComptesAutoMapper.INSTANCE.mapToDtoRetrieve(comptes);
    }

    @Override
    public ComptesDto updateAccount(ComptesSendDto dto) {
        if(dto ==  null){
            throw new NullResourceException("L'objet {} fournit est null");
        }
        if(comptesRepository.existsById(dto.getId())){
            throw new ResourceNotFoundExistsException("Le compte que vous souhaitez modifier n'existe pas");
        }
        return ComptesAutoMapper.INSTANCE.mapToDtoRetrieve(comptesRepository.save(ComptesAutoMapper.INSTANCE.mapToEntityRetrieve(dto)));
    }

    @Override
    public void deleteAccountById(Long accountId) {
        if(accountId == null){
            throw new NullResourceException("L'id fournit est null");
        }if(comptesRepository.existsById(accountId)){
            throw new ResourceNotFoundExistsException("Le compte que vous souhaitez supprimer n'existe pas");
        }
        comptesRepository.deleteById(accountId);
    }
}
