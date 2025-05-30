package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.FournisseurDTO;
import com.belvinard.gestionstock.responses.ErrorResponse;
import com.belvinard.gestionstock.service.FournisseurService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
@Tag(name = "Fournisseurs", description = "Opérations de gestion des fournisseurs")
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



//    @GetMapping("/{id}")
//    @Operation(summary = "Rechercher un fournisseur par ID",
//            description = "Cette opération permet de rechercher un fournisseur à partir de son identifiant.")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Fournisseur trouvé"),
//        @ApiResponse(responseCode = "404", description = "Fournisseur non trouvé")
//    })
//    public ResponseEntity<FournisseurDTO> findById(@PathVariable Long id) {
//        return ResponseEntity.ok(fournisseurService.findById(id));
//    }
//
//
//    @GetMapping
//    @Operation(summary = "Lister tous les fournisseurs",
//            description = "Cette opération permet de récupérer la liste de tous les fournisseurs enregistrés.")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Liste des fournisseurs retournée avec succès")
//    })
//    public ResponseEntity<List<FournisseurDTO>> findAll() {
//
//        return ResponseEntity.ok(fournisseurService.findAll());
//    }
//
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Supprimer un fournisseur",
//            description = "Cette opération permet de supprimer un fournisseur à partir de son identifiant.")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Fournisseur supprimé avec succès"),
//        @ApiResponse(responseCode = "404", description = "Fournisseur non trouvé")
//    })
//    public ResponseEntity<FournisseurDTO> delete(@PathVariable Long id) {
//        return ResponseEntity.ok(fournisseurService.delete(id));
//    }




}
