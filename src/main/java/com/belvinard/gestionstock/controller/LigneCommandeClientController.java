package com.belvinard.gestionstock.controller;


import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.service.LigneCommandeClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes-commandes")
@Tag(name = "Lignes de commande client", description = "API pour la gestion des lignes de commande client")
public class LigneCommandeClientController {

    @Autowired
    private LigneCommandeClientService ligneCommandeClientService;

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
    ) {
        LigneCommandeClientDTO createdLigne = ligneCommandeClientService.createLigneCommandeClient(commandeId, articleId, ligneDTO);
        return ResponseEntity.ok(createdLigne);
    }
}
