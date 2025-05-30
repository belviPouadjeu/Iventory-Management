package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.Adresse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FournisseurDTO {
    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Le nom du client est obligatoire")
    @Size(min = 4, max = 100, message = "Le nom doit contenir entre 4 et 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom du client est obligatoire")
    @Size(min = 4, max = 100, message = "Le prénom doit contenir 4 et 100 caractères")
    private String prenom;

    private Adresse adresse; // Note: Adresse ne devrait pas non plus avoir d'annotations JPA si c'est dans le DTO

    @Pattern(regexp = ".*\\.(jpg|jpeg|png|gif|bmp)$", message = "Le nom du fichier photo doit être une image...")
    private String photo;

    @NotBlank(message = "L'Email du client est obligatoire")
    @Email(message = "L'adresse email est invalide")
    private String mail;

    @Pattern(regexp = "^[0-9+\\s().-]{6,20}$", message = "Le numéro de téléphone doit...")
    private String numTel;

    @Schema(hidden = true)
    private String entrepriseName;

    @Schema(hidden = true)
    private Long entrepriseId;

    @JsonIgnore
    private List<CommandeFournisseurDTO> commandeFournisseurs;
}