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
@RequestMapping("/api/commande-clients")
@Tag(name = "Commande Client-Controller", description = "Opérations liées à la gestion des clients")
@RequiredArgsConstructor
public class CommandeClientController {

    private final CommandeClientService commandeClientService;

    @Operation(
            summary = "Créer une commande client",
            description = "Cette opération permet de créer une commande pour un client donné dans une entreprise spécifique."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Commande client créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou client/entreprise introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping("/commande-clients/client/{clientId}/entreprise/{entrepriseId}")
    public ResponseEntity<CommandeClientDTO> createCommandeClient(
            @PathVariable Long clientId,
            @PathVariable Long entrepriseId,
            @Valid @RequestBody CommandeClientDTO commandeClientDTO
    ) {
        // Injection manuelle des paramètres dans le DTO
        commandeClientDTO.setClientId(clientId);
        commandeClientDTO.setEntrepriseId(entrepriseId);

        CommandeClientDTO createdCommande = commandeClientService.createCommandeClient(clientId, entrepriseId, commandeClientDTO);
        return new ResponseEntity<>(createdCommande, HttpStatus.CREATED);
    }


    @PatchMapping("/{idCommande}/etat/{etatCommande}")
    @Operation(
            summary = "Mettre à jour l'état d'une commande client",
            description = "Cette opération permet de modifier l'état d'une commande (ex: EN_PREPARATION -> VALIDEE)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<CommandeClientDTO> updateEtatCommande(
            @PathVariable Long idCommande,
            @PathVariable EtatCommande etatCommande
    ) {
        CommandeClientDTO updatedCommande = commandeClientService.updateEtatCommande(idCommande, etatCommande);
        return ResponseEntity.ok(updatedCommande);
    }
    @GetMapping
    @Operation(summary = "Lister toutes les commandes clients", description = "Retourne toutes les commandes clients enregistrées")
    public ResponseEntity<List<CommandeClientDTO>> findAll() {
        return ResponseEntity.ok(commandeClientService.findAll());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Rechercher une commande client par ID", description = "Retourne une commande client à partir de son identifiant")
    public ResponseEntity<CommandeClientDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeClientService.findById(id));
    }


    @GetMapping("/{commandeId}/lignes")
    @Operation(summary = "Lister les lignes d'une commande client", description = "Retourne toutes les lignes de commande associées à une commande client")
    public ResponseEntity<List<LigneCommandeClientDTO>> findAllLignesCommandesClientByCommandeClientId(
            @PathVariable Long commandeId) {
        return ResponseEntity.ok(commandeClientService.findAllLignesCommandesClientByCommandeClientId(commandeId));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une commande client", description = "Supprime une commande client par son identifiant")
    public ResponseEntity<CommandeClientDTO> deleteCommandeClient(@PathVariable Long id) {
        return ResponseEntity.ok(commandeClientService.deleteCommandeClient(id));
    }


}
