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

    @Schema(description = "Date de la vente", example = "2025-08-09T13:00:00")
    private LocalDateTime dateVente;

    @Schema(description = "Commentaire optionnel associé à la vente", example = "Vente urgente")
    private String commentaire;

    @NotNull(message = "L'état de la vente est obligatoire")
    private EtatVente etatVente;

    @Schema(hidden = true, description = "Identifiant du client associé", example = "1")
    private Long clientId;

    @Schema(hidden = true, description = "Nom complet du client")
    private String clientName;

    @Schema(hidden = true, description = "Identifiant de l'entreprise associée", example = "1")
    private Long entrepriseId;

    @Schema(hidden = true, description = "Nom de l'entreprise")
    private String entrepriseName;

    @Schema(hidden = true, description = "Date de création de la vente", example = "2025-04-05T10:00:00")
    private LocalDateTime creationDate;

    @Schema(hidden = true, description = "Date de la dernière modification", example = "2025-04-05T10:00:00")
    private LocalDateTime lastModifiedDate;

}
