package ci.doci.sygescom.domaine;



import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "doci_user")
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "stations")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    //private String departement;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String login;
    private String contact;
    // private String codeExploitant;
    private boolean statut = true;
    private boolean activated=true;
    @ManyToOne
    private Stations  stations;
    //private boolean habiliteoperation = false;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Role> roles;


    private boolean firstConnection = true;
    private LocalDateTime passwordExpiration;
    private String oldPassword;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String userRoles[] = this.getRoles()
                .stream()
                .map(Role::getName)
                .toArray(String[]::new);

        Collection<GrantedAuthority> authorities;
        authorities = AuthorityUtils.createAuthorityList(userRoles);

        return authorities;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }



}
