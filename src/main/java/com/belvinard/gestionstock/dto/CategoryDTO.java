package com.belvinard.gestionstock.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    @Schema(hidden = true)
    private Long id;

    @NotBlank(message = "La désignation de la catégorie est obligatoire")
    @Size(min = 4, max = 100, message = "La désignation doit contenir entre 4 et 100 caractères")
    private String designation;

    @NotBlank(message = "Le code de la catégorie est obligatoire")
    @Size(min = 3, max = 10, message = "Le code doit contenir entre 3 et 10 caractères")
    @Pattern(
            regexp = "CAT-\\w{3}|CATEFT",
            message = "Le code doit être au format CAT-XXX ou CATEFT"
    )
    private String code;

    @Schema(hidden = true)
    private String entrepriseName;

    
    @Schema(hidden = true)
    private Long entrepriseId;

    @Schema(hidden = true)
    private LocalDateTime creationDate;

    @Schema(hidden = true)
    private LocalDateTime lastModifiedDate;
}