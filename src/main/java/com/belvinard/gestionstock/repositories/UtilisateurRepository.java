package com.belvinard.gestionstock.repositories;


import com.belvinard.gestionstock.models.RoleType;
import com.belvinard.gestionstock.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Méthodes existantes
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);

    // Recherche par rôle
    List<Utilisateur> findByRoles_RoleType(RoleType roleType);

    // Recherche par entreprise
    List<Utilisateur> findByEntrepriseId(Long entrepriseId);

    // Recherche par statut actif
    List<Utilisateur> findByActifTrue();
    List<Utilisateur> findByActifFalse();

    // Recherche combinée entreprise + statut
    List<Utilisateur> findByEntrepriseIdAndActifTrue(Long entrepriseId);
    List<Utilisateur> findByEntrepriseIdAndActifFalse(Long entrepriseId);

    // Recherche combinée rôle + entreprise
    List<Utilisateur> findByRoles_RoleTypeAndEntrepriseId(RoleType roleType, Long entrepriseId);

    // Recherche combinée rôle + statut
    List<Utilisateur> findByRoles_RoleTypeAndActifTrue(RoleType roleType);
}