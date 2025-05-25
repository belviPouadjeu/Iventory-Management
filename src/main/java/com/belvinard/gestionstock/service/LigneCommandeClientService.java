package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;

public interface LigneCommandeClientService {
    LigneCommandeClientDTO createLigneCommandeClient(Long commandeId, Long articleId, LigneCommandeClientDTO ligneDTO);
}
