package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.Role;
import ci.doci.sygescom.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(Role role) {
        if (role != null)
            roleRepository.save(role);
        return null;
    }
	/*
	 * public Role findByName(String name) { Optional<Role> roleOptional =
	 * roleRepository.findByName(name); return roleOptional.orElse(null); }
	 */
    
    
    public Role findByName(String name) {
        Optional<Role> roleOptional = roleRepository.findByName(name);
        if(roleOptional.isPresent()){
            return roleOptional.get();
        }
        return null;
    }


    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElseThrow(RuntimeException::new);
    }
}
