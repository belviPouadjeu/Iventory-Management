package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.MvtStkDTO;
import com.belvinard.gestionstock.models.SourceMvtStk;
import com.belvinard.gestionstock.models.TypeMvtStk;
import com.belvinard.gestionstock.service.MvtStkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/mouvements-stock")
@RequiredArgsConstructor
@Tag(name = "MvtStk-Controller", description = "API de gestion des mouvements de stock")
public class MvtStkController {

    private final MvtStkService mvtStkService;

    @PostMapping("/entree")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Créer un mouvement d'entrée de stock (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<MvtStkDTO> entreeStock(
            @RequestBody com.belvinard.gestionstock.dto.EntreeStockRequest request,
            @RequestParam SourceMvtStk source) {
        return ResponseEntity.ok(mvtStkService.entreeStock(request.getArticleId(), request.getQuantite(), source, request.getEntrepriseId()));
    }

    @PostMapping("/sortie")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Créer un mouvement de sortie de stock (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<MvtStkDTO> sortieStock(
            @RequestParam Long articleId,
            @RequestParam BigDecimal quantite,
            @RequestParam SourceMvtStk source,
            @RequestParam Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.sortieStock(articleId, quantite, source, entrepriseId));
    }

    @PostMapping("/correction")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Créer un mouvement de correction de stock (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<MvtStkDTO> correctionStock(
            @RequestParam Long articleId,
            @RequestParam BigDecimal quantite,
            @RequestParam TypeMvtStk typeMvt,
            @RequestParam Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.correctionStock(articleId, quantite, typeMvt, entrepriseId));
    }

    @GetMapping("/article/{articleId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Récupérer les mouvements de stock d'un article (ADMIN uniquement)")
    public ResponseEntity<List<MvtStkDTO>> findByArticleId(@PathVariable Long articleId) {
        return ResponseEntity.ok(mvtStkService.findByArticleId(articleId));
    }

    @GetMapping("/entreprise/{entrepriseId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Récupérer les mouvements de stock d'une entreprise (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<List<MvtStkDTO>> findByEntrepriseId(@PathVariable Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.findByEntrepriseId(entrepriseId));
    }

    @GetMapping("/type/{typeMvt}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Récupérer les mouvements par type (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<List<MvtStkDTO>> findByTypeMvt(@PathVariable TypeMvtStk typeMvt) {
        return ResponseEntity.ok(mvtStkService.findByTypeMvt(typeMvt));
    }

    @GetMapping("/source/{sourceMvt}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Récupérer les mouvements par source (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<List<MvtStkDTO>> findBySourceMvt(@PathVariable SourceMvtStk sourceMvt) {
        return ResponseEntity.ok(mvtStkService.findBySourceMvt(sourceMvt));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Récupérer les mouvements dans une période (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<List<MvtStkDTO>> findByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate endDate) {
        return ResponseEntity.ok(mvtStkService.findByDateRange(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)));
    }

    @GetMapping("/stock-actuel/{articleId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Calculer le stock actuel d'un article (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<BigDecimal> calculateCurrentStock(@PathVariable Long articleId) {
        return ResponseEntity.ok(mvtStkService.calculateCurrentStock(articleId));
    }

    @GetMapping("/historique/{articleId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Récupérer l'historique des mouvements d'un article (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<List<MvtStkDTO>> getStockHistory(@PathVariable Long articleId) {
        return ResponseEntity.ok(mvtStkService.getStockHistory(articleId));
    }

    @PostMapping("/vente/{venteId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Créer les mouvements de stock pour une vente (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<Void> createMvtStkForVente(@PathVariable Long venteId) {
        mvtStkService.createMvtStkForVente(venteId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/commande-fournisseur/{commandeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER')")
    @Operation(summary = "Créer les mouvements de stock pour une commande fournisseur (ADMIN ou STOCK_MANAGER)")
    public ResponseEntity<Void> createMvtStkForCommandeFournisseur(@PathVariable Long commandeId) {
        mvtStkService.createMvtStkForCommandeFournisseur(commandeId);
        return ResponseEntity.ok().build();
    }
}
