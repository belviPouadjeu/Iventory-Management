package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Informations d'un utilisateur par défaut avec mot de passe non encodé")
public class DefaultUserInfoDTO {

    @Schema(description = "Adresse email de l'utilisateur", example = "admin@gestionstock.com")
    private String email;

    @Schema(description = "Mot de passe non encodé (pour les tests uniquement)", example = "admin123")
    private String password;

    @Schema(description = "Nom de famille de l'utilisateur", example = "Admin")
    private String nom;

    @Schema(description = "Prénom de l'utilisateur", example = "System")
    private String prenom;

    @Schema(description = "Nom d'utilisateur unique", example = "admin")
    private String userName;

    @Schema(description = "Type de rôle assigné à l'utilisateur")
    private RoleType roleType;

    @Schema(description = "Description des permissions et responsabilités du rôle")
    private String roleDescription;

    @Schema(description = "Nom complet de l'utilisateur", example = "Admin System")
    public String getFullName() {
        return nom + " " + prenom;
    }

    @Schema(description = "Nom du rôle avec préfixe", example = "ROLE_ADMIN")
    public String getRoleName() {
        return "ROLE_" + roleType.name();
    }

    @Schema(description = "Nom d'affichage du rôle", example = "Administrateur")
    public String getRoleDisplayName() {
        return roleType.getDisplayName();
    }
}
