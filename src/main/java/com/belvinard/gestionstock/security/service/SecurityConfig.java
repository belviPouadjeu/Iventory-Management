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
                        // ========== ENDPOINTS PUBLICS ==========
                        .requestMatchers("/api/v1/auth/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/categories/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/*/image-url").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/entreprise/*/image-url").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/entreprise/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/entreprise/*").permitAll()

                        // ========== ADMIN UNIQUEMENT ==========
                        .requestMatchers("/api/v1/utilisateurs/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/entreprise/create").hasRole("ADMIN")
                        .requestMatchers("/api/v1/entreprise/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/entreprise/*/image").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/articles/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/articles/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/categories/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/clients/create/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/clients/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/commande-clients/create/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/commande-clients/*/etat/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/commande-clients/*").hasRole("ADMIN")
                        .requestMatchers("/api/v1/commandes-fournisseurs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/fournisseurs/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/files/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/article/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/ventes/admin/*").hasRole("ADMIN")

                        // ========== ADMIN OU STOCK_MANAGER ==========
                        .requestMatchers(HttpMethod.PUT, "/api/v1/articles/*/image").hasAnyRole("ADMIN", "STOCK_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/historique/article/**").hasAnyRole("ADMIN", "STOCK_MANAGER")
                        .requestMatchers("/api/v1/lignes-commande-fournisseur/**").hasAnyRole("ADMIN", "STOCK_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mouvements-stock/entree").hasAnyRole("ADMIN", "STOCK_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mouvements-stock/sortie").hasAnyRole("ADMIN", "STOCK_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mouvements-stock/correction").hasAnyRole("ADMIN", "STOCK_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mouvements-stock/vente/*").hasAnyRole("ADMIN", "STOCK_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mouvements-stock/commande-fournisseur/*").hasAnyRole("ADMIN", "STOCK_MANAGER")

                        // ========== ADMIN OU SALES_MANAGER ==========
                        .requestMatchers("/api/v1/lignes-commandes/**").hasAnyRole("ADMIN", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/lignes-vente/vente/*").hasAnyRole("ADMIN", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/lignes-vente/*").hasAnyRole("ADMIN", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/lignes-vente/vente/*").hasAnyRole("ADMIN", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/lignes-vente/*/quantity").hasAnyRole("ADMIN", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente").hasAnyRole("ADMIN", "SALES_MANAGER")

                        // ========== ADMIN OU MANAGER (legacy) ==========
                        .requestMatchers("/api/v1/categories/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/files/upload").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/files/download/*").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/files/list").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/files/url/*").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clients/*").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clients/all").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/commande-clients").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/commande-clients/*").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/commande-clients/*/lignes").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/fournisseurs/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/entreprise/*").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/type/*").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/source/*").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/date-range").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/stock-actuel/*").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/historique/*").hasAnyRole("ADMIN", "MANAGER")

                        // ========== ADMIN OU SALES ==========
                        .requestMatchers("/api/v1/ventes/sales/**").hasAnyRole("ADMIN", "SALES")

                        // ========== CONSULTATION ARTICLES (ADMIN, STOCK_MANAGER, SALES_MANAGER) ==========
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/all").hasAnyRole("ADMIN", "STOCK_MANAGER", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/*").hasAnyRole("ADMIN", "STOCK_MANAGER", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/manager/code/**").hasAnyRole("ADMIN", "STOCK_MANAGER", "SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/category/**").hasAnyRole("ADMIN", "STOCK_MANAGER", "SALES_MANAGER")

                        // ========== CONSULTATION VENTES (ADMIN, SALES, MANAGER) ==========
                        .requestMatchers(HttpMethod.GET, "/api/v1/ventes/**").hasAnyRole("ADMIN", "SALES", "MANAGER")

                        // ========== LIGNES VENTE CONSULTATION (ADMIN, SALES_MANAGER, SALES_REP) ==========
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente/*").hasAnyRole("ADMIN", "SALES_MANAGER", "SALES_REP")
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente/vente/*").hasAnyRole("ADMIN", "SALES_MANAGER", "SALES_REP")
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente/article/*").hasAnyRole("ADMIN", "SALES_MANAGER", "SALES_REP")
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente/vente/*/total").hasAnyRole("ADMIN", "SALES_MANAGER", "SALES_REP")
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente/check-stock/*").hasAnyRole("ADMIN", "SALES_MANAGER", "SALES_REP")

                        // ========== AUTHENTIFICATION REQUISE POUR LE RESTE ==========
                        .requestMatchers("/api/v1/auth/user").authenticated()
                        .requestMatchers("/api/v1/auth/username").authenticated()
                        .requestMatchers("/api/v1/utilisateurs/changer-mot-de-passe").authenticated()

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
        if (!entrepriseRepository.existsByNom("Default Company")) {
            Entreprise entreprise = new Entreprise();
            entreprise.setNom("Default Company");
            entreprise.setDescription("Entreprise par défaut du système");
            entreprise.setCodeFiscal("DEFAULT001");
            entreprise.setEmail("contact@default.com");
            entreprise.setNumTel("+1234567890");
            return entrepriseRepository.save(entreprise);
        }

        Entreprise entreprise = entrepriseRepository.findByNom("Default Company");
        if (entreprise == null) {
            throw new RuntimeException("Entreprise par défaut non trouvée");
        }
        return entreprise;
    }
}