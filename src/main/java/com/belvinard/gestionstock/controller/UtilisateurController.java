package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.ChangerMotDePasseUtilisateurDTO;
import com.belvinard.gestionstock.dto.UtilisateurDTO;
import com.belvinard.gestionstock.models.RoleType;
import com.belvinard.gestionstock.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateur-Controller", description = "API de gestion des utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    // === ADMIN ONLY ===

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/create")
    @Operation(summary = "[ADMIN] Créer un nouvel utilisateur")
    public ResponseEntity<UtilisateurDTO> save(@RequestBody @Valid UtilisateurDTO dto) {
        UtilisateurDTO saved = utilisateurService.save(dto, dto.getEntrepriseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "[ADMIN] Récupérer un utilisateur par son ID")
    public ResponseEntity<UtilisateurDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.findById(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    @Operation(summary = "[ADMIN] Récupérer tous les utilisateurs")
    public ResponseEntity<List<UtilisateurDTO>> findAll() {
        return ResponseEntity.ok(utilisateurService.findAll());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "[ADMIN] Supprimer un utilisateur")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        utilisateurService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/email/{email}")
    @Operation(summary = "[ADMIN] Récupérer un utilisateur par son email")
    public ResponseEntity<UtilisateurDTO> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(utilisateurService.findByEmail(email));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/{id}/roles/{roleType}")
    @Operation(summary = "[ADMIN] Assigner un rôle à un utilisateur")
    public ResponseEntity<UtilisateurDTO> assignRole(@PathVariable Long id, @PathVariable RoleType roleType) {
        return ResponseEntity.ok(utilisateurService.assignRole(id, roleType));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}/roles/{roleType}")
    @Operation(summary = "[ADMIN] Retirer un rôle d'un utilisateur")
    public ResponseEntity<UtilisateurDTO> removeRole(@PathVariable Long id, @PathVariable RoleType roleType) {
        return ResponseEntity.ok(utilisateurService.removeRole(id, roleType));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/roles/{roleType}")
    @Operation(summary = "[ADMIN] Récupérer tous les utilisateurs ayant un rôle spécifique")
    public ResponseEntity<List<UtilisateurDTO>> findByRole(@PathVariable RoleType roleType) {
        return ResponseEntity.ok(utilisateurService.findByRole(roleType));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/activate")
    @Operation(summary = "[ADMIN] Activer un utilisateur")
    public ResponseEntity<UtilisateurDTO> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.activateUser(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/deactivate")
    @Operation(summary = "[ADMIN] Désactiver un utilisateur")
    public ResponseEntity<UtilisateurDTO> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.deactivateUser(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/entreprise/{entrepriseId}")
    @Operation(summary = "[ADMIN] Récupérer tous les utilisateurs d'une entreprise")
    public ResponseEntity<List<UtilisateurDTO>> findByEntreprise(@PathVariable Long entrepriseId) {
        return ResponseEntity.ok(utilisateurService.findByEntreprise(entrepriseId));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/actifs")
    @Operation(summary = "[ADMIN] Récupérer tous les utilisateurs actifs")
    public ResponseEntity<List<UtilisateurDTO>> findActiveUsers() {
        return ResponseEntity.ok(utilisateurService.findActiveUsers());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/inactifs")
    @Operation(summary = "[ADMIN] Récupérer tous les utilisateurs inactifs")
    public ResponseEntity<List<UtilisateurDTO>> findInactiveUsers() {
        return ResponseEntity.ok(utilisateurService.findInactiveUsers());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/entreprise/{entrepriseId}/actifs")
    @Operation(summary = "[ADMIN] Récupérer tous les utilisateurs actifs d'une entreprise")
    public ResponseEntity<List<UtilisateurDTO>> findActiveUsersByEntreprise(@PathVariable Long entrepriseId) {
        return ResponseEntity.ok(utilisateurService.findActiveUsersByEntreprise(entrepriseId));
    }

//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @GetMapping("/admins")
//    @Operation(summary = "[ADMIN] Récupérer tous les utilisateurs créés")
//    public ResponseEntity<List<UtilisateurDTO>> findAdminUsers() {
//        return ResponseEntity.ok(utilisateurService.findAdminUsers());
//    }

    // === ACCESSIBLE À TOUS LES UTILISATEURS ===

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/changer-mot-de-passe")
    @Operation(summary = "[TOUS] Changer le mot de passe de l'utilisateur connecté")
    public ResponseEntity<UtilisateurDTO> changerMotDePasse(@RequestBody ChangerMotDePasseUtilisateurDTO dto) {
        return ResponseEntity.ok(utilisateurService.changerMotDePasse(dto));
    }
}
