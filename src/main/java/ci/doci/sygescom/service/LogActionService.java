package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.LogAction;

import java.time.LocalDate;
import java.util.List;

public interface LogActionService {

    List<LogAction> logJour();
    List<LogAction> logWeek();
    List<LogAction> logMois();
}
