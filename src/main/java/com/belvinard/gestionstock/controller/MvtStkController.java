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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mouvements-stock")
@RequiredArgsConstructor
@Tag(name = "MvtStk-Controller", description = "API de gestion des mouvements de stock")
public class MvtStkController {

    private final MvtStkService mvtStkService;

    @PostMapping("/entree")
    @Operation(summary = "Créer un mouvement d'entrée de stock")
    public ResponseEntity<MvtStkDTO> entreeStock(
            @Parameter(description = "ID de l'article") @RequestParam Long articleId,
            @Parameter(description = "Quantité à ajouter") @RequestParam BigDecimal quantite,
            @Parameter(description = "Source du mouvement") @RequestParam SourceMvtStk source,
            @Parameter(description = "ID de l'entreprise") @RequestParam Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.entreeStock(articleId, quantite, source, entrepriseId));
    }

    @PostMapping("/sortie")
    @Operation(summary = "Créer un mouvement de sortie de stock")
    public ResponseEntity<MvtStkDTO> sortieStock(
            @Parameter(description = "ID de l'article") @RequestParam Long articleId,
            @Parameter(description = "Quantité à retirer") @RequestParam BigDecimal quantite,
            @Parameter(description = "Source du mouvement") @RequestParam SourceMvtStk source,
            @Parameter(description = "ID de l'entreprise") @RequestParam Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.sortieStock(articleId, quantite, source, entrepriseId));
    }

    @PostMapping("/correction")
    @Operation(summary = "Créer un mouvement de correction de stock")
    public ResponseEntity<MvtStkDTO> correctionStock(
            @Parameter(description = "ID de l'article") @RequestParam Long articleId,
            @Parameter(description = "Quantité de correction") @RequestParam BigDecimal quantite,
            @Parameter(description = "Type de mouvement") @RequestParam TypeMvtStk typeMvt,
            @Parameter(description = "ID de l'entreprise") @RequestParam Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.correctionStock(articleId, quantite, typeMvt, entrepriseId));
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Récupérer les mouvements de stock d'un article")
    public ResponseEntity<List<MvtStkDTO>> findByArticleId(
            @Parameter(description = "ID de l'article") @PathVariable Long articleId) {
        return ResponseEntity.ok(mvtStkService.findByArticleId(articleId));
    }

    @GetMapping("/entreprise/{entrepriseId}")
    @Operation(summary = "Récupérer les mouvements de stock d'une entreprise")
    public ResponseEntity<List<MvtStkDTO>> findByEntrepriseId(
            @Parameter(description = "ID de l'entreprise") @PathVariable Long entrepriseId) {
        return ResponseEntity.ok(mvtStkService.findByEntrepriseId(entrepriseId));
    }

    @GetMapping("/type/{typeMvt}")
    @Operation(summary = "Récupérer les mouvements par type")
    public ResponseEntity<List<MvtStkDTO>> findByTypeMvt(
            @Parameter(description = "Type de mouvement") @PathVariable TypeMvtStk typeMvt) {
        return ResponseEntity.ok(mvtStkService.findByTypeMvt(typeMvt));
    }

    @GetMapping("/source/{sourceMvt}")
    @Operation(summary = "Récupérer les mouvements par source")
    public ResponseEntity<List<MvtStkDTO>> findBySourceMvt(
            @Parameter(description = "Source du mouvement") @PathVariable SourceMvtStk sourceMvt) {
        return ResponseEntity.ok(mvtStkService.findBySourceMvt(sourceMvt));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Récupérer les mouvements dans une période")
    public ResponseEntity<List<MvtStkDTO>> findByDateRange(
            @Parameter(description = "Date de début") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Date de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(mvtStkService.findByDateRange(startDate, endDate));
    }

    @GetMapping("/stock-actuel/{articleId}")
    @Operation(summary = "Calculer le stock actuel d'un article")
    public ResponseEntity<BigDecimal> calculateCurrentStock(
            @Parameter(description = "ID de l'article") @PathVariable Long articleId) {
        return ResponseEntity.ok(mvtStkService.calculateCurrentStock(articleId));
    }

    @GetMapping("/historique/{articleId}")
    @Operation(summary = "Récupérer l'historique des mouvements d'un article")
    public ResponseEntity<List<MvtStkDTO>> getStockHistory(
            @Parameter(description = "ID de l'article") @PathVariable Long articleId) {
        return ResponseEntity.ok(mvtStkService.getStockHistory(articleId));
    }

    @PostMapping("/vente/{venteId}")
    @Operation(summary = "Créer les mouvements de stock pour une vente")
    public ResponseEntity<Void> createMvtStkForVente(
            @Parameter(description = "ID de la vente") @PathVariable Long venteId) {
        mvtStkService.createMvtStkForVente(venteId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/commande-fournisseur/{commandeId}")
    @Operation(summary = "Créer les mouvements de stock pour une commande fournisseur")
    public ResponseEntity<Void> createMvtStkForCommandeFournisseur(
            @Parameter(description = "ID de la commande") @PathVariable Long commandeId) {
        mvtStkService.createMvtStkForCommandeFournisseur(commandeId);
        return ResponseEntity.ok().build();
    }
}