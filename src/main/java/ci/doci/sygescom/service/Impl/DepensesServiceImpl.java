package ci.doci.sygescom.service.Impl;

import ci.doci.sygescom.domaine.Comptes;
import ci.doci.sygescom.domaine.Depenses;
import ci.doci.sygescom.domaine.dto.DepensesDto;
import ci.doci.sygescom.domaine.dto.DepensesSendDto;
import ci.doci.sygescom.exception.comptes.NullResourceException;
import ci.doci.sygescom.exception.comptes.ResourceNotFoundExistsException;
import ci.doci.sygescom.mapper.DepensesMapper;
import ci.doci.sygescom.repository.ComptesRepository;
import ci.doci.sygescom.repository.DepensesRepository;
import ci.doci.sygescom.service.DepensesService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DepensesServiceImpl implements DepensesService {
    private final DepensesRepository depensesRepository;
    private final ComptesRepository comptesRepository;

    @Override
    public DepensesDto saveDepenses(DepensesSendDto dto) {
        if(dto == null){
            throw new NullResourceException("L'objet {} fournit est null");
        }
        Depenses dp = DepensesMapper.mapToDepenses(dto);
        Depenses depenses = depensesRepository.save(dp);
        Comptes cp = comptesRepository.findById(dto.getCompte()).orElse(null);
        Comptes comptes = depenses.getCompte();
        comptes.setDepensesList(Collections.singletonList(depenses));
        comptes.setBanque(cp.getBanque());
        comptes.setVersementList(cp.getVersementList());
        comptes.setDepensesList(cp.getDepensesList());
        comptes.setSolde(cp.getSolde());
        comptes.setSoldeVeille(cp.getSoldeVeille());
        comptes.setNumeroCompte(cp.getNumeroCompte());
        comptes.setDateEnreg(cp.getDateEnreg());
        comptes.setId(cp.getId());
        comptesRepository.save(comptes);
        depenses.setBanque(comptes.getBanque());
        depensesRepository.save(depenses);
        return DepensesMapper.mapToDepensesDto(depenses);
    }

    @Override
    public DepensesDto getDepensesById(Long depensesId) {
        if(depensesId == null){
            throw  new NullResourceException("L'id fournit est null");
        }
         Depenses depense = null;
        Optional<Depenses> depensesOptional = depensesRepository.findById(depensesId);
        if(depensesOptional.isPresent()){
            depense = depensesOptional.get();
        }else {
            throw new ResourceNotFoundExistsException("Aucune depense n'a été trouvé avec cet id: " + depensesId);
        }

        return DepensesMapper.mapToDepensesDto(depense);
    }

    @Override
    public List<DepensesDto> getAullDepenses() {
        return depensesRepository.findAll().stream().map(DepensesMapper::mapToDepensesDto).collect(Collectors.toList());
    }

    @Override
    public DepensesDto updateDepense(DepensesSendDto dto) {
        if(dto == null){
            throw new NullResourceException("L'objet {} fournit est null");
        }
        if(!depensesRepository.existsById(dto.getId())){
            throw new ResourceNotFoundExistsException("La dpense que vous souhaitez modifier n'existe pas");
        }
        return DepensesMapper.mapToDepensesDto(depensesRepository.save(DepensesMapper.mapToDepenses(dto)));
    }

    @Override
    public void deleteDepesesById(Long depenseId) {
        if(depenseId == null){
            throw new NullResourceException("L'objet {} fournit est null");
        }
        if(!depensesRepository.existsById(depenseId)){
            throw new ResourceNotFoundExistsException("La dpense que vous souhaitez modifier n'existe pas");
        }
        depensesRepository.deleteById(depenseId);
    }
}
