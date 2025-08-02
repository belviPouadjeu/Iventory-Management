package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.ChangerMotDePasseUtilisateurDTO;
import com.belvinard.gestionstock.dto.UtilisateurDTO;
import com.belvinard.gestionstock.models.RoleType;

import java.util.List;

public interface UtilisateurService {

  // Méthodes existantes
  UtilisateurDTO save(UtilisateurDTO DTO);
  UtilisateurDTO findByIdLonge(Long id);
  UtilisateurDTO findById(Long id);
  List<UtilisateurDTO> findAll();
  void delete(Long id);
  UtilisateurDTO findByEmail(String email);
  UtilisateurDTO changerMotDePasse(ChangerMotDePasseUtilisateurDTO DTO);

  // Gestion des rôles
  UtilisateurDTO assignRole(Long userId, RoleType roleType);
  UtilisateurDTO removeRole(Long userId, RoleType roleType);
  List<UtilisateurDTO> findByRole(RoleType roleType);

  // Gestion d'état
  UtilisateurDTO activateUser(Long userId);
  UtilisateurDTO deactivateUser(Long userId);

  // Filtrage
  List<UtilisateurDTO> findByEntreprise(Long entrepriseId);
  List<UtilisateurDTO> findActiveUsers();
  List<UtilisateurDTO> findInactiveUsers();
  List<UtilisateurDTO> findActiveUsersByEntreprise(Long entrepriseId);
}