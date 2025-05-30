package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.FournisseurDTO;

import java.util.List;

public interface FournisseurService {

  FournisseurDTO createFournisseur(Long entrepriseId, FournisseurDTO fournisseurDTO);

  List<FournisseurDTO> getAllFournisseur();

  //FournisseurDTO findById(Long id);


  //FournisseurDTO delete(Long id);
}
