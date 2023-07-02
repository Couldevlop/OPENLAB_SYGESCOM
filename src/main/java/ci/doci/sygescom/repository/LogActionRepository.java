package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.LogAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LogActionRepository extends JpaRepository<LogAction, Long > {

List<LogAction> findLogActionByLocalDate(LocalDate localDate);

@Query(value = " SELECT * FROM log_action L WHERE L.local_date BETWEEN :date1  AND :date2", nativeQuery = true)
List<LogAction> logsemaine(@Param("date1") String date1, @Param("date2") String date2);

@Query(value = " SELECT * FROM log_action L WHERE L.local_date BETWEEN :date1  AND :date2", nativeQuery = true)
List<LogAction> logmois(@Param("date2") String date2, @Param("date1") String date1);


}
