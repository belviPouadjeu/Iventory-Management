package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.ChangerMotDePasseUtilisateurDTO;
import com.belvinard.gestionstock.dto.UtilisateurDTO;
import com.belvinard.gestionstock.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateur-Controller", description = "API de gestion des utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping
    @Operation(summary = "Créer un nouvel utilisateur")
    public ResponseEntity<UtilisateurDTO> save(@RequestBody UtilisateurDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurService.save(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par son ID")
    public ResponseEntity<UtilisateurDTO> findById(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.findById(id));
    }

    @GetMapping("/long/{id}")
    @Operation(summary = "Récupérer un utilisateur par son ID (Long)")
    public ResponseEntity<UtilisateurDTO> findByIdLonge(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.findByIdLonge(id));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les utilisateurs")
    public ResponseEntity<List<UtilisateurDTO>> findAll() {
        return ResponseEntity.ok(utilisateurService.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de l'utilisateur à supprimer") @PathVariable Long id) {
        utilisateurService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Récupérer un utilisateur par son email")
    public ResponseEntity<UtilisateurDTO> findByEmail(
            @Parameter(description = "Email de l'utilisateur") @PathVariable String email) {
        return ResponseEntity.ok(utilisateurService.findByEmail(email));
    }

    @PutMapping("/changer-mot-de-passe")
    @Operation(summary = "Changer le mot de passe d'un utilisateur")
    public ResponseEntity<UtilisateurDTO> changerMotDePasse(
            @RequestBody ChangerMotDePasseUtilisateurDTO dto) {
        return ResponseEntity.ok(utilisateurService.changerMotDePasse(dto));
    }
}