package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.FournisseurDTO;

import java.util.List;

public interface FournisseurService {

  FournisseurDTO createFournisseur(Long entrepriseId, FournisseurDTO fournisseurDTO);

  List<FournisseurDTO> getAllFournisseur();

  FournisseurDTO findFournisseurById(Long fournisseurId);

  FournisseurDTO deleteFournisseur(Long fournisseurId);

  FournisseurDTO updateFournisseur(Long fournisseurId, FournisseurDTO fournisseurDTO);
}
