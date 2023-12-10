package ci.doci.sygescom.mapper;

import ci.doci.sygescom.domaine.Comptes;
import ci.doci.sygescom.domaine.dto.ComptesDto;
import ci.doci.sygescom.domaine.dto.ComptesSendDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ComptesAutoMapper {
    ComptesAutoMapper INSTANCE = Mappers.getMapper(ComptesAutoMapper.class);
    Comptes mapToEntityRetrieve(ComptesSendDto dto);
    ComptesDto mapToDtoRetrieve(Comptes comptes);

}

