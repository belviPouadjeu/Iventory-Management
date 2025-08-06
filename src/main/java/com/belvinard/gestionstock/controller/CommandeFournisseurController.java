package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.CommandeFournisseurDTO;
import com.belvinard.gestionstock.models.EtatCommande;
import com.belvinard.gestionstock.responses.ErrorResponse;
import com.belvinard.gestionstock.service.CommandeFournisseurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/commandes-fournisseurs")
@Tag(name = "Commande Fournisseur-Controller", description = "API de gestion des commandes fournisseurs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommandeFournisseurController {

    private final CommandeFournisseurService commandeFournisseurService;


    /**
     * Crée une nouvelle commande fournisseur
     */
    @PostMapping("/fournisseur/{fournisseurId}")
    @Operation(
        summary = "Créer une nouvelle commande fournisseur",
        description = "Crée une nouvelle commande pour un fournisseur spécifique",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Commande créée avec succès",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CommandeFournisseurDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Données invalides ou code de commande en double",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Fournisseur non trouvé",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<CommandeFournisseurDTO> createCommandeFournisseur(
            @Parameter(description = "ID du fournisseur", required = true)
            @PathVariable Long fournisseurId,
            
            @Parameter(description = "Données de la commande fournisseur", required = true)
            @Valid @RequestBody CommandeFournisseurDTO commandeFournisseurDTO) {
        
        CommandeFournisseurDTO savedCommande = commandeFournisseurService
            .saveCommandFournisseur(commandeFournisseurDTO, fournisseurId);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(savedCommande);
    }

    /**
     * Récupère une commande fournisseur par son ID
     */
    @GetMapping("/{fournisseurId}")
    @Operation(
        summary = "Rechercher une commande fournisseur par ID",
        description = "Retourne une commande fournisseur en fonction de son ID",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Commande trouvée",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CommandeFournisseurDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Commande non trouvée",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<CommandeFournisseurDTO> getCommandeFournisseurById(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long fournisseurId) {
        
        CommandeFournisseurDTO commande = commandeFournisseurService.findById(fournisseurId);
        return ResponseEntity.ok(commande);
    }

    /**
     * Récupère toutes les commandes fournisseurs
     */
    @GetMapping
    @Operation(
        summary = "Lister toutes les commandes fournisseurs",
        description = "Retourne la liste de toutes les commandes fournisseurs",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Liste des commandes récupérée avec succès",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = CommandeFournisseurDTO.class))
                )
            )
        }
    )
    public ResponseEntity<List<CommandeFournisseurDTO>> getAllCommandesFournisseurs() {
        List<CommandeFournisseurDTO> commandes = commandeFournisseurService.findAll();
        return ResponseEntity.ok(commandes);
    }

    /**
     * Supprime une commande fournisseur
     */
    @DeleteMapping("/{fournisseurId}")
    @Operation(
        summary = "Supprimer une commande fournisseur",
        description = "Supprime une commande fournisseur en fonction de son ID",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Commande supprimée avec succès"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Commande non trouvée",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<CommandeFournisseurDTO> deleteCommandeFournisseur(
            @Parameter(description = "ID de la commande à supprimer", required = true)
            @PathVariable Long fournisseurId) {
        
        CommandeFournisseurDTO deletedCommande = commandeFournisseurService.delete(fournisseurId);
        return new ResponseEntity<>(deletedCommande, HttpStatus.OK);
    }

    @PutMapping("/{idCommande}/etat")
    @Operation(
            summary = "Mettre à jour l'état d'une commande fournisseur",
            description = "Met à jour l'état d'une commande fournisseur existante",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "État de la commande mis à jour avec succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommandeFournisseurDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Commande non trouvée",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "État invalide ou modification non autorisée",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<CommandeFournisseurDTO> updateEtatCommande(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable("idCommande") Long idCommande,

            @Parameter(description = "Nouvel état de la commande", required = true)
            @RequestParam EtatCommande nouvelEtat) {

        CommandeFournisseurDTO updatedCommande = commandeFournisseurService.updateEtatCommande(idCommande, nouvelEtat);
        return ResponseEntity.ok(updatedCommande);
    }


    @GetMapping("/code/{code}")
    @Operation(
            summary = "Rechercher une commande fournisseur par code",
            description = "Cette méthode permet de rechercher une commande fournisseur par son code unique",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Commande fournisseur trouvée",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommandeFournisseurDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Commande fournisseur non trouvée",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Code invalide",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<CommandeFournisseurDTO> findByCode(
            @Parameter(description = "Code de la commande fournisseur", required = true)
            @PathVariable("code") String code) {
        return ResponseEntity.ok(commandeFournisseurService.findByCode(code));
    }

}

