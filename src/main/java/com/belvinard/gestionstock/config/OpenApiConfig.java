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