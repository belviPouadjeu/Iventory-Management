package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.LigneCommandeFournisseurDTO;
import com.belvinard.gestionstock.responses.ErrorResponse;
import com.belvinard.gestionstock.service.LigneCommandeFournisseurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lignes-commande-fournisseur")
@Tag(name = "Ligne Commande Fournisseur", description = "API de gestion des lignes de commande fournisseur")
@RequiredArgsConstructor
public class LigneCommandeFournisseurController {

    private final LigneCommandeFournisseurService ligneCommandeFournisseurService;

    @PostMapping("/commande/{commandeFournisseurId}")
    @Operation(
            summary = "Créer une nouvelle ligne de commande fournisseur",
            description = "Cette méthode permet de créer une nouvelle ligne de commande fournisseur avec les informations fournies"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ligne de commande fournisseur créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LigneCommandeFournisseurDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<LigneCommandeFournisseurDTO> create(
            @Parameter(description = "ID de la commande fournisseur", required = true)
            @PathVariable @NotNull Long commandeFournisseurId,
            @Parameter(description = "Données de la ligne de commande fournisseur", required = true)
            @Valid @RequestBody LigneCommandeFournisseurDTO ligneCommandeFournisseurDTO) {

        LigneCommandeFournisseurDTO created = ligneCommandeFournisseurService
                .save(ligneCommandeFournisseurDTO, commandeFournisseurId, ligneCommandeFournisseurDTO.getArticleId());

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Récupérer toutes les lignes de commande fournisseur",
            description = "Cette méthode permet de récupérer la liste de toutes les lignes de commande fournisseur"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des lignes de commande fournisseur récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LigneCommandeFournisseurDTO.class)
                    )
            )
    })
    public ResponseEntity<java.util.List<LigneCommandeFournisseurDTO>> getAll() {
        java.util.List<LigneCommandeFournisseurDTO> lignes = ligneCommandeFournisseurService.getAll();
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer une ligne de commande fournisseur par ID",
            description = "Cette méthode permet de récupérer une ligne de commande fournisseur spécifique par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ligne de commande fournisseur trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LigneCommandeFournisseurDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ligne de commande fournisseur non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<LigneCommandeFournisseurDTO> findById(
            @Parameter(description = "ID de la ligne de commande fournisseur", required = true)
            @PathVariable @NotNull Long id) {
        
        LigneCommandeFournisseurDTO ligne = ligneCommandeFournisseurService.findById(id);
        return ResponseEntity.ok(ligne);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour une ligne de commande fournisseur",
            description = "Cette méthode permet de mettre à jour une ligne de commande fournisseur existante"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ligne de commande fournisseur mise à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LigneCommandeFournisseurDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ligne de commande fournisseur non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Commande déjà livrée ou données invalides",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<LigneCommandeFournisseurDTO> update(
            @Parameter(description = "ID de la ligne de commande fournisseur", required = true)
            @PathVariable @NotNull Long id,
            @Parameter(description = "Données de mise à jour de la ligne de commande fournisseur", required = true)
            @Valid @RequestBody LigneCommandeFournisseurDTO ligneCommandeFournisseurDTO) {
        
        LigneCommandeFournisseurDTO updatedLigne = ligneCommandeFournisseurService.update(id, ligneCommandeFournisseurDTO);
        return ResponseEntity.ok(updatedLigne);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une ligne de commande fournisseur",
            description = "Cette méthode permet de supprimer une ligne de commande fournisseur existante"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Ligne de commande fournisseur supprimée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ligne de commande fournisseur non trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Commande déjà livrée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la ligne de commande fournisseur", required = true)
            @PathVariable @NotNull Long id) {
        
        ligneCommandeFournisseurService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/commande/{commandeFournisseurId}")
    @Operation(
            summary = "Récupérer les lignes d'une commande fournisseur",
            description = "Cette méthode permet de récupérer toutes les lignes d'une commande fournisseur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lignes de commande récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LigneCommandeFournisseurDTO.class)
                    )
            )
    })
    public ResponseEntity<List<LigneCommandeFournisseurDTO>> findByCommandeFournisseurId(
            @Parameter(description = "ID de la commande fournisseur", required = true)
            @PathVariable @NotNull Long commandeFournisseurId) {
        
        List<LigneCommandeFournisseurDTO> lignes = ligneCommandeFournisseurService.findByCommandeFournisseurId(commandeFournisseurId);
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/article/{articleId}")
    @Operation(
            summary = "Récupérer l'historique des commandes d'un article",
            description = "Cette méthode permet de récupérer toutes les lignes de commande pour un article spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historique des commandes récupéré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LigneCommandeFournisseurDTO.class)
                    )
            )
    })
    public ResponseEntity<List<LigneCommandeFournisseurDTO>> findByArticleId(
            @Parameter(description = "ID de l'article", required = true)
            @PathVariable @NotNull Long articleId) {
        
        List<LigneCommandeFournisseurDTO> lignes = ligneCommandeFournisseurService.findByArticleId(articleId);
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/commande/{commandeFournisseurId}/total")
    @Operation(
            summary = "Calculer le total d'une commande fournisseur",
            description = "Cette méthode permet de calculer le montant total TTC d'une commande fournisseur"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Total calculé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = java.math.BigDecimal.class)
                    )
            )
    })
    public ResponseEntity<java.math.BigDecimal> getTotalByCommandeFournisseurId(
            @Parameter(description = "ID de la commande fournisseur", required = true)
            @PathVariable @NotNull Long commandeFournisseurId) {
        
        java.math.BigDecimal total = ligneCommandeFournisseurService.getTotalByCommandeFournisseurId(commandeFournisseurId);
        return ResponseEntity.ok(total);
    }

}
