package com.belvinard.gestionstock.utils;

import com.belvinard.gestionstock.models.RoleType;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaire pour la gestion des informations des rôles
 */
public class RoleUtils {

    /**
     * Map contenant les descriptions et permissions de chaque rôle
     */
    private static final Map<RoleType, RoleInfo> ROLE_DESCRIPTIONS = new HashMap<>();

    static {
        ROLE_DESCRIPTIONS.put(RoleType.ADMIN, new RoleInfo(
            "Administrateur", 
            "Gestion complète du système et des utilisateurs",
            "Accès total à toutes les fonctionnalités",
            "Tous les endpoints de l'API",
            "🔴"
        ));
        
        ROLE_DESCRIPTIONS.put(RoleType.STOCK_MANAGER, new RoleInfo(
            "Gestionnaire de Stock", 
            "Supervision complète des mouvements de stock",
            "Gestion des articles, catégories, mouvements de stock",
            "/articles/**, /categories/**, /mouvements-stock/**",
            "📦"
        ));
        
        ROLE_DESCRIPTIONS.put(RoleType.SALES_MANAGER, new RoleInfo(
            "Responsable Commercial", 
            "Gestion des ventes et relations clients",
            "Gestion des ventes, clients, commandes",
            "/ventes/**, /clients/**, /commande-clients/**",
            "💼"
        ));
        
        ROLE_DESCRIPTIONS.put(RoleType.OPERATOR, new RoleInfo(
            "Opérateur", 
            "Exécution des opérations quotidiennes d'entrepôt",
            "Consultation et mise à jour des stocks",
            "Lecture des articles et mouvements de stock",
            "⚙️"
        ));
        
        ROLE_DESCRIPTIONS.put(RoleType.USER_BASE, new RoleInfo(
            "Utilisateur", 
            "Accès aux fonctionnalités de base",
            "Consultation limitée des données",
            "Endpoints publics et de consultation de base",
            "👤"
        ));
    }

    /**
     * Récupère les informations d'un rôle
     */
    public static RoleInfo getRoleInfo(RoleType roleType) {
        return ROLE_DESCRIPTIONS.get(roleType);
    }

    /**
     * Récupère le nom d'affichage d'un rôle
     */
    public static String getDisplayName(RoleType roleType) {
        RoleInfo info = ROLE_DESCRIPTIONS.get(roleType);
        return info != null ? info.getDisplayName() : roleType.name();
    }

    /**
     * Récupère la description d'un rôle
     */
    public static String getDescription(RoleType roleType) {
        RoleInfo info = ROLE_DESCRIPTIONS.get(roleType);
        return info != null ? info.getDescription() : "Aucune description disponible";
    }

    /**
     * Récupère les permissions d'un rôle
     */
    public static String getPermissions(RoleType roleType) {
        RoleInfo info = ROLE_DESCRIPTIONS.get(roleType);
        return info != null ? info.getPermissions() : "Aucune permission définie";
    }

    /**
     * Récupère les endpoints accessibles pour un rôle
     */
    public static String getEndpoints(RoleType roleType) {
        RoleInfo info = ROLE_DESCRIPTIONS.get(roleType);
        return info != null ? info.getEndpoints() : "Aucun endpoint défini";
    }

    /**
     * Récupère l'emoji associé à un rôle
     */
    public static String getEmoji(RoleType roleType) {
        RoleInfo info = ROLE_DESCRIPTIONS.get(roleType);
        return info != null ? info.getEmoji() : "👤";
    }

    /**
     * Récupère toutes les informations des rôles
     */
    public static Map<RoleType, RoleInfo> getAllRoleInfos() {
        return new HashMap<>(ROLE_DESCRIPTIONS);
    }

    /**
     * Classe interne pour stocker les informations d'un rôle
     */
    public static class RoleInfo {
        private final String displayName;
        private final String description;
        private final String permissions;
        private final String endpoints;
        private final String emoji;

        public RoleInfo(String displayName, String description, String permissions, String endpoints, String emoji) {
            this.displayName = displayName;
            this.description = description;
            this.permissions = permissions;
            this.endpoints = endpoints;
            this.emoji = emoji;
        }

        // Getters
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public String getPermissions() { return permissions; }
        public String getEndpoints() { return endpoints; }
        public String getEmoji() { return emoji; }

        @Override
        public String toString() {
            return String.format("%s %s - %s", emoji, displayName, description);
        }
    }
}
