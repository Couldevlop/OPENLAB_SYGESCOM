package ci.doci.sygescom.config;

import ci.doci.sygescom.security.CustomAuthenticationProvider;
import ci.doci.sygescom.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/h2-console/**", "/", "/resources/**", "/static/**", "**/assets/**",
                        "/public/**", "/console/**", "/configuration/**", "/*.html", "/**/*.css", "/**/*.js", "/**/*.min.js",
                        "/**/*.png", "/**/*.jpg", "/**/*.gif", "/**/*.svg", "/**/*.ico", "/**/*.ttf",
                        "/**/*.woff", "/**/*.min.css");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/admin/**").hasAnyRole("ADMIN","SUPERVISEUR","SUPERADMIN")
                .antMatchers("/validation/**").hasAnyRole("VALIDATEUR", "SYSDBA")
                .antMatchers("/gerant/**").hasAnyRole("GERANT","SUPERVISEUR")
                .antMatchers("/superviseur/**").hasAnyRole("SUPERVISEUR","GERANT","ASSISTANTE","RAF","RESPCOM","ADMIN")
                .antMatchers("/controle/**").hasAnyRole("DG","ADMIN","CONTROLEUR")
                .antMatchers("/dg/**").hasAnyRole("DG","ADMIN")
                .antMatchers("/raf/**").hasAnyRole("DG","SUPERADMIN","RAF")
                .antMatchers("/superadmin/**").hasAnyRole("SUPERADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/accessDenied");
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //auth.authenticationProvider(customAuthenticationProvider);//LDAP
        auth.authenticationProvider(authenticationProvider());
    }
}
