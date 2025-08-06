package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.CommandeClientDTO;
import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.models.EtatCommande;
import com.belvinard.gestionstock.service.CommandeClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/commande-clients")
@Tag(name = "Commande Client Controller", description = "Opérations liées à la gestion des commandes clients")
@RequiredArgsConstructor
public class CommandeClientController {

    private final CommandeClientService commandeClientService;

    @Operation(
            summary = "ADMIN: Créer une commande client",
            description = "Crée une commande pour un client donné dans une entreprise spécifique. Accessible uniquement aux ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Commande client créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou client/entreprise introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/client/{clientId}/entreprise/{entrepriseId}")
    public ResponseEntity<CommandeClientDTO> createCommandeClient(
            @PathVariable Long clientId,
            @PathVariable Long entrepriseId,
            @Valid @RequestBody CommandeClientDTO commandeClientDTO
    ) {
        commandeClientDTO.setClientId(clientId);
        commandeClientDTO.setEntrepriseId(entrepriseId);

        CommandeClientDTO createdCommande = commandeClientService.createCommandeClient(clientId, entrepriseId, commandeClientDTO);
        return new ResponseEntity<>(createdCommande, HttpStatus.CREATED);
    }

    @Operation(
            summary = "ADMIN: Mettre à jour l'état d'une commande",
            description = "Modifie l'état d'une commande client (ex: EN_PREPARATION → VALIDEE). Accessible uniquement aux ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    //@PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{idCommande}/etat/{etatCommande}")
    public ResponseEntity<CommandeClientDTO> updateEtatCommande(
            @PathVariable Long idCommande,
            @PathVariable EtatCommande etatCommande
    ) {
        CommandeClientDTO updatedCommande = commandeClientService.updateEtatCommande(idCommande, etatCommande);
        return ResponseEntity.ok(updatedCommande);
    }

    @Operation(
            summary = "MANAGER ou ADMIN: Lister toutes les commandes clients",
            description = "Retourne toutes les commandes clients enregistrées. Accessible aux MANAGER ou ADMIN."
    )
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/manager")
    public ResponseEntity<List<CommandeClientDTO>> findAll() {
        return ResponseEntity.ok(commandeClientService.findAll());
    }

    @Operation(
            summary = "MANAGER ou ADMIN: Rechercher une commande client par ID",
            description = "Retourne une commande client à partir de son identifiant. Accessible aux MANAGER ou ADMIN."
    )
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/manager/{id}")
    public ResponseEntity<CommandeClientDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeClientService.findById(id));
    }

    @Operation(
            summary = "MANAGER ou ADMIN: Lister les lignes d'une commande client",
            description = "Retourne toutes les lignes de commande associées à une commande client. Accessible aux MANAGER ou ADMIN."
    )
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/manager/{commandeId}/lignes")
    public ResponseEntity<List<LigneCommandeClientDTO>> findAllLignesCommandesClientByCommandeClientId(
            @PathVariable Long commandeId) {
        return ResponseEntity.ok(commandeClientService.findAllLignesCommandesClientByCommandeClientId(commandeId));
    }

    @Operation(
            summary = "ADMIN: Supprimer une commande client",
            description = "Supprime une commande client par son identifiant. Accessible uniquement aux ADMIN."
    )
    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<CommandeClientDTO> deleteCommandeClient(@PathVariable Long id) {
        return ResponseEntity.ok(commandeClientService.deleteCommandeClient(id));
    }
}
