package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.LigneVenteDTO;
import com.belvinard.gestionstock.exceptions.InvalidOperationException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Article;
import com.belvinard.gestionstock.models.LigneVente;
import com.belvinard.gestionstock.models.Vente;
import com.belvinard.gestionstock.repositories.ArticleRepository;
import com.belvinard.gestionstock.repositories.LigneVenteRepository;
import com.belvinard.gestionstock.repositories.VenteRepository;
import com.belvinard.gestionstock.service.LigneVenteService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LigneVenteServiceImpl implements LigneVenteService {

    private final LigneVenteRepository ligneVenteRepository;
    private final VenteRepository venteRepository;
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public LigneVenteDTO save(LigneVenteDTO dto, Long venteId) {
        // Validate vente
        Vente vente = venteRepository.findById(venteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", "id", venteId));

        // Validate article
        Article article = articleRepository.findById(dto.getIdArticle())
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", dto.getIdArticle()));

        // Check stock
        if (!checkStockBeforeAdd(dto.getIdArticle(), dto.getQuantite())) {
            throw new InvalidOperationException("Stock insuffisant pour l'article: " + article.getDesignation());
        }

        // Decrease article stock
        Long newStock = article.getQuantiteEnStock() - dto.getQuantite().longValue();
        article.setQuantiteEnStock(newStock);
        articleRepository.save(article);

        LigneVente ligneVente = modelMapper.map(dto, LigneVente.class);
        ligneVente.setVente(vente);
        ligneVente.setArticle(article);

        LigneVente saved = ligneVenteRepository.save(ligneVente);
        return modelMapper.map(saved, LigneVenteDTO.class);
    }


    @Override
    public LigneVenteDTO findById(Long id) {
        LigneVente ligneVente = ligneVenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LigneVente", "id", id));
        return modelMapper.map(ligneVente, LigneVenteDTO.class);
    }

    @Override
    public List<LigneVenteDTO> findAllByVenteId(Long venteId) {
        return ligneVenteRepository.findAllByVenteId(venteId).stream()
                .map(ligne -> modelMapper.map(ligne, LigneVenteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<LigneVenteDTO> findAllByArticleId(Long articleId) {
        return ligneVenteRepository.findAllByArticleId(articleId).stream()
                .map(ligne -> modelMapper.map(ligne, LigneVenteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!ligneVenteRepository.existsById(id)) {
            throw new ResourceNotFoundException("LigneVente", "id", id);
        }
        ligneVenteRepository.deleteById(id);
    }

    @Override
    public List<LigneVenteDTO> findAll() {
        return ligneVenteRepository.findAll().stream()
                .map(ligne -> modelMapper.map(ligne, LigneVenteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllByVenteId(Long venteId) {
        ligneVenteRepository.deleteAllByVenteId(venteId);
    }

    @Override
    @Transactional
    public LigneVenteDTO updateQuantity(Long ligneVenteId, BigDecimal newQuantity) {
        LigneVente ligneVente = ligneVenteRepository.findById(ligneVenteId)
                .orElseThrow(() -> new ResourceNotFoundException("LigneVente", "id", ligneVenteId));

        Article article = ligneVente.getArticle();
        BigDecimal oldQuantity = ligneVente.getQuantite();
        BigDecimal stockDifference = newQuantity.subtract(oldQuantity);

        // Check if we have enough stock for the increase
        if (stockDifference.compareTo(BigDecimal.ZERO) > 0) {
            if (BigDecimal.valueOf(article.getQuantiteEnStock()).compareTo(stockDifference) < 0) {
                throw new InvalidOperationException("Stock insuffisant pour cette quantité");
            }
        }

        // Update article stock
        Long newStock = article.getQuantiteEnStock() - stockDifference.longValue();
        article.setQuantiteEnStock(newStock);
        articleRepository.save(article);

        ligneVente.setQuantite(newQuantity);
        LigneVente updated = ligneVenteRepository.save(ligneVente);
        return modelMapper.map(updated, LigneVenteDTO.class);
    }
    @Override
    public BigDecimal calculateTotalForVente(Long venteId) {
        List<LigneVente> lignes = ligneVenteRepository.findAllByVenteId(venteId);
        return lignes.stream()
                .map(ligne -> ligne.getPrixUnitaire().multiply(ligne.getQuantite()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean checkStockBeforeAdd(Long articleId, BigDecimal quantite) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        return article.getQuantiteEnStock() >= quantite.longValue();
    }
}