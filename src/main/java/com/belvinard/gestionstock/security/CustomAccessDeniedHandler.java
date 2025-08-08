package com.belvinard.gestionstock.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // Configuration de la réponse
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Création du message d'erreur personnalisé
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", java.time.Instant.now().toString());
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("error", "Access Denied");
        errorResponse.put("message",
                "🚫 Accès refusé : Vous n'avez pas les permissions nécessaires pour accéder à cette ressource.");
        errorResponse.put("details",
                "Votre rôle actuel ne vous permet pas d'effectuer cette action. Contactez votre administrateur pour obtenir les droits d'accès appropriés.");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("method", request.getMethod());

        // Ajout d'informations sur les rôles requis (si disponible)
        String requiredRoles = getRequiredRolesFromRequest(request);
        if (requiredRoles != null) {
            errorResponse.put("requiredRoles", requiredRoles);
        }

        // Écriture de la réponse JSON
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Tente d'extraire les rôles requis depuis la requête
     * (Cette méthode peut être améliorée selon vos besoins)
     */
    private String getRequiredRolesFromRequest(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Mapping basique des endpoints vers les rôles requis
        if (path.contains("/admin/")) {
            return "ROLE_ADMIN";
        } else if (path.contains("/manager/")) {
            return "ROLE_ADMIN, ROLE_STOCK_MANAGER, ROLE_SALES_MANAGER";
        } else if (path.contains("/sales/")) {
            return "ROLE_ADMIN, ROLE_SALES_MANAGER, ROLE_SALES_REP";
        }

        return null;
    }
}
