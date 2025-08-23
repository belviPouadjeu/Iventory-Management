package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesDTO {
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Le nom du rôle est obligatoire")
    @Size(min = 4, max = 50, message = "Le nom du rôle doit contenir entre 5 et 50 caractères")
    private String roleName;

    // Suppression de la référence utilisateur - relation gérée côté Utilisateur

    @NotNull(message = "Le type de rôle est obligatoire")
    private RoleType roleType;

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public RoleType getRoleType() {
        return roleType;
    }
}
