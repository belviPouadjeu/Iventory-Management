package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.LigneVenteDTO;
import com.belvinard.gestionstock.dto.VenteDTO;
import com.belvinard.gestionstock.enums.EtatVente;
import com.belvinard.gestionstock.service.VenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ventes")
@RequiredArgsConstructor
@Tag(name = "Vente", description = "API de gestion des ventes")
public class VenteController {

    private final VenteService venteService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle vente")
    public ResponseEntity<VenteDTO> save(@Valid @RequestBody VenteDTO venteDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venteService.save(venteDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une vente par ID")
    public ResponseEntity<VenteDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(venteService.findById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Récupérer une vente par code")
    public ResponseEntity<VenteDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(venteService.findByCode(code));
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les ventes")
    public ResponseEntity<List<VenteDTO>> findAll() {
        return ResponseEntity.ok(venteService.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une vente")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/entreprise/{entrepriseId}")
    @Operation(summary = "Créer une vente pour une entreprise")
    public ResponseEntity<VenteDTO> createVente(
            @PathVariable Long entrepriseId,
            @Valid @RequestBody VenteDTO venteDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(venteService.createVente(entrepriseId, venteDTO));
    }

    @PutMapping("/{id}/finalize")
    @Operation(summary = "Finaliser une vente")
    public ResponseEntity<VenteDTO> finalizeVente(@PathVariable Long id) {
        return ResponseEntity.ok(venteService.finalizeVente(id));
    }

    @GetMapping("/entreprise/{entrepriseId}")
    @Operation(summary = "Récupérer les ventes par entreprise")
    public ResponseEntity<List<VenteDTO>> findByEntreprise(@PathVariable Long entrepriseId) {
        return ResponseEntity.ok(venteService.findAllByEntreprise(entrepriseId));
    }

    @GetMapping("/etat/{etatVente}")
    @Operation(summary = "Récupérer les ventes par état")
    public ResponseEntity<List<VenteDTO>> findByEtatVente(@PathVariable EtatVente etatVente) {
        return ResponseEntity.ok(venteService.findByEtatVente(etatVente));
    }

    @GetMapping("/entreprise/{entrepriseId}/etat/{etatVente}")
    @Operation(summary = "Récupérer les ventes par entreprise et état")
    public ResponseEntity<List<VenteDTO>> findByEntrepriseAndEtatVente(
            @PathVariable Long entrepriseId,
            @PathVariable EtatVente etatVente) {
        return ResponseEntity.ok(venteService.findByEntrepriseAndEtatVente(entrepriseId, etatVente));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Récupérer les ventes par période")
    public ResponseEntity<List<VenteDTO>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(venteService.findByDateRange(startDate, endDate));
    }

    @GetMapping("/entreprise/{entrepriseId}/date-range")
    @Operation(summary = "Récupérer les ventes par entreprise et période")
    public ResponseEntity<List<VenteDTO>> findByEntrepriseAndDateRange(
            @PathVariable Long entrepriseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(venteService.findByEntrepriseAndDateRange(entrepriseId, startDate, endDate));
    }

    @GetMapping("/{venteId}/lignes")
    @Operation(summary = "Récupérer les lignes de vente")
    public ResponseEntity<List<LigneVenteDTO>> findLignesVente(@PathVariable Long venteId) {
        return ResponseEntity.ok(venteService.findAllLignesVenteByVenteId(venteId));
    }

    @PostMapping("/{venteId}/lignes")
    @Operation(summary = "Ajouter une ligne de vente")
    public ResponseEntity<LigneVenteDTO> addLigneVente(
            @PathVariable Long venteId,
            @Valid @RequestBody LigneVenteDTO ligneVenteDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(venteService.addLigneVente(venteId, ligneVenteDTO));
    }
}