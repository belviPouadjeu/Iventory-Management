package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.LigneCommandeFournisseurDTO;
import com.belvinard.gestionstock.responses.ErrorResponse;
import com.belvinard.gestionstock.service.LigneCommandeFournisseurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/lignes-commande-fournisseur")
@Tag(name = "Ligne Commande Fournisseur-Controller", description = "API de gestion des lignes de commande fournisseur")
@RequiredArgsConstructor
public class LigneCommandeFournisseurController {

        private final LigneCommandeFournisseurService ligneCommandeFournisseurService;

        @PostMapping("/commande/{commandeFournisseurId}")
        @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STOCK_MANAGER')")
        @Operation(summary = "Créer une nouvelle ligne de commande fournisseur (ADMIN ou STOCK_MANAGER)", 
                description = "Crée une ligne de commande fournisseur. Le prix et la TVA sont récupérés automatiquement depuis l'article.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Données de la ligne de commande avec seulement la quantité et l'ID de l'article",
                        required = true,
                        content = @Content(mediaType = "application/json",
                                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                        name = "Ligne de commande simplifiée",
                                        summary = "Format JSON simplifié",
                                        value = """
                                                {
                                                  "quantite": 5,
                                                  "articleId": 1
                                                }
                                                """
                                )
                        )
                ))
        public ResponseEntity<LigneCommandeFournisseurDTO> create(
                        @Parameter(description = "ID de la commande fournisseur", required = true) @PathVariable @NotNull Long commandeFournisseurId,
                        @Parameter(description = "Données de la ligne de commande fournisseur", required = true) @Valid @RequestBody LigneCommandeFournisseurDTO ligneCommandeFournisseurDTO) {

                LigneCommandeFournisseurDTO created = ligneCommandeFournisseurService
                                .save(ligneCommandeFournisseurDTO, commandeFournisseurId,
                                                ligneCommandeFournisseurDTO.getArticleId());

                return new ResponseEntity<>(created, HttpStatus.CREATED);
        }

        @GetMapping
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
        @Operation(summary = "Récupérer toutes les lignes de commande fournisseur (ADMIN ou STOCK_MANAGER)", description = "Cette méthode permet de récupérer la liste de toutes les lignes de commande fournisseur")
        public ResponseEntity<java.util.List<LigneCommandeFournisseurDTO>> getAll() {
                java.util.List<LigneCommandeFournisseurDTO> lignes = ligneCommandeFournisseurService.getAll();
                return ResponseEntity.ok(lignes);
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
        @Operation(summary = "Récupérer une ligne de commande fournisseur par ID (ADMIN ou STOCK_MANAGER)", description = "Cette méthode permet de récupérer une ligne de commande fournisseur spécifique par son ID")
        public ResponseEntity<LigneCommandeFournisseurDTO> findById(
                        @Parameter(description = "ID de la ligne de commande fournisseur", required = true) @PathVariable @NotNull Long id) {

                LigneCommandeFournisseurDTO ligne = ligneCommandeFournisseurService.findById(id);
                return ResponseEntity.ok(ligne);
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STOCK_MANAGER')")
        @Operation(summary = "Mettre à jour une ligne de commande fournisseur (ADMIN ou STOCK_MANAGER)", description = "Cette méthode permet de mettre à jour une ligne de commande fournisseur existante")
        public ResponseEntity<LigneCommandeFournisseurDTO> update(
                        @Parameter(description = "ID de la ligne de commande fournisseur", required = true) @PathVariable @NotNull Long id,
                        @Parameter(description = "Données de mise à jour de la ligne de commande fournisseur", required = true) @Valid @RequestBody LigneCommandeFournisseurDTO ligneCommandeFournisseurDTO) {

                LigneCommandeFournisseurDTO updatedLigne = ligneCommandeFournisseurService.update(id,
                                ligneCommandeFournisseurDTO);
                return ResponseEntity.ok(updatedLigne);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STOCK_MANAGER')")
        @Operation(summary = "Supprimer une ligne de commande fournisseur (ADMIN ou STOCK_MANAGER)", description = "Cette méthode permet de supprimer une ligne de commande fournisseur existante")
        public ResponseEntity<Void> delete(
                        @Parameter(description = "ID de la ligne de commande fournisseur", required = true) @PathVariable @NotNull Long id) {

                ligneCommandeFournisseurService.delete(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/commande/{commandeFournisseurId}")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
        @Operation(summary = "Récupérer les lignes d'une commande fournisseur (ADMIN ou STOCK_MANAGER)", description = "Cette méthode permet de récupérer toutes les lignes d'une commande fournisseur spécifique")
        public ResponseEntity<List<LigneCommandeFournisseurDTO>> findByCommandeFournisseurId(
                        @Parameter(description = "ID de la commande fournisseur", required = true) @PathVariable @NotNull Long commandeFournisseurId) {

                List<LigneCommandeFournisseurDTO> lignes = ligneCommandeFournisseurService
                                .findByCommandeFournisseurId(commandeFournisseurId);
                return ResponseEntity.ok(lignes);
        }

        @GetMapping("/article/{articleId}")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
        @Operation(summary = "Récupérer l'historique des commandes d'un article (ADMIN ou STOCK_MANAGER)", description = "Cette méthode permet de récupérer toutes les lignes de commande pour un article spécifique")
        public ResponseEntity<List<LigneCommandeFournisseurDTO>> findByArticleId(
                        @Parameter(description = "ID de l'article", required = true) @PathVariable @NotNull Long articleId) {

                List<LigneCommandeFournisseurDTO> lignes = ligneCommandeFournisseurService.findByArticleId(articleId);
                return ResponseEntity.ok(lignes);
        }

        @GetMapping("/commande/{commandeFournisseurId}/total")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
        @Operation(summary = "Calculer le total d'une commande fournisseur (ADMIN ou STOCK_MANAGER)", description = "Cette méthode permet de calculer le montant total TTC d'une commande fournisseur")
        public ResponseEntity<java.math.BigDecimal> getTotalByCommandeFournisseurId(
                        @Parameter(description = "ID de la commande fournisseur", required = true) @PathVariable @NotNull Long commandeFournisseurId) {

                java.math.BigDecimal total = ligneCommandeFournisseurService
                                .getTotalByCommandeFournisseurId(commandeFournisseurId);
                return ResponseEntity.ok(total);
        }

        @PutMapping("/{id}/valider")
        @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STOCK_MANAGER')")
        @Operation(summary = "Valider une ligne de commande fournisseur (ADMIN ou STOCK_MANAGER)", description = "Change l'état de la ligne de EN_PREPARATION à VALIDEE")
        public ResponseEntity<LigneCommandeFournisseurDTO> validerLigne(
                        @Parameter(description = "ID de la ligne de commande fournisseur", required = true) @PathVariable @NotNull Long id) {

                LigneCommandeFournisseurDTO ligneValidee = ligneCommandeFournisseurService.validerLigne(id);
                return ResponseEntity.ok(ligneValidee);
        }
}
