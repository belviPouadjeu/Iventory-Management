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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/auth/public/**")
        );

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/public/**",
                                "/api/csrf-token",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/stock/**").hasAnyRole("ADMIN", "STOCK_MANAGER")
                        .requestMatchers("/api/v1/sales/**").hasAnyRole("ADMIN", "SALES_MANAGER")
                        .requestMatchers("/api/v1/operator/**").hasAnyRole("ADMIN", "STOCK_MANAGER", "OPERATOR")
                        .requestMatchers("/api/v1/salesrep/**").hasAnyRole("ADMIN", "SALES_MANAGER", "SALES_REP")
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .anyRequest().authenticated());
        http.exceptionHandling(exception
                -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UtilisateurRepository utilisateurRepository) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService(utilisateurRepository));
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsServiceImpl userDetailsService(UtilisateurRepository utilisateurRepository) {
        return new UserDetailsServiceImpl(utilisateurRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UtilisateurRepository utilisateurRepository) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider(utilisateurRepository))
                .build();
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
                roleRepository.save(createRole(RoleType.ADMIN, "ROLE_ADMIN", savedAdmin));
            }
            
            if (!userRepository.existsByEmail("stock@gestionstock.com")) {
                Utilisateur stockManager = createUser("stock@gestionstock.com", "stock123", "Manager", "Stock", "stockmanager", defaultEntreprise, passwordEncoder);
                Utilisateur savedStockManager = userRepository.save(stockManager);
                roleRepository.save(createRole(RoleType.STOCK_MANAGER, "ROLE_STOCK_MANAGER", savedStockManager));
            }
            
            if (!userRepository.existsByEmail("sales@gestionstock.com")) {
                Utilisateur salesManager = createUser("sales@gestionstock.com", "sales123", "Manager", "Sales", "salesmanager", defaultEntreprise, passwordEncoder);
                Utilisateur savedSalesManager = userRepository.save(salesManager);
                roleRepository.save(createRole(RoleType.SALES_MANAGER, "ROLE_SALES_MANAGER", savedSalesManager));
            }
            
            if (!userRepository.existsByEmail("operator@gestionstock.com")) {
                Utilisateur operator = createUser("operator@gestionstock.com", "operator123", "Operator", "Warehouse", "operator", defaultEntreprise, passwordEncoder);
                Utilisateur savedOperator = userRepository.save(operator);
                roleRepository.save(createRole(RoleType.OPERATOR, "ROLE_OPERATOR", savedOperator));
            }
            
            if (!userRepository.existsByEmail("salesrep@gestionstock.com")) {
                Utilisateur salesRep = createUser("salesrep@gestionstock.com", "salesrep123", "Rep", "Sales", "salesrep", defaultEntreprise, passwordEncoder);
                Utilisateur savedSalesRep = userRepository.save(salesRep);
                roleRepository.save(createRole(RoleType.SALES_REP, "ROLE_SALES_REP", savedSalesRep));
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
    
    private Entreprise createDefaultEntreprise(EntrepriseRepository entrepriseRepository) {
        Entreprise existingEntreprise = entrepriseRepository.findByNom("Système");
        if (existingEntreprise != null) {
            return existingEntreprise;
        }
        
        Entreprise entreprise = new Entreprise();
        entreprise.setNom("Système");
        entreprise.setDescription("Entreprise système par défaut");
        entreprise.setCodeFiscal("SYS001");
        entreprise.setEmail("system@gestionstock.com");
        entreprise.setNumTel("0000000000");
        entreprise.setSteWeb("https://gestionstock.com");
        return entrepriseRepository.save(entreprise);
    }
    
    private Roles createRole(RoleType roleType, String roleName, Utilisateur utilisateur) {
        return Roles.builder()
                .roleType(roleType)
                .roleName(roleName)
                .utilisateur(utilisateur)
                .build();
    }
}