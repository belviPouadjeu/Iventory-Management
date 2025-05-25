package com.belvinard.gestionstock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO {

    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "Le code de l'article est obligatoire")
    @Size(min = 4, max = 50, message = "Le code article doit etre entre 4 et 50 caractères")
    private String codeArticle;

    @NotBlank(message = "La désignation est obligatoire")
    @Size(min = 4, max = 100, message = "La désignation doit etre entre 4 et 100 caractères")
    private String designation;

    private Long quantiteEnStock;

    @NotNull(message = "Le prix HT est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Le prix HT doit être positif")
    private BigDecimal prixUnitaireHt;

    @NotNull(message = "Le taux de TVA est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Le taux de TVA ne peut pas être négatif")
    private BigDecimal tauxTva;

    @Schema(hidden = true)
    private BigDecimal prixUnitaireTtc;

    @Schema(hidden = true)
    private Long categoryId;

    @Schema(hidden = true)
    private Long entrepriseId;

    @Schema(hidden = true)
    private String entrepriseName;

    @Schema(hidden = true)
    private String categoryDesignation;

    @Schema(hidden = true)
    private LocalDateTime creationDate;

    @Schema(hidden = true)
    private LocalDateTime lastModifiedDate;


}

