package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.MvtStkDTO;
import com.belvinard.gestionstock.models.SourceMvtStk;
import com.belvinard.gestionstock.models.TypeMvtStk;
import com.belvinard.gestionstock.service.MvtStkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @PostMapping("/manager/entree")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Créer un mouvement d'entrée de stock (Manager uniquement)")
    public ResponseEntity<MvtStkDTO> entreeStock(
            @RequestParam Long articleId,
            @RequestParam BigDecimal quantite,
            @RequestParam SourceMvtStk source,
            @RequestParam Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.entreeStock(articleId, quantite, source, entrepriseId));
    }

    @PostMapping("/manager/sortie")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Créer un mouvement de sortie de stock (Manager uniquement)")
    public ResponseEntity<MvtStkDTO> sortieStock(
            @RequestParam Long articleId,
            @RequestParam BigDecimal quantite,
            @RequestParam SourceMvtStk source,
            @RequestParam Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.sortieStock(articleId, quantite, source, entrepriseId));
    }

    @PostMapping("/manager/correction")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Créer un mouvement de correction de stock (Manager uniquement)")
    public ResponseEntity<MvtStkDTO> correctionStock(
            @RequestParam Long articleId,
            @RequestParam BigDecimal quantite,
            @RequestParam TypeMvtStk typeMvt,
            @RequestParam Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.correctionStock(articleId, quantite, typeMvt, entrepriseId));
    }

    @GetMapping("/admin/article/{articleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer les mouvements de stock d'un article (Admin uniquement)")
    public ResponseEntity<List<MvtStkDTO>> findByArticleId(@PathVariable Long articleId) {
        return ResponseEntity.ok(mvtStkService.findByArticleId(articleId));
    }

    @GetMapping("/manager/entreprise/{entrepriseId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Récupérer les mouvements de stock d'une entreprise (Manager uniquement)")
    public ResponseEntity<List<MvtStkDTO>> findByEntrepriseId(@PathVariable Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.findByEntrepriseId(entrepriseId));
    }

    @GetMapping("/manager/type/{typeMvt}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Récupérer les mouvements par type (Manager uniquement)")
    public ResponseEntity<List<MvtStkDTO>> findByTypeMvt(@PathVariable TypeMvtStk typeMvt) {
        return ResponseEntity.ok(mvtStkService.findByTypeMvt(typeMvt));
    }

    @GetMapping("/manager/source/{sourceMvt}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Récupérer les mouvements par source (Manager uniquement)")
    public ResponseEntity<List<MvtStkDTO>> findBySourceMvt(@PathVariable SourceMvtStk sourceMvt) {
        return ResponseEntity.ok(mvtStkService.findBySourceMvt(sourceMvt));
    }

    @GetMapping("/manager/date-range")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Récupérer les mouvements dans une période (Manager uniquement)")
    public ResponseEntity<List<MvtStkDTO>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(mvtStkService.findByDateRange(startDate, endDate));
    }

    @GetMapping("/admin/stock-actuel/{articleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Calculer le stock actuel d'un article (Admin ou Manager)")
    public ResponseEntity<BigDecimal> calculateCurrentStock(@PathVariable Long articleId) {
        return ResponseEntity.ok(mvtStkService.calculateCurrentStock(articleId));
    }

    @GetMapping("/admin/historique/{articleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Récupérer l'historique des mouvements d'un article (Admin ou Manager)")
    public ResponseEntity<List<MvtStkDTO>> getStockHistory(@PathVariable Long articleId) {
        return ResponseEntity.ok(mvtStkService.getStockHistory(articleId));
    }

    @PostMapping("/manager/vente/{venteId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Créer les mouvements de stock pour une vente (Manager uniquement)")
    public ResponseEntity<Void> createMvtStkForVente(@PathVariable Long venteId) {
        mvtStkService.createMvtStkForVente(venteId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/manager/commande-fournisseur/{commandeId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Créer les mouvements de stock pour une commande fournisseur (Manager uniquement)")
    public ResponseEntity<Void> createMvtStkForCommandeFournisseur(@PathVariable Long commandeId) {
        mvtStkService.createMvtStkForCommandeFournisseur(commandeId);
        return ResponseEntity.ok().build();
    }
}

