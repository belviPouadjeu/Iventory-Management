package com.belvinard.gestionstock.security.service;

import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.models.RoleType;
import com.belvinard.gestionstock.models.Roles;
import com.belvinard.gestionstock.models.Utilisateur;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.repositories.RolesRepository;
import com.belvinard.gestionstock.repositories.UtilisateurRepository;
import com.belvinard.gestionstock.security.jwt.AuthEntryPointJwt;
import com.belvinard.gestionstock.security.jwt.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final UtilisateurRepository utilisateurRepository;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics
                        .requestMatchers("/api/v1/auth/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Endpoints ADMIN uniquement
                        .requestMatchers("/api/v1/utilisateurs/create").hasRole("ADMIN")
                        .requestMatchers("/api/v1/entreprise/create").hasRole("ADMIN")
                        .requestMatchers("/api/v1/articles/create").hasRole("ADMIN")
                        .requestMatchers("/api/v1/articles/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/categories/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/commandes-fournisseurs/**").hasRole("ADMIN")

                        // Endpoints ADMIN ou MANAGER
                        .requestMatchers("/api/v1/fournisseurs/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/clients/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/commandes-clients/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/categories/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/files/**").hasAnyRole("ADMIN", "MANAGER")

                        // Endpoints SALES
                        .requestMatchers("/api/v1/ventes/sales/**").hasAnyRole("ADMIN", "SALES")
                        .requestMatchers("/api/v1/lignes-commandes/**").hasAnyRole("ADMIN", "SALES_MANAGER")

                        // Endpoints consultation (ADMIN, MANAGER, SALES)
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/**").hasAnyRole("ADMIN", "STOCK_MANAGER", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/ventes/**").hasAnyRole("ADMIN", "SALES", "MANAGER")

                        // Stock management
                        .requestMatchers("/api/v1/mouvements-stock/**").hasAnyRole("ADMIN", "MANAGER", "STOCK_MANAGER")
                        .requestMatchers("/api/v1/lignes-commande-fournisseur/**").hasAnyRole("ADMIN", "STOCK_MANAGER")

                        // Articles - ADMIN uniquement
                        .requestMatchers(HttpMethod.POST, "/api/v1/articles/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/articles/admin/**").hasRole("ADMIN")

                        // Articles - ADMIN ou STOCK_MANAGER
                        .requestMatchers(HttpMethod.PUT, "/api/v1/articles/*/image").hasAnyRole("ADMIN", "STOCK_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/historique/article/**").hasAnyRole("ADMIN", "STOCK_MANAGER")

                        // Articles - ADMIN, STOCK_MANAGER ou SALES_MANAGER (consultation)
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/all").hasAnyRole("ADMIN", "STOCK_MANAGER", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/*").hasAnyRole("ADMIN", "STOCK_MANAGER", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/manager/code/**").hasAnyRole("ADMIN", "STOCK_MANAGER", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/category/**").hasAnyRole("ADMIN", "STOCK_MANAGER", "SALES_MANAGER")


                        // Tout le reste nécessite une authentification
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());
        return authenticationManagerBuilder.build();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public UserDetailsServiceImpl userDetailsService() {
        return new UserDetailsServiceImpl(utilisateurRepository);
    }

    @Bean
    public CommandLineRunner initData(RolesRepository roleRepository,
                                      UtilisateurRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      EntrepriseRepository entrepriseRepository) {
        return args -> {
            Entreprise defaultEntreprise = createDefaultEntreprise(entrepriseRepository);
            
            if (!userRepository.existsByEmail("admin@gestionstock.com")) {
                Utilisateur admin = createUser("admin@gestionstock.com", "admin123", "Admin", "System", "admin", defaultEntreprise, passwordEncoder);
                Utilisateur savedAdmin = userRepository.save(admin);
                Roles adminRole = createRole(RoleType.ADMIN, "ROLE_ADMIN", savedAdmin);
                roleRepository.save(adminRole);
                savedAdmin.setRole(adminRole);
                userRepository.save(savedAdmin);
            }
        };
    }
    
    private Utilisateur createUser(String email, String password, String nom, String prenom, String userName, Entreprise entreprise, PasswordEncoder passwordEncoder) {
        Utilisateur user = new Utilisateur();
        user.setEmail(email);
        user.setMoteDePasse(passwordEncoder.encode(password));
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setUserName(userName);
        user.setDateDeNaissance(LocalDateTime.now().minusYears(25));
        user.setEntreprise(entreprise);
        user.setActif(true);
        return user;
    }
    
    private Roles createRole(RoleType roleType, String roleName, Utilisateur utilisateur) {
        Roles role = new Roles();
        role.setRoleType(roleType);
        role.setRoleName(roleName);
        role.setUtilisateur(utilisateur);
        return role;
    }
    
    private Entreprise createDefaultEntreprise(EntrepriseRepository entrepriseRepository) {
        Entreprise existing = entrepriseRepository.findByNom("Système");
        if (existing != null) {
            return existing;
        }
        
        Entreprise entreprise = new Entreprise();
        entreprise.setNom("Système");
        entreprise.setDescription("Entreprise système par défaut");
        entreprise.setCodeFiscal("SYS001");
        entreprise.setEmail("system@gestionstock.com");
        entreprise.setNumTel("+1234567890");
        return entrepriseRepository.save(entreprise);
    }
}