package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;

import java.util.List;

public interface LigneCommandeClientService {
    LigneCommandeClientDTO createLigneCommandeClient(Long commandeId, LigneCommandeClientDTO ligneDTO);

    List<LigneCommandeClientDTO> getAllLigneCommandeClients();

    LigneCommandeClientDTO getLigneCommandeClientById(Long ligneId);

    LigneCommandeClientDTO updateLigneCommandeClient(Long ligneId, LigneCommandeClientDTO ligneDTO);

    LigneCommandeClientDTO deleteLigneCommandeClient(Long ligneId);

    List<LigneCommandeClientDTO> findHistoriqueCommandeClient(Long idArticle);
}
