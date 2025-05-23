package com.belvinard.gestionstock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")
                ))
                .info(new Info()
                        .title("Blog API - Documentation")
                        .version("1.0")
                        .description("""
                                This API allows users to manage articles and comments in a blogging platform.
                                
                                ### Features:
                                - Create, retrieve, update, and delete articles
                                - Add comments to articles
                                - Fetch comments related to specific articles
                                - Error handling with meaningful responses
                                
                                ### Technologies Used:
                                - **Spring Boot** for backend development
                                - **JPA & Hibernate** for database interaction
                                - **Swagger/OpenAPI** for API documentation
                                - **Lombok** to reduce boilerplate code
                                
                                This API is designed for developers who want to integrate blog functionality 
                                into their applications or use it as a learning project for REST API development.
                                """)
                        .contact(new Contact()
                                .name("Technical Support")
                                .email("support@belvi.com")
                                .url("https://belvi.com/contact"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                        .termsOfService("https://belvi.com/terms"));
    }
}
