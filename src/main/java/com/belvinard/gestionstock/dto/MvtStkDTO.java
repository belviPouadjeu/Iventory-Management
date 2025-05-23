package com.belvinard.gestionstock.dto;


import com.belvinard.gestionstock.models.SourceMvtStk;
import com.belvinard.gestionstock.models.TypeMvtStk;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MvtStkDTO {

    @Schema(hidden = true)
    private Long id;

    @Schema(description = "Date et heure du mouvement de stock", example = "2024-05-09T14:30:00")
    private LocalDateTime dateMvt;

    @Schema(description = "Quantité déplacée lors du mouvement de stock (entrée ou sortie)", example = "5")
    private BigDecimal quantite;

    @Schema(description = "Article concerné par le mouvement de stock")
    private ArticleDTO article;

    @Schema(description = "Type de mouvement : ENTRÉE ou SORTIE", example = "ENTREE")
    private TypeMvtStk typeMvt;

    @Schema(description = "Source à l'origine du mouvement de stock (ex: COMMANDE_CLIENT, COMMANDE_FOURNISSEUR, VENTE)", example = "VENTE")
    private SourceMvtStk sourceMvt;

    @Schema(description = "Identifiant de l'entreprise liée au mouvement de stock", example = "1")
    private Long entrepriseId;

}
