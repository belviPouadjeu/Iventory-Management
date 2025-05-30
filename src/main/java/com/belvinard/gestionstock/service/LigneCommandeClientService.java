package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;

import java.util.List;

public interface LigneCommandeClientService {
    LigneCommandeClientDTO createLigneCommandeClient(Long commandeId, Long articleId, LigneCommandeClientDTO ligneDTO);

    List<LigneCommandeClientDTO> getAllLigneCommandeClients();

    LigneCommandeClientDTO getLigneCommandeClientById(Long ligneId);
}
