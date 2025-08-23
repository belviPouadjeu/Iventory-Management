package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.EtatCommande;
import com.belvinard.gestionstock.validation.FutureOrToday;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeClientDTO {
    @Schema(hidden = true)
    private Long id;

    @Size(max = 50, message = "Le code de la commande ne doit pas dépasser 50 caractères")
    @Schema(description = "Code de la commande (généré automatiquement si non fourni)", example = "CMD-A1B2C3D4", hidden = true)
    private String code;

    @Schema(hidden = true)
    private LocalDateTime creationDate;

    @Schema(hidden = true)
    private LocalDateTime lastModifiedDate;

    @NotNull(message = "La date de livraison souhaitée est obligatoire")
    @FutureOrToday(message = "La date de livraison ne peut pas être antérieure à aujourd'hui")
    @Schema(description = "Date de livraison souhaitée (format: YYYY-MM-DD)", example = "2026-02-10", required = true)
    private LocalDate dateCommande;

    @NotNull(message = "L'état de la commande est obligatoire")
    @Schema(description = "État de la commande", example = "EN_PREPARATION")
    private EtatCommande etatCommande;

    @Schema(description = "Commentaire sur la commande", example = "Commande urgente")
    private String commentaire;

    @NotNull(message = "L'ID de l'entreprise est obligatoire")
    @Schema(description = "ID de l'entreprise", example = "1")
    private Long entrepriseId;

    @Schema(hidden = true)
    private Long clientId;

    @Schema(hidden = true)
    private String clientName;

    @Schema(hidden = true)
    private List<LigneCommandeClientDTO> ligneCommandeClients;
}
