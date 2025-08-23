package com.belvinard.gestionstock.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        // Configuration de la réponse
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Création du message d'erreur personnalisé
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", java.time.Instant.now().toString());
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message",
                "🔐 Authentification requise : Vous devez vous connecter pour accéder à cette ressource.");
        errorResponse.put("details",
                "Veuillez vous authentifier avec des identifiants valides ou vérifier que votre token JWT est valide et non expiré.");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("method", request.getMethod());

        // Informations supplémentaires pour aider l'utilisateur
        errorResponse.put("loginEndpoint", "/api/v1/auth/public/signin");
        errorResponse.put("hint", "Utilisez l'endpoint de connexion pour obtenir un token d'authentification.");

        // Écriture de la réponse JSON
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
