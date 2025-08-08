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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
@Tag(name = "Catégories Controller", description = "API de gestion des catégories")
public class CategoryController {

        private final CategoryService categoryService;

        @Operation(summary = "PUBLIC: Rechercher une catégorie par désignation", description = "Accessible à tous sans authentification.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Catégories trouvées"),
                        @ApiResponse(responseCode = "404", description = "Aucune catégorie trouvée")
        })
        @GetMapping("/public/{designation}")
        public ResponseEntity<List<CategoryDTO>> getCategoryByDesignation(@PathVariable String designation) {
                List<CategoryDTO> categories = categoryService.findByDesignation(designation);
                return ResponseEntity.ok(categories);
        }

        @Operation(summary = "ADMIN: Créer une nouvelle catégorie", description = "Ajoute une catégorie à une entreprise existante. Accessible uniquement aux ADMIN.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Catégorie créée avec succès"),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
        })
        @PreAuthorize("hasAuthority('ROLE_ADMIN')")
        @PostMapping("/admin/category/{entrepriseId}")
        public ResponseEntity<CategoryDTO> createCategory(
                        @Parameter(description = "ID de l'entreprise") @PathVariable Long entrepriseId,
                        @Valid @RequestBody CategoryDTO categoryDTO) {

                CategoryDTO created = categoryService.addCategory(entrepriseId, categoryDTO);
                return new ResponseEntity<>(created, HttpStatus.CREATED);
        }

        @Operation(summary = "MANAGER ou ADMIN: Lister toutes les catégories avec leur entreprise", description = "Accessible aux rôles ADMIN ou MANAGER.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste retournée avec succès"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
        })
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @GetMapping("/manager/with-entreprise")
        public ResponseEntity<List<CategoryDTO>> getAllWithEntreprise() {
                List<CategoryDTO> categories = categoryService.getAllCategoriesWithEntreprise();
                return ResponseEntity.ok(categories);
        }

        @Operation(summary = "ADMIN: Supprimer une catégorie", description = "Supprime une catégorie à partir de son ID. Accessible uniquement aux ADMIN.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Catégorie supprimée"),
                        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
        })
        @PreAuthorize("hasAuthority('ROLE_ADMIN')")
        @DeleteMapping("/admin/category/{catergoryId}")
        public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long catergoryId) {
                CategoryDTO deleted = categoryService.delete(catergoryId);
                return ResponseEntity.ok(deleted);
        }

        @Operation(summary = "PUBLIC: Rechercher une catégorie par code", description = "Accessible à tous sans authentification.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Catégorie trouvée avec succès"),
                        @ApiResponse(responseCode = "404", description = "Catégorie non trouvée pour ce code")
        })
        @GetMapping("/public/code/{code}")
        public ResponseEntity<CategoryDTO> getCategoryByCode(@PathVariable String code) {
                CategoryDTO categoryDTO = categoryService.findByCode(code);
                return ResponseEntity.ok(categoryDTO);
        }
}
