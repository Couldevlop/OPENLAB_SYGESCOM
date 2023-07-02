package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.DataIndex;
import ci.doci.sygescom.domaine.Indexes;
import ci.doci.sygescom.domaine.ResponseEntity;
import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.dto.IndexeDTO;

import java.util.List;
import java.util.Optional;

public interface DataIndexService {
    DataIndex add(DataIndex dataIndex);
    DataIndex findById(Long id);
    List<DataIndex> getAll();
    void deleteById(Long id);

}
