package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.MvtStkDTO;
import com.belvinard.gestionstock.exceptions.InvalidOperationException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.*;
import com.belvinard.gestionstock.repositories.ArticleRepository;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
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
    private final EntrepriseRepository entrepriseRepository;

    @Override
    @Transactional
    public MvtStkDTO entreeStock(Long articleId, BigDecimal quantite, SourceMvtStk source, Long entrepriseId) {
        if (articleId == null || quantite == null || source == null || entrepriseId == null) {
            throw new InvalidOperationException("Tous les paramètres sont obligatoires");
        }
        
        // Vérifier que l'article existe
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        
        // Vérifier que l'entreprise existe (vous devez ajouter EntrepriseRepository si nécessaire)
        entrepriseRepository.findById(entrepriseId)
           .orElseThrow(() -> new ResourceNotFoundException("Entreprise", "id", entrepriseId));
        
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

    @Override
    @Transactional
    public MvtStkDTO reserverStock(Long articleId, BigDecimal quantite, Long entrepriseId) {
        return createMvtStk(articleId, quantite, TypeMvtStk.RESERVATION, SourceMvtStk.VENTE, entrepriseId);
    }

    @Override
    @Transactional
    public MvtStkDTO annulerReservation(Long articleId, BigDecimal quantite, Long entrepriseId) {
        return createMvtStk(articleId, quantite, TypeMvtStk.ANNULATION_RESERVATION, SourceMvtStk.VENTE, entrepriseId);
    }

    private MvtStkDTO createMvtStk(Long articleId, BigDecimal quantite, TypeMvtStk typeMvt, SourceMvtStk source, Long entrepriseId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

        // Vérification du stock disponible pour réservation
        if (typeMvt == TypeMvtStk.RESERVATION) {
            Long stockDisponible = article.getQuantiteEnStock() - (article.getQuantiteReservee() != null ? article.getQuantiteReservee() : 0L);
            if (stockDisponible < quantite.longValue()) {
                throw new InvalidOperationException("Stock insuffisant pour l'article: " + article.getDesignation());
            }
        }
        
        // Vérification du stock physique pour sortie
        if (typeMvt == TypeMvtStk.SORTIE && article.getQuantiteEnStock() < quantite.longValue()) {
            throw new InvalidOperationException("Stock insuffisant pour l'article: " + article.getDesignation());
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
        Long currentReserved = article.getQuantiteReservee() != null ? article.getQuantiteReservee() : 0L;
        
        switch (typeMvt) {
            case ENTREE:
            case CORRECTION_POS:
                article.setQuantiteEnStock(currentStock + quantite.longValue());
                break;
            case SORTIE:
            case CORRECTION_NEG:
                article.setQuantiteEnStock(currentStock - quantite.longValue());
                break;
            case RESERVATION:
                // Réservation : SEULEMENT augmente la quantité réservée, NE TOUCHE PAS au stock physique
                article.setQuantiteReservee(currentReserved + quantite.longValue());
                break;
            case ANNULATION_RESERVATION:
                // Annulation : SEULEMENT diminue la quantité réservée, NE TOUCHE PAS au stock physique
                article.setQuantiteReservee(Math.max(0L, currentReserved - quantite.longValue()));
                break;
            default:
                throw new InvalidOperationException("Type de mouvement non supporté: " + typeMvt);
        }

        articleRepository.save(article);
    }


}