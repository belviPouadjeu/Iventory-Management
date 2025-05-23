package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.EtatCommande;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeClientDTO {
    @Schema(hidden = true)
    private Long id;

    /**
     * Code unique représentant la commande.
     */
    @NotBlank(message = "Le code de la commande est obligatoire")
    @Size(min = 4, max = 50, message = "Le code de la commande doit contenir entre 4 et 50 caractères")
    private String code;

    /**
     * Date à laquelle la commande a été passée.
     */
    @Schema(hidden = true)
    private LocalDateTime creationDate;

    @Schema(hidden = true)
    private LocalDateTime lastModifiedDate;

    /**
     * État actuel de la commande (EN_PREPARATION, LIVREE, ANNULÉE...).
     */
    @NotNull(message = "L'état de la commande est obligatoire")
    private EtatCommande etatCommande;

    /**
     * ID du client ayant passé la commande.
     */
    //@NotNull(message = "L'identifiant du client est requis")
    @JsonIgnore
    @Schema(hidden = true)
    private Long clientId;

    /**
     * ID de l'entreprise à laquelle est rattachée la commande.
     */
    //@NotNull(message = "L'identifiant de l'entreprise est requis")
    @JsonIgnore
    @Schema(hidden = true)
    private Long entrepriseId;

    /**
     * Informations détaillées du client (rempli uniquement pour la consultation).
     */
    @Schema(hidden = true)
    private ClientDTO clientDetails;

    @Schema(hidden = true)
    private EntrepriseDTO entrepriseDetails;


    /**
     * Liste des lignes de commande associées (affichée uniquement lors de la consultation).
     */
    @Schema(hidden = true)
    private List<LigneCommandeClientDTO> ligneCommandeClients;
}
