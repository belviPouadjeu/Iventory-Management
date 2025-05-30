package com.belvinard.gestionstock.dto;


import com.belvinard.gestionstock.models.Adresse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
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
public class ClientDTO {
    private Long id;

    @NotBlank(message = "Le nom du client est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom du client est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;

    @Valid
    private Adresse adresse;

    @Pattern(
            regexp = ".*\\.(jpg|jpeg|png|gif|bmp)$",
            message = "Le nom du fichier photo doit être une image (jpg, jpeg, png, gif, bmp)"
    )
    private String photo;

    @NotBlank(message = "L'Email du client est obligatoire")
    @Email(message = "L'adresse email est invalide")
    private String mail;

    @Pattern(
            regexp = "^[0-9+\\s().-]{6,20}$",
            message = "Le numéro de téléphone doit contenir entre 6 et 20 caractères " +
                    "et peut inclure des chiffres, espaces, +, (), ou -"
    )
    private String numTel;


    private Long entrepriseId;
    private String entrepriseName;

    @JsonIgnore
    private List<CommandeClientDTO> commandeClients;

}
