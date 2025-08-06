package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.FournisseurDTO;
import com.belvinard.gestionstock.responses.ErrorResponse;
import com.belvinard.gestionstock.service.FournisseurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("${api.prefix}/fournisseurs")
@RequiredArgsConstructor
@Tag(name = "Fournisseurs-Controller", description = "Opérations de gestion des fournisseurs")
public class FournisseurController {

    private final FournisseurService fournisseurService;


    @PostMapping("/entreprise/{entrepriseId}")
    @Operation(summary = "Créer un fournisseur", description = "Ajoute un nouveau fournisseur lié à une entreprise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fournisseur créé avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<FournisseurDTO> createFournisseur(
            @PathVariable Long entrepriseId,
            @Valid @RequestBody FournisseurDTO fournisseurDTO) {
        FournisseurDTO created = fournisseurService.createFournisseur(entrepriseId, fournisseurDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Récupère la liste de tous les fournisseurs
     *
     * @return Liste des fournisseurs avec leurs informations d'entreprise
     */
    @GetMapping
    @Operation(
            summary = "Récupérer tous les fournisseurs",
            description = "Récupère la liste complète des fournisseurs avec leurs informations d'entreprise",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Liste des fournisseurs récupérée avec succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FournisseurDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Aucun fournisseur trouvé",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<List<FournisseurDTO>> getAllFournisseurs() {
        List<FournisseurDTO> fournisseurs = fournisseurService.getAllFournisseur();
        return ResponseEntity.ok(fournisseurs);
    }

    @GetMapping("/{fournisseurId}")
    @Operation(summary = "Rechercher un fournisseur par ID",
            description = "Cette opération permet de rechercher un fournisseur à partir de son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fournisseur trouvé"),
            @ApiResponse(responseCode = "404", description = "Fournisseur non trouvé")
    })
    public ResponseEntity<FournisseurDTO> findById(
            @Parameter(description = "ID du fournisseur à récupérer", required = true)
            @PathVariable Long fournisseurId) {
        return ResponseEntity.ok(fournisseurService.findFournisseurById(fournisseurId));
    }

    @DeleteMapping("/{fournisseurId}")
    @Operation(summary = "Supprimer un fournisseur",
            description = "Cette opération permet de supprimer un fournisseur à partir de son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fournisseur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Fournisseur non trouvé")
    })
    public ResponseEntity<FournisseurDTO> deleteFourniseur(@PathVariable Long id) {
        return ResponseEntity.ok(fournisseurService.deleteFournisseur(id));
    }

    /**
     * Met à jour un fournisseur existant
     *
     * @param fournisseurId L'identifiant du fournisseur à mettre à jour
     * @param fournisseurDTO Les nouvelles données du fournisseur
     * @return Le fournisseur mis à jour
     */
    @PutMapping("/{fournisseurId}")
    @Operation(
            summary = "Mettre à jour un fournisseur",
            description = "Met à jour les informations d'un fournisseur existant",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Fournisseur mis à jour avec succès",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FournisseurDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Fournisseur non trouvé",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Données invalides ou fournisseur en double",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<FournisseurDTO> updateFournisseur(
            @PathVariable Long fournisseurId,
            @Valid @RequestBody FournisseurDTO fournisseurDTO) {
        FournisseurDTO updatedFournisseur = fournisseurService.updateFournisseur(fournisseurId, fournisseurDTO);
        return ResponseEntity.ok(updatedFournisseur);
    }


}
