package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.CommandeFournisseurDTO;
import com.belvinard.gestionstock.models.EtatCommande;
import com.belvinard.gestionstock.responses.ErrorResponse;
import com.belvinard.gestionstock.service.CommandeFournisseurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/commandes-fournisseurs")
@Tag(name = "Commande Fournisseur Controller", description = "API de gestion des commandes fournisseurs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommandeFournisseurController {

    private final CommandeFournisseurService commandeFournisseurService;

    @PostMapping("/admin/fournisseur/{fournisseurId}")
    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "ADMIN: Créer une nouvelle commande fournisseur",
            description = "Accessible uniquement aux ADMIN. Crée une nouvelle commande pour un fournisseur spécifique."
    )
    public ResponseEntity<CommandeFournisseurDTO> createCommandeFournisseur(
            @PathVariable Long fournisseurId,
            @Valid @RequestBody CommandeFournisseurDTO commandeFournisseurDTO) {
        CommandeFournisseurDTO savedCommande = commandeFournisseurService
                .saveCommandFournisseur(commandeFournisseurDTO, fournisseurId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCommande);
    }

    @GetMapping("/manager/{fournisseurId}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "MANAGER ou ADMIN: Rechercher une commande fournisseur par ID",
            description = "Retourne une commande fournisseur en fonction de son ID. Accès réservé aux MANAGER ou ADMIN."
    )
    public ResponseEntity<CommandeFournisseurDTO> getCommandeFournisseurById(
            @PathVariable Long fournisseurId) {
        CommandeFournisseurDTO commande = commandeFournisseurService.findById(fournisseurId);
        return ResponseEntity.ok(commande);
    }

    @GetMapping("/manager")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "MANAGER ou ADMIN: Lister toutes les commandes fournisseurs",
            description = "Retourne la liste de toutes les commandes fournisseurs. Accès réservé aux MANAGER ou ADMIN."
    )
    public ResponseEntity<List<CommandeFournisseurDTO>> getAllCommandesFournisseurs() {
        List<CommandeFournisseurDTO> commandes = commandeFournisseurService.findAll();
        return ResponseEntity.ok(commandes);
    }

    @DeleteMapping("/admin/{fournisseurId}")
    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "ADMIN: Supprimer une commande fournisseur",
            description = "Supprime une commande fournisseur par son ID. Accessible uniquement aux ADMIN."
    )
    public ResponseEntity<CommandeFournisseurDTO> deleteCommandeFournisseur(@PathVariable Long fournisseurId) {
        CommandeFournisseurDTO deletedCommande = commandeFournisseurService.delete(fournisseurId);
        return new ResponseEntity<>(deletedCommande, HttpStatus.OK);
    }

    @PutMapping("/admin/{idCommande}/etat")
    //@PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "ADMIN: Mettre à jour l'état d'une commande fournisseur",
            description = "Permet aux ADMIN de modifier l’état d’une commande fournisseur."
    )
    public ResponseEntity<CommandeFournisseurDTO> updateEtatCommande(
            @PathVariable("idCommande") Long idCommande,
            @RequestParam EtatCommande nouvelEtat) {
        CommandeFournisseurDTO updatedCommande = commandeFournisseurService.updateEtatCommande(idCommande, nouvelEtat);
        return ResponseEntity.ok(updatedCommande);
    }

    @GetMapping("/manager/code/{code}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "MANAGER ou ADMIN: Rechercher une commande fournisseur par code",
            description = "Recherche une commande fournisseur à partir de son code unique. Accès réservé aux MANAGER ou ADMIN."
    )
    public ResponseEntity<CommandeFournisseurDTO> findByCode(@PathVariable("code") String code) {
        return ResponseEntity.ok(commandeFournisseurService.findByCode(code));
    }
}
