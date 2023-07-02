package ci.doci.sygescom.repository;

import ci.doci.sygescom.domaine.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);


}
