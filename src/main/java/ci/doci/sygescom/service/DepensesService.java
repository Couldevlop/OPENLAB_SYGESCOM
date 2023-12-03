package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.dto.DepensesDto;
import ci.doci.sygescom.domaine.dto.DepensesSendDto;

import java.util.List;

public interface DepensesService {

    DepensesDto saveDepenses(DepensesSendDto dto);
    DepensesDto getDepensesById(Long depensesId);
    List<DepensesDto> getAullDepenses();
    DepensesDto updateDepense(DepensesSendDto dto);
    void deleteDepesesById(Long depenseId);
}
