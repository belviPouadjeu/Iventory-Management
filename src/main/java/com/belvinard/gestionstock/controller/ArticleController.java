package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.ArticleDTO;
import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.models.Article;
import com.belvinard.gestionstock.service.ArticleService;
import com.belvinard.gestionstock.service.LigneCommandeClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
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
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/articles")
@RequiredArgsConstructor
@Tag(name = "Articles-Controller", description = "API de gestion des articles")
@SecurityRequirement(name = "bearerAuth")
public class ArticleController {

    private final ArticleService articleService;
    private final LigneCommandeClientService ligneCommandeClientService;

    /* ================== CREATE ARTICLE ================== */
    @Operation(summary = "ADMIN: Créer un nouvel article")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Article créé avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise ou catégorie non trouvée")
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<ArticleDTO> createArticle(
            @RequestParam Long entrepriseId,
            @Valid @RequestBody ArticleDTO articleDTO) {

        ArticleDTO created = articleService.createArticle(entrepriseId, articleDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /* ================== GET ALL ARTICLES ================== */
    @Operation(summary = "USER, MANAGER ou ADMIN: Récupérer tous les articles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
    @GetMapping("/all")
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<ArticleDTO> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    /* ================== GET ARTICLE BY ID ================== */
    @Operation(summary = "USER, MANAGER ou ADMIN: Récupérer un article par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article trouvé"),
            @ApiResponse(responseCode = "404", description = "Article non trouvé")
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        ArticleDTO articleDTO = articleService.findAllByArticleId(id);
        return ResponseEntity.ok(articleDTO);
    }

    /* ================== DELETE ARTICLE ================== */
    @Operation(summary = "ADMIN: Supprimer un article par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article supprimé"),
            @ApiResponse(responseCode = "404", description = "Article non trouvé")
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ArticleDTO> deleteArticle(@PathVariable Long id) {
        ArticleDTO deleted = articleService.deleteArticle(id);
        return ResponseEntity.ok(deleted);
    }

    /* ================== GET BY CODE ================== */
    @Operation(summary = "USER, MANAGER ou ADMIN: Rechercher un article par code")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
    @GetMapping("/manager/code/{codeArticle}")
    public ResponseEntity<ArticleDTO> findByCodeArticle(@PathVariable String codeArticle) {
        ArticleDTO articleDTO = articleService.findByCodeArticle(codeArticle);
        return ResponseEntity.ok(articleDTO);
    }

    /* ================== GET BY CATEGORY ================== */
    @Operation(summary = "USER, MANAGER ou ADMIN: Lister les articles d'une catégorie")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
    @GetMapping("/category/{idCategory}")
    public ResponseEntity<List<ArticleDTO>> findAllByCategory(@PathVariable Long idCategory) {
        List<ArticleDTO> articles = articleService.findAllArticleByIdCategory(idCategory);
        return ResponseEntity.ok(articles);
    }

    /* ================== HISTORIQUE DES COMMANDES ================== */
    @Operation(summary = "MANAGER ou ADMIN: Historique des commandes d’un article")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @GetMapping("/historique/article/{idArticle}")
    public ResponseEntity<List<LigneCommandeClientDTO>> findHistoriqueCommandeClient(@PathVariable Long idArticle) {
        List<LigneCommandeClientDTO> lignes = ligneCommandeClientService.findHistoriqueCommandeClient(idArticle);
        return ResponseEntity.ok(lignes);
    }

    /* ================== UPDATE IMAGE ================== */
    @Operation(summary = "ADMIN ou MANAGER: Modifier l’image d’un article")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArticleDTO> updateArticleImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) throws IOException {

        ArticleDTO updatedArticle = articleService.updateArticleImage(id, image);
        return new ResponseEntity<>(updatedArticle, HttpStatus.OK);
    }

    /* ================== GET IMAGE PRESIGNED URL ================== */
    @Operation(summary = "PUBLIC: Obtenir le lien temporaire de l’image d’un article")
    @GetMapping("/{id}/image-url")
    public ResponseEntity<String> getPresignedArticleImageUrl(@PathVariable Long id) {
        String presignedUrl = articleService.getPresignedImageUrl(id);
        return ResponseEntity.ok(presignedUrl);
    }

}
