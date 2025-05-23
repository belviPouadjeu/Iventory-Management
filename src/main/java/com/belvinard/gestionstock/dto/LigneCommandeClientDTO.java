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

    @Schema(description = "Identifiant unique de la ligne de commande")
    private Long id;

    //@NotNull(message = "L'identifiant de la commande client est obligatoire")
    private Long commandeClientId;

    @JsonIgnore
    @Schema(description = "Détails de la commande client (utilisé en interne, non exposé)")
    private CommandeClientDTO commandeClientDetails;

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.01", message = "La quantité doit être supérieure à zéro")
    @Schema(description = "Quantité commandée", required = true, example = "5.0")
    private BigDecimal quantite;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix unitaire doit être supérieur à zéro")
    @Schema(description = "Prix unitaire de l'article", required = true, example = "10.50")
    private BigDecimal prixUnitaire;

    //@NotNull(message = "L'identifiant de l'article est obligatoire")
    private Long articleId;

    @Schema(description = "Détails de l'article associé à la ligne", required = true, hidden = true)
    private ArticleDTO articleDetails;


}
