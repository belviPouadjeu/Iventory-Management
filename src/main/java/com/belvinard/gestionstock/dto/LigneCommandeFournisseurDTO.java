package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.EtatLigneCommandeFournisseur;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class LigneCommandeFournisseurDTO {

 @Schema(hidden = true)
 private Long id;

 @NotNull(message = "La quantité est obligatoire")
 @DecimalMin(value = "0.1", message = "La quantité doit être supérieure à 0")
 private BigDecimal quantite;

 @Schema(description = "Prix unitaire HT (récupéré automatiquement depuis l'article si non fourni)", hidden = true)
 private BigDecimal prixUnitaireHt;

 @Schema(description = "Taux de TVA (récupéré automatiquement depuis l'article si non fourni)", hidden = true)
 private BigDecimal tauxTva;

 @Schema(hidden = true)
 private BigDecimal prixUnitaireTtc;

 @Schema(description = "Prix total TTC pour cette ligne de commande", example = "8850.00", hidden = true)
 private BigDecimal prixTotal;


 @Schema(hidden = true)
 private Long commandeFournisseurId;

 @Schema(hidden = true)
 private String commandeFournisseurName;

 @NotNull(message = "L'ID de l'article est obligatoire")
 @Schema(description = "ID de l'article", example = "1")
 private Long articleId;

 @Schema(hidden = true)
 private String articleName;

 @Schema(description = "État de la ligne de commande", example = "EN_PREPARATION")
 private EtatLigneCommandeFournisseur etatLigne = EtatLigneCommandeFournisseur.EN_PREPARATION;

}
