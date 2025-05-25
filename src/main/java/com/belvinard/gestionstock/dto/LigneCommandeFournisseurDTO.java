package com.belvinard.gestionstock.dto;

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

 @NotNull(message = "L'article est obligatoire")
 private Long articleId;

 @NotNull(message = "La quantité est obligatoire")
 @DecimalMin(value = "0.1", message = "La quantité doit être supérieure à 0")
 private BigDecimal quantite;

 @NotNull(message = "Le prix unitaire HT est obligatoire")
 @DecimalMin(value = "1.0", message = "Le prix unitaire HT doit être au moins 1")
 private BigDecimal prixUnitaireHt;

 @NotNull(message = "Le taux de TVA est obligatoire")
 @DecimalMin(value = "0.0", message = "Le taux de TVA ne peut pas être négatif")
 private BigDecimal tauxTva;

 @Schema(hidden = true)
 private BigDecimal prixUnitaireTtc;

 @NotNull(message = "L'identifiant de la commande fournisseur est obligatoire")
 private Long commandeFournisseurId;

 private String commandeFournisseurName;

 private String articleName;

}
