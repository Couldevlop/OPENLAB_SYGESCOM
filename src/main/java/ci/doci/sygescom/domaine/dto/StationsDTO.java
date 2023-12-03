package ci.doci.sygescom.domaine.dto;

import ci.doci.sygescom.domaine.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationsDTO {
    private Long id;
    private String nom;


    public static StationsDTO mapToDto(Stations stations){
        return StationsDTO.builder()
                .id(stations.getId())
                .nom(stations.getNom())
                .build();
    }



}


