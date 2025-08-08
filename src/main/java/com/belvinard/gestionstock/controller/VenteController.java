package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.LigneVenteDTO;
import com.belvinard.gestionstock.dto.VenteDTO;
import com.belvinard.gestionstock.models.EtatVente;
import com.belvinard.gestionstock.service.VenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/ventes")
@RequiredArgsConstructor
@Tag(name = "Vente-Controller", description = "API de gestion des ventes")
public class VenteController {

    private final VenteService venteService;

    // === ADMIN ou SALES ===

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    @PostMapping("/sales/entreprise/{entrepriseId}")
    @Operation(summary = "[SALES/ADMIN] Créer une vente pour une entreprise")
    public ResponseEntity<VenteDTO> createVente(
            @PathVariable Long entrepriseId,
            @Valid @RequestBody VenteDTO venteDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(venteService.createVente(entrepriseId, venteDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    @PutMapping("/sales/{id}")
    @Operation(summary = "[SALES/ADMIN] Modifier une vente")
    public ResponseEntity<VenteDTO> updateVente(
            @PathVariable Long id,
            @Valid @RequestBody VenteDTO venteDTO) {
        return ResponseEntity.ok(venteService.updateVente(id, venteDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    @PutMapping("/sales/{id}/finalize")
    @Operation(summary = "[SALES/ADMIN] Finaliser une vente")
    public ResponseEntity<VenteDTO> finalizeVente(@PathVariable Long id) {
        return ResponseEntity.ok(venteService.finalizeVente(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    @PostMapping("/sales/{venteId}/lignes")
    @Operation(summary = "[SALES/ADMIN] Ajouter une ligne de vente")
    public ResponseEntity<LigneVenteDTO> addLigneVente(
            @PathVariable Long venteId,
            @Valid @RequestBody LigneVenteDTO ligneVenteDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(venteService.addLigneVente(venteId, ligneVenteDTO));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/admin/{id}")
    @Operation(summary = "[ADMIN] Supprimer une vente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // === ACCESSIBLE AUX ADMIN/SALES/MANAGERS POUR CONSULTATION ===

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES_REP', 'ROLE_SALES_MANAGER')")
    @GetMapping("/{id}")
    @Operation(summary = "[ 'ADMIN', 'SALES_REP', 'SALES_MANAGER'] Récupérer une vente par ID")
    public ResponseEntity<VenteDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(venteService.findById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES_REP', 'ROLE_SALES_MANAGER')")
    @GetMapping("/code/{code}")
    @Operation(summary = "[ 'ADMIN', 'SALES_REP', 'SALES_MANAGER'] Récupérer une vente par code")
    public ResponseEntity<VenteDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(venteService.findByCode(code));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES_REP', 'ROLE_SALES_MANAGER')")
    @GetMapping("/all")
    @Operation(summary = "[ 'ADMIN', 'SALES_REP', 'SALES_MANAGER'] Récupérer toutes les ventes")
    public ResponseEntity<List<VenteDTO>> findAll() {
        return ResponseEntity.ok(venteService.findAll());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES_REP', 'ROLE_SALES_MANAGER')")
    @GetMapping("/entreprise/{entrepriseId}")
    @Operation(summary = "[ 'ADMIN', 'SALES_REP', 'SALES_MANAGER'] Récupérer les ventes par entreprise")
    public ResponseEntity<List<VenteDTO>> findByEntreprise(@PathVariable Long entrepriseId) {
        return ResponseEntity.ok(venteService.findAllByEntreprise(entrepriseId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES', 'ROLE_MANAGER')")
    @GetMapping("/etat/{etatVente}")
    @Operation(summary = "['ADMIN', 'SALES', 'MANAGER'] Récupérer les ventes par état")
    public ResponseEntity<List<VenteDTO>> findByEtatVente(@PathVariable EtatVente etatVente) {
        return ResponseEntity.ok(venteService.findByEtatVente(etatVente));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES', 'ROLE_MANAGER')")
    @GetMapping("/entreprise/{entrepriseId}/etat/{etatVente}")
    @Operation(summary = "['ADMIN', 'SALES', 'MANAGER'] Récupérer les ventes par entreprise et état")
    public ResponseEntity<List<VenteDTO>> findByEntrepriseAndEtatVente(
            @PathVariable Long entrepriseId,
            @PathVariable EtatVente etatVente) {
        return ResponseEntity.ok(venteService.findByEntrepriseAndEtatVente(entrepriseId, etatVente));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES', 'ROLE_MANAGER')")
    @GetMapping("/date-range")
    @Operation(summary = "['ADMIN', 'SALES', 'MANAGER'] Récupérer les ventes par période")
    public ResponseEntity<List<VenteDTO>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(venteService.findByDateRange(startDate, endDate));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES', 'ROLE_MANAGER')")
    @GetMapping("/entreprise/{entrepriseId}/date-range")
    @Operation(summary = "[ADMIN,SALES,MANAGER] Récupérer les ventes par entreprise et période")
    public ResponseEntity<List<VenteDTO>> findByEntrepriseAndDateRange(
            @PathVariable Long entrepriseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(venteService.findByEntrepriseAndDateRange(entrepriseId, startDate, endDate));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES', 'ROLE_MANAGER')")
    @GetMapping("/{venteId}/lignes")
    @Operation(summary = "['ADMIN', 'SALES', 'MANAGER'] Récupérer les lignes de vente")
    public ResponseEntity<List<LigneVenteDTO>> findLignesVente(@PathVariable Long venteId) {
        return ResponseEntity.ok(venteService.findAllLignesVenteByVenteId(venteId));
    }
}
