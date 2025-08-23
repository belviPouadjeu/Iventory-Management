package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.Adresse;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurDTO {

    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 4, max = 100, message = "Le nom doit contenir entre 4 et 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 4, max = 100, message = "Le prénom doit contenir entre 4 et 100 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'adresse email est invalide")
    private String email;

    @NotNull(message = "La date de naissance est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDeNaissance;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Permet la lecture depuis JSON mais pas l'écriture dans les// réponses
    private String moteDePasse;

    private Adresse adresse;

    private String photo;

    @NotNull(message = "L'entreprise est obligatoire")
    private Long entrepriseId; // 👈 Nouvelle propriété

    private List<RolesDTO> roles;
}
