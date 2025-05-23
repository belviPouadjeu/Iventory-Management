package com.belvinard.gestionstock.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesDTO {

    private Long id;

    @NotBlank(message = "Le nom du rôle est obligatoire")
    @Size(min = 4, max = 50, message = "Le nom du rôle doit contenir entre 5 et 50 caractères")
    private String roleName;

    @JsonIgnore
    private UtilisateurDTO utilisateur;
}
