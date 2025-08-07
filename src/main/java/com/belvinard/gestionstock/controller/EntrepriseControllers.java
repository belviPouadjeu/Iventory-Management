package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.responses.EntrepriseResponse;
import com.belvinard.gestionstock.service.EntrepriseService;
import com.belvinard.gestionstock.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("${api.prefix}/entreprise")
@Tag(name = "Entreprise-Controller", description = "API pour la gestion des entreprises")
@RequiredArgsConstructor
public class EntrepriseControllers {

    private final EntrepriseService entrepriseService;
    private final MinioService minioService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Ajouter une entreprise",
            description = "Ajoute une nouvelle entreprise à la base de données"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entreprise créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntrepriseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    public ResponseEntity<EntrepriseDTO> createEntreprise(@Valid @RequestBody EntrepriseDTO entrepriseDTO) {
        EntrepriseDTO savedEntrepriseDTO = entrepriseService.createEntreprise(entrepriseDTO);
        return new ResponseEntity<>(savedEntrepriseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Retourne la liste des entreprises",
            description = "Retourne la liste complète des entreprises"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des entreprises récupérée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntrepriseResponse.class)))
    })
    public ResponseEntity<EntrepriseResponse> getAllEntreprises() {
        EntrepriseResponse entreprises = entrepriseService.getAllEntreprises();
        return ResponseEntity.ok(entreprises);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupère une entreprise par son ID",
            description = "Cette méthode permet de récupérer une entreprise en fonction de son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entreprise trouvée avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<EntrepriseDTO> getEntrepriseById(
            @Parameter(description = "ID de l'entreprise à récupérer", required = true) @PathVariable Long id) {
        EntrepriseDTO entreprise = entrepriseService.findEntrepriseById(id);
        return ResponseEntity.ok(entreprise);
    }

    @DeleteMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une entreprise par son ID",
            description = "Cette méthode permet de supprimer une entreprise en fonction de son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entreprise supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<EntrepriseDTO> deleteEntrepriseById(@PathVariable Long id) {
        EntrepriseDTO deletedEntreprise = entrepriseService.deleteEntrepriseById(id);
        return ResponseEntity.ok(deletedEntreprise);
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour l'image de l'entreprise",
            description = "Uploader une nouvelle image pour l'entreprise via MinIO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = EntrepriseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Fichier image invalide", content = @Content)
    })
    public ResponseEntity<EntrepriseDTO> updateEntrepriseImage(
            @Parameter(description = "ID de l'entreprise") @PathVariable Long id,
            @Parameter(description = "Fichier image", required = true) @RequestParam("image") MultipartFile image) throws IOException {
        EntrepriseDTO updatedEntreprise = entrepriseService.updateEntrepriseImage(id, image);
        return ResponseEntity.ok(updatedEntreprise);
    }

    @GetMapping("/{id}/image-url")
    @Operation(summary = "Obtenir l'URL pré-signée de l'image d'une entreprise",
            description = "Retourne une URL pré-signée pour accéder à l'image de l'entreprise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL pré-signée générée avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée"),
            @ApiResponse(responseCode = "400", description = "Aucune image trouvée pour cette entreprise")
    })
    public ResponseEntity<String> getPresignedImageUrl(
            @Parameter(description = "ID de l'entreprise") @PathVariable Long id) {
        String presignedUrl = entrepriseService.getPresignedImageUrl(id);
        return ResponseEntity.ok(presignedUrl);
    }
}
