package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.EtatCommande;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeFournisseurDTO {

    @Schema(hidden = true)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    @NotBlank(message = "Le code de la commande est obligatoire")
    @Size(min = 1, max = 50, message = "Le code de la commande doit avoir entre 1 et 50 caractères")
    private String code;

    @NotNull(message = "L'état de la commande est obligatoire")
    private EtatCommande etatCommande;

    @Schema(hidden = true)
    private Long fournisseurId;

    @Schema(hidden = true)
    private String fournisseurName;


}
