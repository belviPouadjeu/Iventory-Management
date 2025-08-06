package com.belvinard.gestionstock.repositories;


import com.belvinard.gestionstock.models.RoleType;
import com.belvinard.gestionstock.models.Utilisateur;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Méthodes existantes
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);

    // Recherche par rôle (via la table roles)
    @Query("SELECT u FROM Utilisateur u JOIN Roles r ON r.utilisateur.id = u.id WHERE r.roleType = :roleType")
    List<Utilisateur> findByRoleType(@Param("roleType") RoleType roleType);

    // Recherche par entreprise
    List<Utilisateur> findByEntrepriseId(Long entrepriseId);

    // Recherche par statut actif
    List<Utilisateur> findByActifTrue();
    List<Utilisateur> findByActifFalse();

    // Recherche combinée entreprise + statut
    List<Utilisateur> findByEntrepriseIdAndActifTrue(Long entrepriseId);
    List<Utilisateur> findByEntrepriseIdAndActifFalse(Long entrepriseId);

    boolean existsByUserName(@NotBlank @Size(min = 3, max = 20) String username);
}