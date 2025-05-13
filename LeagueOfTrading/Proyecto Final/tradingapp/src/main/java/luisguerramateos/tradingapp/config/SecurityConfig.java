package luisguerramateos.tradingapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import luisguerramateos.tradingapp.services.UsuarioService;

import java.util.Collections;
import java.util.Optional;
import luisguerramateos.tradingapp.entities.Usuario;

@Configuration
public class SecurityConfig {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/register", "/userlogin", "/css/**", "/js/**", "/test", "/testpw").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/userlogin")
                .loginProcessingUrl("/userlogin")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/precios", true)
                .failureUrl("/userlogin?error=true")
                .permitAll()
            ).logout(logout -> logout.logoutUrl("/logout")
            .logoutSuccessUrl("/userlogin?logout=true")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll())
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(username);
            if (!usuarioOpt.isPresent()) {
                throw new UsernameNotFoundException("Usuario no encontrado: " + username);
            }
            Usuario usuario = usuarioOpt.get();
            
            return new User(
                usuario.getEmail(),
                usuario.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
            );
        };
    }
}