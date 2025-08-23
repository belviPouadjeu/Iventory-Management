package com.belvinard.gestionstock.models;

public enum RoleType {
    ADMIN("Administrateur", "Gestion complète du système et des utilisateurs"),
    STOCK_MANAGER("Gestionnaire de Stock", "Supervision complète des mouvements de stock"),
    SALES_MANAGER("Responsable Commercial", "Gestion des ventes et relations clients"),
    OPERATOR("Opérateur", "Exécution des opérations quotidiennes d'entrepôt"),
    USER_BASE("Utilisateur", "Accès aux fonctionnalités de base"),
    @Deprecated
    SALES_REP("Commercial", "Rôle obsolète - migrer vers SALES_MANAGER");

    private final String displayName;
    private final String description;

    RoleType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}