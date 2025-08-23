//package com.belvinard.gestionstock.controller;
//
//import com.belvinard.gestionstock.dto.LigneVenteDTO;
//import com.belvinard.gestionstock.service.LigneVenteService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//@RestController
//@RequestMapping("${api.prefix}/lignes-vente")
//@RequiredArgsConstructor
//@Tag(name = "LigneVente-Controller", description = "API de gestion des lignes de vente")
//public class LigneVenteController {
//
//    private final LigneVenteService ligneVenteService;
//
//    @PostMapping("/vente/{venteId}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SALES_MANAGER')")
//    @Operation(summary = "Créer une ligne de vente (ADMIN ou SALES_MANAGER uniquement)")
//    public ResponseEntity<LigneVenteDTO> save(
//            @PathVariable Long venteId,
//            @Valid @RequestBody LigneVenteDTO dto) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ligneVenteService.save(dto, venteId));
//    }
//
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES_MANAGER', 'ROLE_SALES_REP')")
//    @Operation(summary = "Récupérer une ligne de vente par ID (ADMIN, SALES_MANAGER ou SALES_REP)")
//    public ResponseEntity<LigneVenteDTO> findById(@PathVariable Long id) {
//        return ResponseEntity.ok(ligneVenteService.findById(id));
//    }
//
//    @GetMapping("/vente/{venteId}")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES_MANAGER', 'ROLE_SALES_REP')")
//    @Operation(summary = "Récupérer toutes les lignes d'une vente (ADMIN, SALES_MANAGER ou SALES_REP)")
//    public ResponseEntity<List<LigneVenteDTO>> findAllByVenteId(@PathVariable Long venteId) {
//        return ResponseEntity.ok(ligneVenteService.findAllByVenteId(venteId));
//    }
//
//    @GetMapping("/article/{articleId}")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES_MANAGER', 'ROLE_SALES_REP')")
//    @Operation(summary = "Récupérer toutes les lignes d'un article (ADMIN, SALES_MANAGER ou SALES_REP)")
//    public ResponseEntity<List<LigneVenteDTO>> findAllByArticleId(@PathVariable Long articleId) {
//        return ResponseEntity.ok(ligneVenteService.findAllByArticleId(articleId));
//    }
//
//    @GetMapping
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES_MANAGER')")
//    @Operation(summary = "Récupérer toutes les lignes de vente (ADMIN ou SALES_MANAGER uniquement)")
//    public ResponseEntity<List<LigneVenteDTO>> findAll() {
//        return ResponseEntity.ok(ligneVenteService.findAll());
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SALES_MANAGER')")
//    @Operation(summary = "Supprimer une ligne de vente (ADMIN ou SALES_MANAGER uniquement)")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        ligneVenteService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/vente/{venteId}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SALES_MANAGER')")
//    @Operation(summary = "Supprimer toutes les lignes d'une vente (ADMIN ou SALES_MANAGER uniquement)")
//    public ResponseEntity<Void> deleteAllByVenteId(@PathVariable Long venteId) {
//        ligneVenteService.deleteAllByVenteId(venteId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PutMapping("/{id}/quantity")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SALES_MANAGER')")
//    @Operation(summary = "Modifier la quantité d'une ligne de vente (ADMIN ou SALES_MANAGER uniquement)")
//    public ResponseEntity<LigneVenteDTO> updateQuantity(
//            @PathVariable Long id,
//            @RequestParam BigDecimal quantity) {
//        return ResponseEntity.ok(ligneVenteService.updateQuantity(id, quantity));
//    }
//
//    @GetMapping("/vente/{venteId}/total")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES_MANAGER', 'ROLE_SALES_REP')")
//    @Operation(summary = "Calculer le total d'une vente (ADMIN, SALES_MANAGER ou SALES_REP)")
//    public ResponseEntity<BigDecimal> calculateTotalForVente(@PathVariable Long venteId) {
//        return ResponseEntity.ok(ligneVenteService.calculateTotalForVente(venteId));
//    }
//
//    @GetMapping("/check-stock/{articleId}")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES_MANAGER', 'ROLE_SALES_REP')")
//    @Operation(summary = "Vérifier le stock avant ajout (ADMIN, SALES_MANAGER ou SALES_REP)")
//    public ResponseEntity<Boolean> checkStock(
//            @PathVariable Long articleId,
//            @RequestParam BigDecimal quantite) {
//        return ResponseEntity.ok(ligneVenteService.checkStockBeforeAdd(articleId, quantite));
//    }
//}
