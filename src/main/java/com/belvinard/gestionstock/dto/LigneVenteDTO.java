package com.belvinard.gestionstock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneVenteDTO {
    @Schema(description = "Identifiant unique de la ligne de vente", example = "1")
    private Long id;

    @Schema(description = "Prix unitaire", example = "19.99")
    private BigDecimal prixUnitaire;

    @Schema(description = "Quantité vendue", example = "5")
    private BigDecimal quantite;

    @Schema(description = "Identifiant de la vente associée", example = "1")
    private Long idVente;

    @Schema(description = "Identifiant de l'article vendu", example = "1")
    private Long idArticle;

    @Schema(description = "Détails de l'article")
    private ArticleDTO articleDetails;

    @Schema(description = "Date de création", example = "2025-04-05T10:00:00")
    private LocalDateTime creationDate;

    @Schema(description = "Date de modification", example = "2025-04-05T10:00:00")
    private LocalDateTime lastModifiedDate;
}