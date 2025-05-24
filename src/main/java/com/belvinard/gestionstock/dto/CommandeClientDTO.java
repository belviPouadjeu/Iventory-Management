package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.EtatCommande;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeClientDTO {
    @Schema(hidden = true)
    private Long id;


    @NotBlank(message = "Le code de la commande est obligatoire")
    @Size(min = 4, max = 50, message = "Le code de la commande doit contenir entre 4 et 50 caractères")
    private String code;

    @Schema(hidden = true)
    private LocalDateTime creationDate;

    @Schema(hidden = true)
    private LocalDateTime lastModifiedDate;


    @NotNull(message = "L'état de la commande est obligatoire")
    private EtatCommande etatCommande;

    @Schema(hidden = true)
    private Long clientId;

    @Schema(hidden = true)
    private Long entrepriseId;

    @Schema(hidden = true)
    private String clientName;


    @Schema(hidden = true)
    private List<LigneCommandeClientDTO> ligneCommandeClients;
}
