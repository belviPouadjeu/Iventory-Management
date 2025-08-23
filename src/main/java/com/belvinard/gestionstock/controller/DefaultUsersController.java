package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.DefaultUserInfoDTO;
import com.belvinard.gestionstock.models.RoleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/default-users")
@Tag(name = "Default Users Controller", description = "API pour récupérer les informations des utilisateurs par défaut")
public class DefaultUsersController {

    @GetMapping("/info")
    @Operation(summary = "[PUBLIC] Récupérer les informations des utilisateurs par défaut", description = "Retourne la liste des utilisateurs par défaut avec leurs mots de passe non encodés et leurs rôles. Endpoint public pour faciliter les tests et la documentation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs par défaut récupérée avec succès")
    })
    public ResponseEntity<List<DefaultUserInfoDTO>> getDefaultUsersInfo() {

        List<DefaultUserInfoDTO> defaultUsers = Arrays.asList(
                new DefaultUserInfoDTO(
                        "admin@gestionstock.com",
                        "admin123",
                        "Admin",
                        "System",
                        "admin",
                        RoleType.ADMIN,
                        "Accès complet à toutes les fonctionnalités du système"),
                new DefaultUserInfoDTO(
                        "stock.manager@gestionstock.com",
                        "stock123",
                        "Stock",
                        "Manager",
                        "stockmanager",
                        RoleType.STOCK_MANAGER,
                        "Supervision complète des mouvements de stock et gestion des inventaires"),
                new DefaultUserInfoDTO(
                        "sales.manager@gestionstock.com",
                        "sales123",
                        "Sales",
                        "Manager",
                        "salesmanager",
                        RoleType.SALES_MANAGER,
                        "Gestion des ventes, relations clients et supervision de l'équipe commerciale"),
                new DefaultUserInfoDTO(
                        "operator@gestionstock.com",
                        "operator123",
                        "Operator",
                        "Warehouse",
                        "operator",
                        RoleType.OPERATOR,
                        "Exécution des opérations quotidiennes d'entrepôt et manutention"),
                new DefaultUserInfoDTO(
                        "user@gestionstock.com",
                        "user123",
                        "User",
                        "Base",
                        "userbase",
                        RoleType.USER_BASE,
                        "Accès aux fonctionnalités de base du système"));

        return ResponseEntity.ok(defaultUsers);
    }

    @GetMapping("/test-credentials")
    @Operation(summary = "[PUBLIC] Récupérer les identifiants de test", description = "Retourne uniquement les emails et mots de passe pour faciliter les tests. Format optimisé pour copier-coller.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Identifiants de test récupérés avec succès")
    })
    public ResponseEntity<TestCredentialsResponse> getTestCredentials() {

        TestCredentialsResponse response = new TestCredentialsResponse();
        response.setMessage(
                "⚠️ ATTENTION: Ces identifiants sont pour les tests uniquement. Changez-les en production !");
        response.setCredentials(Arrays.asList(
                new TestCredential("ADMIN", "admin@gestionstock.com", "admin123"),
                new TestCredential("STOCK_MANAGER", "stock.manager@gestionstock.com", "stock123"),
                new TestCredential("SALES_MANAGER", "sales.manager@gestionstock.com", "sales123"),
                new TestCredential("OPERATOR", "operator@gestionstock.com", "operator123"),
                new TestCredential("SALES_REP", "sales.rep@gestionstock.com", "salesrep123"),
                new TestCredential("USER_BASE", "user@gestionstock.com", "user123")));

        return ResponseEntity.ok(response);
    }

    // Classes internes pour les réponses
    public static class TestCredentialsResponse {
        private String message;
        private List<TestCredential> credentials;

        // Getters et setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<TestCredential> getCredentials() {
            return credentials;
        }

        public void setCredentials(List<TestCredential> credentials) {
            this.credentials = credentials;
        }
    }

    public static class TestCredential {
        private String role;
        private String email;
        private String password;

        public TestCredential(String role, String email, String password) {
            this.role = role;
            this.email = email;
            this.password = password;
        }

        // Getters et setters
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
