package ci.doci.sygescom.domaine.dto;

import ci.doci.sygescom.domaine.Stations;
import ci.doci.sygescom.domaine.StockInitStation;
import ci.doci.sygescom.domaine.StockStation;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
public class StockStationDTO {
    private long id;
/*    private double essenceInit;
    private double gazoilInit;
    private double essenceDepot;
    private double gazoilDepot;
    private LocalDate dateDepot;
    private double ecartEssence=0;
    private double ecartGazoil=0;*/
    private double qteGlobaleEssence;
    private double qteGlobaleGazoile;
  /*  private int alerte = 0;
    private int nbrIndex=0;*/
    private LocalDate dateJour;

    public static StockStationDTO toEntityDTO(StockStation stockStation){
        if(stockStation == null){
            return null;
        }
        return StockStationDTO.builder()
                .dateJour(stockStation.getDateJour())
                .qteGlobaleGazoile(stockStation.getQteGlobaleGazoile())
                .qteGlobaleEssence(stockStation.getQteGlobaleEssence())
                .build();
    }



}
