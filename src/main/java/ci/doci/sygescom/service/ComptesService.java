package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.Comptes;
import ci.doci.sygescom.domaine.dto.ComptesDto;
import ci.doci.sygescom.domaine.dto.ComptesSendDto;

import java.util.List;

public interface ComptesService {
    ComptesDto saveACcount(ComptesSendDto dto);
    List<ComptesDto> getAllAccount();
    ComptesDto getAccountById(Long accountId);
    ComptesDto updateAccount(ComptesSendDto dto);
    void deleteAccountById(Long accountId);
}
