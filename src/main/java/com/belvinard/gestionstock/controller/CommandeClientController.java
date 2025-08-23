package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.CommandeClientDTO;
import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.models.EtatCommande;
import com.belvinard.gestionstock.service.CommandeClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/commande-clients")
@Tag(name = "Commande Client Controller", description = "Opérations liées à la gestion des commandes clients")
@RequiredArgsConstructor
public class CommandeClientController {

        private final CommandeClientService commandeClientService;

        @Operation(summary = "ADMIN, ROLE_SALES_MANAGER ou MANAGERS: Créer une commande client", description = "Crée une commande pour un client donné. L'ID de l'entreprise doit être fourni dans le JSON. Accessible aux ADMIN et MANAGERS.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Données de la commande client avec l'ID de l'entreprise", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Exemple commande client", summary = "Commande client avec entreprise dans le JSON", value = """
                        {
                          "entrepriseId": 1,
                          "dateCommande": "2026-02-10",
                          "etatCommande": "EN_PREPARATION",
                          "commentaire": "Commande urgente - dateCommande est la date de livraison souhaitée"
                        }
                        """))))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Commande client créée avec succès"),
                        @ApiResponse(responseCode = "400", description = "Données invalides, client/entreprise introuvable ou entrepriseId manquant"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @PostMapping("create/client/{clientId}")
        public ResponseEntity<CommandeClientDTO> createCommandeClient(
                        @PathVariable Long clientId,
                        @Valid @RequestBody CommandeClientDTO commandeClientDTO) {

                // Validation que l'entrepriseId est fourni dans le JSON
                if (commandeClientDTO.getEntrepriseId() == null) {
                        throw new IllegalArgumentException("L'ID de l'entreprise doit être fourni dans le JSON");
                }

                commandeClientDTO.setClientId(clientId);

                CommandeClientDTO createdCommande = commandeClientService.createCommandeClient(clientId,
                                commandeClientDTO);
                return new ResponseEntity<>(createdCommande, HttpStatus.CREATED);
        }

        @Operation(summary = "ADMIN: Mettre à jour l'état d'une commande", description = "Modifie l'état d'une commande client (ex: EN_PREPARATION → VALIDEE). Accessible uniquement aux ADMIN.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Commande mise à jour avec succès"),
                        @ApiResponse(responseCode = "404", description = "Commande non trouvée")
        })
        @PreAuthorize("hasAuthority('ROLE_ADMIN')")
        @PatchMapping("/{idCommande}/etat/{etatCommande}")
        public ResponseEntity<CommandeClientDTO> updateEtatCommande(
                        @PathVariable Long idCommande,
                        @PathVariable EtatCommande etatCommande) {
                CommandeClientDTO updatedCommande = commandeClientService.updateEtatCommande(idCommande, etatCommande);
                return ResponseEntity.ok(updatedCommande);
        }

        @Operation(summary = "MANAGER ou ADMIN: Lister toutes les commandes clients", description = "Retourne toutes les commandes clients enregistrées. Accessible aux MANAGER ou ADMIN.")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @GetMapping
        public ResponseEntity<List<CommandeClientDTO>> findAll() {
                return ResponseEntity.ok(commandeClientService.findAll());
        }

        @Operation(summary = "MANAGER ou ADMIN: Rechercher une commande client par ID", description = "Retourne une commande client à partir de son identifiant. Accessible aux MANAGER ou ADMIN.")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @GetMapping("/{id}")
        public ResponseEntity<CommandeClientDTO> findById(@PathVariable Long id) {
                return ResponseEntity.ok(commandeClientService.findById(id));
        }

        @Operation(summary = "MANAGER ou ADMIN: Lister les lignes d'une commande client", description = "Retourne toutes les lignes de commande associées à une commande client. Accessible aux MANAGER ou ADMIN.")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @GetMapping("/{commandeId}/lignes")
        public ResponseEntity<List<LigneCommandeClientDTO>> findAllLignesCommandesClientByCommandeClientId(
                        @PathVariable Long commandeId) {
                return ResponseEntity
                                .ok(commandeClientService.findAllLignesCommandesClientByCommandeClientId(commandeId));
        }

        @Operation(summary = "ADMIN: Supprimer une commande client", description = "Supprime une commande client par son identifiant et remet automatiquement en stock les articles. "
                        +
                        "⚠️ Seules les commandes EN_PREPARATION ou ANNULEE peuvent être supprimées. " +
                        "Les commandes VALIDEE ou LIVREE ne peuvent pas être supprimées. " +
                        "Accessible uniquement aux ADMIN.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Commande supprimée avec succès"),
                        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
                        @ApiResponse(responseCode = "400", description = "Commande validée ou livrée, suppression interdite")
        })
        @PreAuthorize("hasAuthority('ROLE_ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<CommandeClientDTO> deleteCommandeClient(@PathVariable Long id) {
                return ResponseEntity.ok(commandeClientService.deleteCommandeClient(id));
        }

        @Operation(summary = "ADMIN, ROLE_SALES_MANAGER ou MANAGERS: Annuler une commande client", description = "Annule une commande client et remet automatiquement en stock les articles des lignes de commande. "
                        +
                        "⚠️ Seules les commandes en état EN_PREPARATION peuvent être annulées. " +
                        "Les commandes VALIDEE, LIVREE ou déjà ANNULEE ne peuvent pas être annulées. " +
                        "Accessible aux ADMIN et MANAGERS.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Commande annulée avec succès"),
                        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
                        @ApiResponse(responseCode = "400", description = "Commande déjà validée, livrée ou déjà annulée")
        })
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @PutMapping("/{commandeId}/annuler")
        public ResponseEntity<CommandeClientDTO> annulerCommande(
                        @Parameter(description = "ID de la commande à annuler", required = true) @PathVariable Long commandeId) {

                CommandeClientDTO commandeAnnulee = commandeClientService.annulerCommande(commandeId);
                return ResponseEntity.ok(commandeAnnulee);
        }
}
