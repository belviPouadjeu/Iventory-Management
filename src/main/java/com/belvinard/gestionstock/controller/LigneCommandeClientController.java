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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lignes-commandes")
@Tag(name = "Lignes de commande client", description = "API pour la gestion des lignes de commande client")
public class LigneCommandeClientController {

    private final LigneCommandeClientService ligneCommandeClientService;

    @Operation(
            summary = "Créer une ligne de commande client",
            description = "Crée une ligne de commande pour un article donné et décrémente automatiquement le stock"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ligne de commande créée avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande ou article non trouvé"),
            @ApiResponse(responseCode = "400", description = "Stock insuffisant ou données invalides")
    })
    @PostMapping("/commande/{commandeId}/article/{articleId}")
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

    @Operation(summary = "Récupérer toutes les lignes de commande client",
            description = "Retourne la liste de toutes les lignes de commande client avec les détails enrichis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<List<LigneCommandeClientDTO>> getAllLigneCommandeClients() {
        List<LigneCommandeClientDTO> lignes = ligneCommandeClientService.getAllLigneCommandeClients();
        return ResponseEntity.ok(lignes);
    }

    @GetMapping("/{ligneId}")
    @Operation(
            summary = "Récupérer une ligne de commande client par ID",
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
    @Operation(summary = "Mettre à jour une ligne de commande client",
            description = "Impossible si la commande est déjà livrée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ligne mise à jour",
                    content = @Content(schema = @Schema(implementation = LigneCommandeClientDTO.class))),
            @ApiResponse(responseCode = "409", description = "Commande déjà livrée")
    })
    public ResponseEntity<LigneCommandeClientDTO> updateLigneCommandeClient(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données mises à jour pour la ligne de commande",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LigneCommandeClientDTO.class))
            )
            @RequestBody LigneCommandeClientDTO ligneDTO) {

        LigneCommandeClientDTO updatedLigne = ligneCommandeClientService.updateLigneCommandeClient(id, ligneDTO);
        return ResponseEntity.ok(updatedLigne);
    }


}
