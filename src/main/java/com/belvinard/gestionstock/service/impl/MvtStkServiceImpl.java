package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.MvtStkDTO;
import com.belvinard.gestionstock.exceptions.InvalidOperationException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.*;
import com.belvinard.gestionstock.repositories.ArticleRepository;
import com.belvinard.gestionstock.repositories.MvtStkRepository;
import com.belvinard.gestionstock.service.MvtStkService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MvtStkServiceImpl implements MvtStkService {

    private final MvtStkRepository mvtStkRepository;
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public MvtStkDTO entreeStock(Long articleId, BigDecimal quantite, SourceMvtStk source, Long entrepriseId) {
        return createMvtStk(articleId, quantite, TypeMvtStk.ENTREE, source, entrepriseId);
    }

    @Override
    @Transactional
    public MvtStkDTO sortieStock(Long articleId, BigDecimal quantite, SourceMvtStk source, Long entrepriseId) {
        return createMvtStk(articleId, quantite, TypeMvtStk.SORTIE, source, entrepriseId);
    }

    @Override
    @Transactional
    public MvtStkDTO correctionStock(Long articleId, BigDecimal quantite, TypeMvtStk typeMvt, Long entrepriseId) {
        return createMvtStk(articleId, quantite, typeMvt, SourceMvtStk.COMMANDE_FOURNISSEUR, entrepriseId);
    }

    @Override
    public List<MvtStkDTO> findByArticleId(Long articleId) {
        return mvtStkRepository.findAllByArticleIdOrderByDateMvtDesc(articleId).stream()
                .map(mvt -> modelMapper.map(mvt, MvtStkDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MvtStkDTO> findByEntrepriseId(Long entrepriseId) {
        return mvtStkRepository.findAllByEntrepriseId(entrepriseId).stream()
                .map(mvt -> modelMapper.map(mvt, MvtStkDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MvtStkDTO> findByTypeMvt(TypeMvtStk typeMvt) {
        return mvtStkRepository.findByTypeMvt(typeMvt).stream()
                .map(mvt -> modelMapper.map(mvt, MvtStkDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MvtStkDTO> findBySourceMvt(SourceMvtStk sourceMvt) {
        return mvtStkRepository.findBySourceMvt(sourceMvt).stream()
                .map(mvt -> modelMapper.map(mvt, MvtStkDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MvtStkDTO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return mvtStkRepository.findByDateMvtBetween(startDate, endDate).stream()
                .map(mvt -> modelMapper.map(mvt, MvtStkDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateCurrentStock(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        return BigDecimal.valueOf(article.getQuantiteEnStock());
    }

    @Override
    public List<MvtStkDTO> getStockHistory(Long articleId) {
        return findByArticleId(articleId);
    }

    @Override
    @Transactional
    public void createMvtStkForVente(Long venteId) {
        // Implementation for vente stock movement
    }

    @Override
    @Transactional
    public void createMvtStkForCommandeFournisseur(Long commandeId) {
        // Implementation for commande fournisseur stock movement
    }

    private MvtStkDTO createMvtStk(Long articleId, BigDecimal quantite, TypeMvtStk typeMvt, SourceMvtStk source, Long entrepriseId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        if (typeMvt == TypeMvtStk.SORTIE && article.getQuantiteEnStock() < quantite.longValue()) {
            throw new InvalidOperationException("Stock insuffisant");
        }

        updateArticleStock(article, quantite, typeMvt);

        MvtStk mvtStk = new MvtStk();
        mvtStk.setArticle(article);
        mvtStk.setQuantite(quantite);
        mvtStk.setTypeMvt(typeMvt);
        mvtStk.setSourceMvt(source);
        mvtStk.setEntrepriseId(entrepriseId);
        mvtStk.setDateMvt(LocalDateTime.now());

        MvtStk saved = mvtStkRepository.save(mvtStk);
        return modelMapper.map(saved, MvtStkDTO.class);
    }

    private void updateArticleStock(Article article, BigDecimal quantite, TypeMvtStk typeMvt) {
        Long currentStock = article.getQuantiteEnStock();
        Long newStock;

        switch (typeMvt) {
            case ENTREE:
            case CORRECTION_POS:
                newStock = currentStock + quantite.longValue();
                break;
            case SORTIE:
            case CORRECTION_NEG:
                newStock = currentStock - quantite.longValue();
                break;
            default:
                throw new InvalidOperationException("Type de mouvement non supporté: " + typeMvt);
        }

        article.setQuantiteEnStock(newStock);
        articleRepository.save(article);
    }


}