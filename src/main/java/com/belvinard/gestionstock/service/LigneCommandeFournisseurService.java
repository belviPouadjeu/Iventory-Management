package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.LigneCommandeFournisseurDTO;
import java.util.List;

public interface LigneCommandeFournisseurService {
    LigneCommandeFournisseurDTO save(LigneCommandeFournisseurDTO ligneCommandeFournisseurDTO, Long commandeFournisseurId, Long articleId);
    List<LigneCommandeFournisseurDTO> getAll();
    LigneCommandeFournisseurDTO findById(Long id);
    LigneCommandeFournisseurDTO update(Long id, LigneCommandeFournisseurDTO ligneCommandeFournisseurDTO);
    void delete(Long id);

    List<LigneCommandeFournisseurDTO> findByCommandeFournisseurId(Long commandeFournisseurId);
    List<LigneCommandeFournisseurDTO> findByArticleId(Long articleId);
    java.math.BigDecimal getTotalByCommandeFournisseurId(Long commandeFournisseurId);
    LigneCommandeFournisseurDTO validerLigne(Long id);

}
