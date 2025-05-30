package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.CommandeFournisseurDTO;
import com.belvinard.gestionstock.models.EtatCommande;

import java.util.List;

public interface CommandeFournisseurService{
    CommandeFournisseurDTO saveCommandFournisseur(CommandeFournisseurDTO commandeFournisseurDTO, Long fournisseurId);

    CommandeFournisseurDTO findById(Long fournisseurId);

    List<CommandeFournisseurDTO> findAll();

    CommandeFournisseurDTO delete(Long fournisseurId);

    CommandeFournisseurDTO updateEtatCommande(Long idCommande, EtatCommande nouvelEtat);

    CommandeFournisseurDTO findByCode(String code);
}