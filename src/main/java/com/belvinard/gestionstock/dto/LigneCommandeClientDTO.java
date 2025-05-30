package com.belvinard.gestionstock.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO représentant une ligne de commande client")
public class LigneCommandeClientDTO {

    @Schema(description = "Identifiant unique de la ligne de commande", hidden = true)
    private Long id;

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité doit être supérieure à zéro")
    @Schema(description = "Quantité commandée", required = true, example = "5.0")
    private BigDecimal quantite;

    @Schema(description = "Prix unitaire HT de l'article",example = "1500.00", hidden = true)
    private BigDecimal prixUnitaireHt;

    @Schema(description = "Taux de TVA en pourcentage",example = "18.0", hidden = true)
    private BigDecimal tauxTva;

    @Schema(description = "Prix unitaire TTC",example = "1770.00", hidden = true)
    private BigDecimal prixUnitaireTtc;

    @Schema(description = "Prix total TTC pour cette ligne de commande", example = "8850.00", hidden = true)
    private BigDecimal prixTotal;


    @Schema(hidden = true)
    private Long commandeClientId;
    @JsonIgnore
    @Schema(description = "Détails de la commande client (utilisé en interne, non exposé)")
    private String commandeClientName;

    @Schema(hidden = true)
    private Long articleId;
    @Schema(description = "Détails de l'article associé à la ligne",hidden = true)
    private String articleName;
}
