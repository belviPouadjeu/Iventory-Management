package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.LigneVenteDTO;
import com.belvinard.gestionstock.service.LigneVenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/lignes-vente")
@RequiredArgsConstructor
@Tag(name = "LigneVente-Controller", description = "API de gestion des lignes de vente")
public class LigneVenteController {

    private final LigneVenteService ligneVenteService;

    @PostMapping("/vente/{venteId}")
    @Operation(summary = "Créer une ligne de vente")
    public ResponseEntity<LigneVenteDTO> save(
            @PathVariable Long venteId,
            @Valid @RequestBody LigneVenteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ligneVenteService.save(dto, venteId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une ligne de vente par ID")
    public ResponseEntity<LigneVenteDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ligneVenteService.findById(id));
    }

    @GetMapping("/vente/{venteId}")
    @Operation(summary = "Récupérer toutes les lignes d'une vente")
    public ResponseEntity<List<LigneVenteDTO>> findAllByVenteId(@PathVariable Long venteId) {
        return ResponseEntity.ok(ligneVenteService.findAllByVenteId(venteId));
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "Récupérer toutes les lignes d'un article")
    public ResponseEntity<List<LigneVenteDTO>> findAllByArticleId(@PathVariable Long articleId) {
        return ResponseEntity.ok(ligneVenteService.findAllByArticleId(articleId));
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les lignes de vente")
    public ResponseEntity<List<LigneVenteDTO>> findAll() {
        return ResponseEntity.ok(ligneVenteService.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une ligne de vente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ligneVenteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/vente/{venteId}")
    @Operation(summary = "Supprimer toutes les lignes d'une vente")
    public ResponseEntity<Void> deleteAllByVenteId(@PathVariable Long venteId) {
        ligneVenteService.deleteAllByVenteId(venteId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/quantity")
    @Operation(summary = "Modifier la quantité d'une ligne de vente")
    public ResponseEntity<LigneVenteDTO> updateQuantity(
            @PathVariable Long id,
            @RequestParam BigDecimal quantity) {
        return ResponseEntity.ok(ligneVenteService.updateQuantity(id, quantity));
    }

    @GetMapping("/vente/{venteId}/total")
    @Operation(summary = "Calculer le total d'une vente")
    public ResponseEntity<BigDecimal> calculateTotalForVente(@PathVariable Long venteId) {
        return ResponseEntity.ok(ligneVenteService.calculateTotalForVente(venteId));
    }

    @GetMapping("/check-stock/{articleId}")
    @Operation(summary = "Vérifier le stock avant ajout")
    public ResponseEntity<Boolean> checkStock(
            @PathVariable Long articleId,
            @RequestParam BigDecimal quantite) {
        return ResponseEntity.ok(ligneVenteService.checkStockBeforeAdd(articleId, quantite));
    }
}