package ci.doci.sygescom.ServiceImpl;

import ci.doci.sygescom.domaine.LogAction;
import ci.doci.sygescom.repository.LogActionRepository;
import ci.doci.sygescom.service.LogActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogActionServiceImpl implements LogActionService {
    private final LogActionRepository logActionRepository;

    @Override
    public List<LogAction> logJour() {
        return logActionRepository.findLogActionByLocalDate(LocalDate.now());
    }

    @Override
    public List<LogAction> logWeek() {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DATE, -7);
        Date d = calendar.getTime();
        String date2 = sdf.format(calendar1.getTime());
        String date1 = sdf.format(d);

        return logActionRepository.logsemaine(date1,date2);
    }

    @Override
    public List<LogAction> logMois() {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.MONTH, -1);
        Date d = calendar.getTime();
        String d1 = sdf.format(calendar1.getTime());
        String d2 = sdf.format(d);
        return logActionRepository.logmois(d1,d2);
    }
}
