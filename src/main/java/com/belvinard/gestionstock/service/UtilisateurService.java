package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.ChangerMotDePasseUtilisateurDTO;
import com.belvinard.gestionstock.dto.UtilisateurDTO;

import java.util.List;

public interface UtilisateurService {

  UtilisateurDTO save(UtilisateurDTO DTO);

  UtilisateurDTO findByIdLonge (Long id);

  UtilisateurDTO findById(Long id);

  List<UtilisateurDTO> findAll();

  void delete(Long id);

  UtilisateurDTO findByEmail(String email);

  UtilisateurDTO changerMotDePasse(ChangerMotDePasseUtilisateurDTO DTO);


}