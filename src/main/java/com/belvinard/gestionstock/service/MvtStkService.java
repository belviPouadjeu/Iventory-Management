package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.MvtStkDTO;
import com.belvinard.gestionstock.models.SourceMvtStk;
import com.belvinard.gestionstock.models.TypeMvtStk;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MvtStkService {
    // Record stock entry (purchase, supplier order, stock correction)
    MvtStkDTO entreeStock(Long articleId, BigDecimal quantite, SourceMvtStk source, Long entrepriseId);

    // Record stock exit (sale, client order, stock correction)
    MvtStkDTO sortieStock(Long articleId, BigDecimal quantite, SourceMvtStk source, Long entrepriseId);

    // Stock correction (positive/negative adjustments)
    MvtStkDTO correctionStock(Long articleId, BigDecimal quantite, TypeMvtStk typeMvt, Long entrepriseId);

    // Find all movements for an article
    List<MvtStkDTO> findByArticleId(Long articleId);

    // Find movements by enterprise
    List<MvtStkDTO> findByEntrepriseId(Long entrepriseId);

    // Find movements by type (ENTREE/SORTIE)
    List<MvtStkDTO> findByTypeMvt(TypeMvtStk typeMvt);

    // Find movements by source (VENTE/COMMANDE_CLIENT/COMMANDE_FOURNISSEUR)
    List<MvtStkDTO> findBySourceMvt(SourceMvtStk sourceMvt);

    // Find movements by date range
    List<MvtStkDTO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    // Calculate current stock for an article
    BigDecimal calculateCurrentStock(Long articleId);

    // Get stock history for an article
    List<MvtStkDTO> getStockHistory(Long articleId);

    // Create movement when sale is finalized (called from VenteService)
    void createMvtStkForVente(Long venteId);

    // Create movement when supplier order is received
    void createMvtStkForCommandeFournisseur(Long commandeId);




}
