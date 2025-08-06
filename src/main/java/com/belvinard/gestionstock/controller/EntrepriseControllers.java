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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/")
@Tag(name = "Entreprise-Controller", description = "API pour la gestion des entreprises")
@RequiredArgsConstructor
public class EntrepriseControllers {
    private final EntrepriseService entrepriseService;
    private final MinioService minioService;
    

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
    @PostMapping("/admin/create")
    public ResponseEntity<EntrepriseDTO> createEntreprise(@Valid @RequestBody EntrepriseDTO entrepriseDTO) {
        EntrepriseDTO savedEntrepriseDTO = entrepriseService.createEntreprise(entrepriseDTO);
        return new ResponseEntity<>(savedEntrepriseDTO, HttpStatus.CREATED);
    }

    // ==================== GET ALL ENTREPRIS
    @Operation(
            summary = "Retourne la liste des entreprise",
            description = """
       Retourne la liste des categories""
    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of entreprise successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntrepriseResponse.class),
                            examples = @ExampleObject(value = """
                        """)
                    )
            )
    })
    @GetMapping("/public/entreprises")
    public ResponseEntity<EntrepriseResponse> getAllEntreprises(){
        EntrepriseResponse entreprises = entrepriseService.
                getAllEntreprises();
        return new ResponseEntity<>(entreprises, HttpStatus.OK);
    }

    @Operation(summary = "Récupère une entreprise par son ID",
            description = "Cette méthode permet de récupérer une entreprise en fonction de son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entreprise trouvée avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("public/{entrepriseId}")
    public ResponseEntity<EntrepriseDTO> getEntrepriseById(
            @Parameter(description = "ID de l'entreprise à récupérer", required = true) @PathVariable Long id) {
        EntrepriseDTO entreprise = entrepriseService.findEntrepriseById(id);
        return ResponseEntity.ok(entreprise);
    }

    @Operation(summary = "Supprimer une entreprise par son ID",
            description = "Cette méthode permet de supprimer une entreprise en fonction de son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entreprise trouvée avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/admin/delete/{EntrepriseId}")
    public ResponseEntity<EntrepriseDTO> deleteEntrepriseById(@PathVariable Long id) {
        EntrepriseDTO deletedEntreprise = entrepriseService.deleteEntrepriseById(id);

        return ResponseEntity.ok(deletedEntreprise);
    }

    @Operation(summary = "Update entreprise image",
            description = "Uploads a new product image to MinIO and returns a URL to access it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product image updated successfully",
                    content = @Content(schema = @Schema(implementation = EntrepriseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid image file", content = @Content)
    })
    @PutMapping(value = "/entreprise/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EntrepriseDTO> updateEntrepriseImage(
            @Parameter(description = "ID of the entreprise") @PathVariable Long id,
            @Parameter(description = "Image file", required = true) @RequestParam("image") MultipartFile image) throws IOException {

        EntrepriseDTO updatedProduct = entrepriseService.updateEntrepriseImage(id, image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @Operation(summary = "Get presigned image URL",
            description = "Returns a presigned URL to access the entreprise image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL generated successfully"),
            @ApiResponse(responseCode = "404", description = "Entreprise not found"),
            @ApiResponse(responseCode = "400", description = "No image found for this entreprise")
    })
    @GetMapping("/public/{id}/image-url")
    public ResponseEntity<String> getPresignedImageUrl(
            @Parameter(description = "ID of the entreprise") @PathVariable Long id) {
        String presignedUrl = entrepriseService.getPresignedImageUrl(id);
        return ResponseEntity.ok(presignedUrl);
    }
}