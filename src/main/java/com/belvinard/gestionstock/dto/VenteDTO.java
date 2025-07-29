package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.EtatVente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenteDTO {
    @Schema(hidden = true, description = "Identifiant unique de la vente", example = "1")
    private Long id;

    @Schema(description = "Code unique de la vente", example = "V12345")
    private String code;

    @Schema(description = "Commentaire optionnel associé à la vente", example = "Vente urgente")
    private String commentaire;

    @Schema(hidden = true, description = "Identifiant de l'entreprise associée", example = "1")
    private Long idEntreprise;

    @NotNull(message = "L'état de la vente est obligatoire")
    private EtatVente etatVente;

    // Ce champ est automatiquement rempli côté backend
    @Schema(hidden = true)
    private String entrepriseName;

    @Schema(hidden = true, description = "Date de création de la vente", example = "2025-04-05T10:00:00")
    private LocalDateTime creationDate;

    @Schema(hidden = true, description = "Date de la dernière modification", example = "2025-04-05T10:00:00")
    private LocalDateTime lastModifiedDate;

}
