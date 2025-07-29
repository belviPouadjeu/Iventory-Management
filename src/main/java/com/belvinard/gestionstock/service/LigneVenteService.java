package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.LigneVenteDTO;

import java.math.BigDecimal;
import java.util.List;

public interface LigneVenteService {
    LigneVenteDTO save(LigneVenteDTO dto, Long VenteId);

    LigneVenteDTO findById(Long id);

    List<LigneVenteDTO> findAllByVenteId(Long venteId);

    List<LigneVenteDTO> findAllByArticleId(Long articleId);

    void delete(Long id);
    List<LigneVenteDTO> findAll();

    void deleteAllByVenteId(Long venteId);

    LigneVenteDTO updateQuantity(Long ligneVenteId, BigDecimal newQuantity);

    BigDecimal calculateTotalForVente(Long venteId);

    boolean checkStockBeforeAdd(Long articleId, BigDecimal quantite);
}
