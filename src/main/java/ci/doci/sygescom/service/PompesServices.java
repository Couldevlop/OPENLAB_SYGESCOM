/*package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.Pompe;
import ci.doci.sygescom.exception.BadResourceException;
//import ci.doci.sygescom.repository.PompeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PompesServices {


    private final Logger log = LoggerFactory.getLogger(PompesServices.class);


    private boolean existById(Long id){
        return pompeRepository.existsById(id);
    }

    public void update(Pompe pompe) throws BadResourceException {
        log.debug("Request to update Agences : {}", pompe);

        if(!StringUtils.isEmpty(pompe.getLibellee()) && !StringUtils.isEmpty(pompe.getDescription())){
            if(!existById(pompe.getId())){
                System.out.println("\"la variable avec l'id\" + agences.getId() + \"est introuvable\"");
                throw new BadResourceException("la variable avec l'id" + pompe.getId() + "est introuvable");

            }
            pompeRepository.save(pompe);
        }
        else {
            BadResourceException message = new BadResourceException("l'agence est introuvable");
        }

    }
}
*/