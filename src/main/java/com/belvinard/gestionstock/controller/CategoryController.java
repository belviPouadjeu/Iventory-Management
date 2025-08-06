package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.CategoryDTO;
import com.belvinard.gestionstock.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
@Tag(name = "Catégories-Controller", description = "API de gestion des catégories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/public/{designation}")
    @Operation(summary = "Rechercher une catégorie par désignation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catégories trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune catégorie trouvée")
    })
    public ResponseEntity<List<CategoryDTO>> getCategoryByDesignation(@PathVariable String designation) {
        List<CategoryDTO> categories = categoryService.findByDesignation(designation);
        return ResponseEntity.ok(categories);
    }


    @Operation(
        summary = "Créer une nouvelle catégorie",
        description = "Ajoute une nouvelle catégorie à une entreprise existante"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Catégorie créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    })
    @PostMapping("/admin/category/{entrepriseId}")
    public ResponseEntity<CategoryDTO> createCategory(
            @Parameter(description = "ID de l'entreprise à laquelle ajouter la catégorie") @PathVariable Long entrepriseId,
            @Parameter(description = "Les données de la catégorie à créer") @Valid @RequestBody CategoryDTO categoryDTO) {

        CategoryDTO created = categoryService.addCategory(entrepriseId, categoryDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    @Operation(
            summary = "Lister toutes les catégories avec leur entreprise",
            description = "Retourne la liste des catégories avec les noms des entreprises associées."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/with-entreprise")
    public ResponseEntity<List<CategoryDTO>> getAllWithEntreprise() {
        List<CategoryDTO> categories = categoryService.getAllCategoriesWithEntreprise();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Supprimer une catégorie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catégorie supprimée"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @DeleteMapping("/admin/category/{catergoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long catergoryId) {
        CategoryDTO deleted = categoryService.delete(catergoryId);
        return ResponseEntity.ok(deleted);
    }

    @Operation(summary = "Rechercher une catégorie par code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie trouvée avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée pour ce code")
    })
    @GetMapping("public/code/{code}")
    public ResponseEntity<CategoryDTO> getCategoryByCode(@PathVariable String code) {
        CategoryDTO categoryDTO = categoryService.findByCode(code);
        return ResponseEntity.ok(categoryDTO);
    }




}
