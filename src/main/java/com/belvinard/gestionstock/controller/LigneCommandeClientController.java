package com.belvinard.gestionstock.controller;


import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.service.LigneCommandeClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/lignes-commandes")
@Tag(name = "Lignes de commande client-Controller", description = "API pour la gestion des lignes de commande client")
public class LigneCommandeClientController {

    private final LigneCommandeClientService ligneCommandeClientService;

    @PostMapping("/commande/{commandeId}/article/{articleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
            summary = "Créer une ligne de commande client (ADMIN ou SALES_MANAGER)",
            description = "Crée une ligne de commande pour un article donné et décrémente automatiquement le stock"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ligne de commande créée avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande ou article non trouvé"),
            @ApiResponse(responseCode = "400", description = "Stock insuffisant ou données invalides")
    })
    public ResponseEntity<LigneCommandeClientDTO> createLigneCommande(
            @Parameter(description = "ID de la commande client", required = true)
            @PathVariable Long commandeId,

            @Parameter(description = "ID de l'article", required = true)
            @PathVariable Long articleId,

            @Valid @RequestBody LigneCommandeClientDTO ligneDTO
    ){
        LigneCommandeClientDTO createdLigne = ligneCommandeClientService.createLigneCommandeClient(commandeId, articleId, ligneDTO);
        return ResponseEntity.ok(createdLigne);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Récupérer toutes les lignes de commande client (ADMIN ou SALES_MANAGER)",
            description = "Retourne la liste de toutes les lignes de commande client avec les détails enrichis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<LigneCommandeClientDTO>> getAllLigneCommandeClients() {
        List<LigneCommandeClientDTO> lignes = ligneCommandeClientService.getAllLigneCommandeClients();
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/{ligneId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(
            summary = "Récupérer une ligne de commande client par ID (ADMIN ou SALES_MANAGER)",
            description = "Retourne une ligne de commande client en fonction de son identifiant unique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ligne de commande client trouvée"),
            @ApiResponse(responseCode = "404", description = "Ligne de commande client non trouvée")
    })
    public ResponseEntity<LigneCommandeClientDTO> getLigneCommandeClient(
            @Parameter(description = "ID de la ligne de commande client à récupérer", required = true)
            @PathVariable Long ligneId) {

        LigneCommandeClientDTO ligneCommandeClientDTO = ligneCommandeClientService.getLigneCommandeClientById(ligneId);
        return ResponseEntity.ok(ligneCommandeClientDTO);
    }

    @PutMapping("/{ligneId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(summary = "Mettre à jour une ligne de commande client (ADMIN ou SALES_MANAGER)",
            description = "Impossible si la commande est déjà livrée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ligne mise à jour",
                    content = @Content(schema = @Schema(implementation = LigneCommandeClientDTO.class))),
            @ApiResponse(responseCode = "409", description = "Commande déjà livrée")
    })
    public ResponseEntity<LigneCommandeClientDTO> updateLigneCommandeClient(
            @PathVariable Long ligneId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données mises à jour pour la ligne de commande",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LigneCommandeClientDTO.class))
            )
            @RequestBody LigneCommandeClientDTO ligneDTO) {

        LigneCommandeClientDTO updatedLigne = ligneCommandeClientService.updateLigneCommandeClient(ligneId, ligneDTO);
        return ResponseEntity.ok(updatedLigne);
    }

    @DeleteMapping("/{ligneId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES_MANAGER')")
    @Operation(
            summary = "Supprimer une ligne de commande client (ADMIN ou SALES_MANAGER)",
            description = "Cette opération permet de supprimer une ligne de commande client sauf si la commande est déjà livrée."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ligne de commande supprimée avec succès"),
            @ApiResponse(responseCode = "409", description = "Impossible de supprimer la ligne car la commande est livrée"),
            @ApiResponse(responseCode = "404", description = "Ligne de commande non trouvée")
    })
    public ResponseEntity<LigneCommandeClientDTO> deleteLigneCommandeClient(@PathVariable Long ligneId) {
        LigneCommandeClientDTO deletedLigne = ligneCommandeClientService.deleteLigneCommandeClient(ligneId);
        return ResponseEntity.ok(deletedLigne);
    }

    @GetMapping("/article/{idArticle}/historique")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(
            summary = "Historique des commandes d'un article (ADMIN ou SALES_MANAGER)",
            description = "Récupère la liste des lignes de commande client pour un article donné"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des lignes de commande récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Article non trouvé")
    })
    public ResponseEntity<List<LigneCommandeClientDTO>> getHistoriqueCommandeClient(
            @Parameter(description = "ID de l'article concerné", example = "2")
            @PathVariable Long idArticle
    ) {
        List<LigneCommandeClientDTO> historique = ligneCommandeClientService.findHistoriqueCommandeClient(idArticle);
        return ResponseEntity.ok(historique);
    }
}

