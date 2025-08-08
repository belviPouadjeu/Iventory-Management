package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.CommandeClientDTO;
import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.models.EtatCommande;

import java.util.List;

public interface CommandeClientService {
    CommandeClientDTO createCommandeClient(Long clientId, CommandeClientDTO commandeClientDTO);

    CommandeClientDTO updateEtatCommande(Long idCommande, EtatCommande etatCommande);

    List<CommandeClientDTO> findAll();

    CommandeClientDTO findById(Long id);

    List<LigneCommandeClientDTO> findAllLignesCommandesClientByCommandeClientId(Long idCommande);

    CommandeClientDTO deleteCommandeClient(Long id);

    CommandeClientDTO findByCode(String code);
}
