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
import java.util.List;

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
                        .requestMatchers("/api/v1/utilisateurs/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/v1/entreprise/create").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/v1/entreprise/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/entreprise/*/image").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/articles/create").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/articles/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/v1/categories/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/clients/create/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/clients/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/commande-clients/create/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/commande-clients/*/etat/*")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/commande-clients/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/v1/commandes-fournisseurs/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/fournisseurs/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/files/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/article/*")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/ventes/admin/*").hasAuthority("ROLE_ADMIN")

                        // ========== ADMIN OU STOCK_MANAGER ==========
                        .requestMatchers(HttpMethod.PUT, "/api/v1/articles/*/image")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/historique/article/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER")
                        .requestMatchers("/api/v1/lignes-commande-fournisseur/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mouvements-stock/entree")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mouvements-stock/sortie")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mouvements-stock/correction")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mouvements-stock/vente/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mouvements-stock/commande-fournisseur/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER")

                        // ========== ADMIN OU SALES_MANAGER ==========
                        .requestMatchers("/api/v1/lignes-commandes/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/lignes-vente/vente/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/lignes-vente/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/lignes-vente/vente/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/lignes-vente/*/quantity")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER")

                        // ========== ADMIN OU MANAGER (legacy) ==========
                        .requestMatchers("/api/v1/categories/manager/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers("/api/v1/files/upload").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers("/api/v1/files/download/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers("/api/v1/files/list").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers("/api/v1/files/url/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clients/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/clients/all")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/commande-clients")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/commande-clients/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/commande-clients/*/lignes")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers("/api/v1/fournisseurs/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/entreprise/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/type/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/source/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/date-range")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/stock-actuel/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/mouvements-stock/historique/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")

                        // ========== ADMIN OU SALES ==========
                        .requestMatchers("/api/v1/ventes/sales/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES")

                        // ========== CONSULTATION ARTICLES (ADMIN, STOCK_MANAGER, SALES_MANAGER)
                        // ==========
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/all")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER", "ROLE_SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER", "ROLE_SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/manager/code/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER", "ROLE_SALES_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/category/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_STOCK_MANAGER", "ROLE_SALES_MANAGER")

                        // ========== CONSULTATION VENTES (ADMIN, SALES, MANAGER) ==========
                        .requestMatchers(HttpMethod.GET, "/api/v1/ventes/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES", "ROLE_MANAGER")

                        // ========== LIGNES VENTE CONSULTATION (ADMIN, SALES_MANAGER, SALES_REP)
                        // ==========
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER", "ROLE_SALES_REP")
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente/vente/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER", "ROLE_SALES_REP")
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente/article/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER", "ROLE_SALES_REP")
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente/vente/*/total")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER", "ROLE_SALES_REP")
                        .requestMatchers(HttpMethod.GET, "/api/v1/lignes-vente/check-stock/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SALES_MANAGER", "ROLE_SALES_REP")

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
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
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
            // Créer les rôles par défaut
            createDefaultRoles(roleRepository);

            Entreprise defaultEntreprise = createDefaultEntreprise(entrepriseRepository);

            if (!userRepository.existsByEmail("admin@gestionstock.com")) {
                Utilisateur admin = createUser("admin@gestionstock.com", "admin123", "Admin", "System", "admin",
                        defaultEntreprise, passwordEncoder);
                Utilisateur savedAdmin = userRepository.save(admin);

                // Récupérer le rôle ADMIN existant
                Roles adminRole = roleRepository.findByRoleType(RoleType.ADMIN).get(0);
                savedAdmin.setRole(adminRole);
                userRepository.save(savedAdmin);
            } else {
                // Vérifier et corriger le rôle de l'admin existant si nécessaire
                Utilisateur existingAdmin = userRepository.findByEmail("admin@gestionstock.com").orElse(null);
                if (existingAdmin != null && existingAdmin.getRole() != null) {
                    String currentRoleName = existingAdmin.getRole().getRoleName();
                    if (!"ROLE_ADMIN".equals(currentRoleName)) {
                        existingAdmin.getRole().setRoleName("ROLE_ADMIN");
                        roleRepository.save(existingAdmin.getRole());
                    }
                }
            }
        };
    }

    private Utilisateur createUser(String email, String password, String nom, String prenom, String userName,
            Entreprise entreprise, PasswordEncoder passwordEncoder) {
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

    private void createDefaultRoles(RolesRepository roleRepository) {
        // Créer tous les rôles par défaut s'ils n'existent pas
        for (RoleType roleType : RoleType.values()) {
            List<Roles> existingRoles = roleRepository.findByRoleType(roleType);
            if (existingRoles.isEmpty()) {
                Roles role = new Roles();
                role.setRoleType(roleType);
                role.setRoleName("ROLE_" + roleType.name());
                roleRepository.save(role);
            }
        }
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