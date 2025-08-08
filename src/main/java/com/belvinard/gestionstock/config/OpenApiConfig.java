package com.belvinard.gestionstock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        @Value("${server.port:8082}")
        private String serverPort;

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:" + serverPort)
                                                                .description("Local Development Server")))
                                .info(new Info()
                                                .title("Gestion Stock API")
                                                .version("1.0")
                                                .description("""
                                                                API de gestion de stock avec authentification JWT

                                                                ### Fonctionnalités:
                                                                - Gestion des entreprises, utilisateurs et rôles
                                                                - Gestion des articles, catégories et stock
                                                                - Gestion des clients et fournisseurs
                                                                - Gestion des commandes et ventes
                                                                - Authentification JWT sécurisée

                                                                ### 👥 Rôles et Permissions:

                                                                #### 🔴 ADMIN - Administrateur
                                                                - **Description**: Gestion complète du système et des utilisateurs
                                                                - **Permissions**: Accès total à toutes les fonctionnalités
                                                                - **Endpoints**: Tous les endpoints de l'API

                                                                #### 📦 STOCK_MANAGER - Gestionnaire de Stock
                                                                - **Description**: Supervision complète des mouvements de stock
                                                                - **Permissions**: Gestion des articles, catégories, mouvements de stock
                                                                - **Endpoints**: `/articles/**`, `/categories/**`, `/mouvements-stock/**`

                                                                #### 💼 SALES_MANAGER - Responsable Commercial
                                                                - **Description**: Gestion des ventes et relations clients
                                                                - **Permissions**: Gestion des ventes, clients, commandes
                                                                - **Endpoints**: `/ventes/**`, `/clients/**`, `/commande-clients/**`

                                                                #### ⚙️ OPERATOR - Opérateur
                                                                - **Description**: Exécution des opérations quotidiennes d'entrepôt
                                                                - **Permissions**: Consultation et mise à jour des stocks
                                                                - **Endpoints**: Lecture des articles et mouvements de stock

                                                                #### 🛒 SALES_REP - Commercial/Vendeur
                                                                - **Description**: Consultation des stocks et création de commandes
                                                                - **Permissions**: Consultation des articles, création de commandes
                                                                - **Endpoints**: Lecture des articles, création de commandes clients

                                                                #### 👤 USER_BASE - Utilisateur
                                                                - **Description**: Accès aux fonctionnalités de base
                                                                - **Permissions**: Consultation limitée des données
                                                                - **Endpoints**: Endpoints publics et de consultation de base

                                                                ### 🔐 Authentification:
                                                                1. **Connexion**: `POST /api/v1/auth/public/signin`
                                                                2. **Récupération du token JWT** dans la réponse
                                                                3. **Utilisation du token** dans l'en-tête `Authorization: Bearer <token>`

                                                                ### 🧪 Utilisateurs de Test:
                                                                Consultez l'endpoint `/api/v1/default-users/info` pour obtenir les identifiants de test.

                                                                ### Technologies:
                                                                - **Spring Boot** pour le backend
                                                                - **Spring Security** pour l'authentification
                                                                - **JPA & Hibernate** pour la base de données
                                                                - **JWT** pour l'authentification stateless
                                                                - **PostgreSQL** pour la persistance
                                                                """)
                                                .contact(new Contact()
                                                                .name("Support Technique")
                                                                .email("support@gestionstock.com")))
                                .components(new Components()
                                                .addSecuritySchemes("bearerAuth",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")))
                                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        }
}