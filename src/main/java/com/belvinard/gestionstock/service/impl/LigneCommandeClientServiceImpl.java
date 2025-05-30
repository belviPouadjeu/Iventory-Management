package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.exceptions.APIException;
import com.belvinard.gestionstock.exceptions.BusinessRuleException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Article;
import com.belvinard.gestionstock.models.CommandeClient;
import com.belvinard.gestionstock.models.EtatCommande;
import com.belvinard.gestionstock.models.LigneCommandeClient;
import com.belvinard.gestionstock.repositories.ArticleRepository;
import com.belvinard.gestionstock.repositories.CommandeClientRepository;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.repositories.LigneCommandeClientRepository;
import com.belvinard.gestionstock.service.LigneCommandeClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LigneCommandeClientServiceImpl implements LigneCommandeClientService {

    private final CommandeClientRepository commandeClientRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    /**
     * Creates a new order line item for a customer order.
     *
     * @param commandeId The ID of the customer order
     * @param articleId The ID of the article/product to add to the order
     * @param ligneDTO Data transfer object containing the order line details, including quantity
     *
     * @return LigneCommandeClientDTO The created order line with complete details including prices
     *
     * @throws ResourceNotFoundException If either the order or article is not found
     * @throws IllegalArgumentException If requested quantity exceeds available stock
     *
     * This method:
     * 1. Verifies the existence of both the order and article
     * 2. Checks if sufficient stock is available
     * 3. Updates the stock quantity
     * 4. Calculates prices (HT, TVA, TTC)
     * 5. Creates and saves the order line
     * 6. Returns a DTO with complete order line information
     */
    @Override
    public LigneCommandeClientDTO createLigneCommandeClient(Long commandeId, Long articleId,
                                                            LigneCommandeClientDTO ligneDTO) {

        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande client non trouvée avec l'id " + commandeId));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé avec l'id " + articleId));

        BigDecimal stockDisponible = BigDecimal.valueOf(article.getQuantiteEnStock());

        if (stockDisponible.compareTo(ligneDTO.getQuantite()) < 0) {
            throw new IllegalArgumentException("Stock insuffisant pour l'article : quantité demandée = "
                    + ligneDTO.getQuantite() + ", en stock = " + stockDisponible);
        }

        BigDecimal nouveauStock = stockDisponible.subtract(ligneDTO.getQuantite());
        article.setQuantiteEnStock(nouveauStock.longValue());
        articleRepository.save(article);

        LigneCommandeClient ligne = new LigneCommandeClient();
        ligne.setCommandeClient(commande);
        ligne.setArticle(article);
        ligne.setQuantite(ligneDTO.getQuantite());

        BigDecimal prixHT = article.getPrixUnitaireHt();
        BigDecimal tauxTVA = article.getTauxTva();
        BigDecimal tva = prixHT.multiply(tauxTVA).divide(BigDecimal.valueOf(100));
        BigDecimal prixTTC = prixHT.add(tva);
        BigDecimal prixTotal = prixTTC.multiply(ligneDTO.getQuantite());

        ligne.setPrixUnitaireHt(prixHT);
        ligne.setTauxTva(tauxTVA);
        ligne.setPrixUnitaireTtc(prixTTC);

        LigneCommandeClient ligneSaved = ligneCommandeClientRepository.save(ligne);

        LigneCommandeClientDTO ligneCommandeClientDTO = modelMapper.map(ligneSaved, LigneCommandeClientDTO.class);

        ligneCommandeClientDTO.setCommandeClientId(commande.getId());
        ligneCommandeClientDTO.setCommandeClientName(commande.getCode());
        ligneCommandeClientDTO.setArticleId(article.getId());
        ligneCommandeClientDTO.setArticleName(article.getDesignation());
        ligneCommandeClientDTO.setPrixTotal(prixTotal);

        return ligneCommandeClientDTO;
    }

    @Override
    public List<LigneCommandeClientDTO> getAllLigneCommandeClients() {
        List<LigneCommandeClient> lignes = ligneCommandeClientRepository.findAll();

        if (lignes.isEmpty()) {
            throw new APIException("Aucune de commande creer!");
        }

        return lignes.stream().map(ligne -> {
            LigneCommandeClientDTO ligneCommandeClientFromDb = modelMapper.map(ligne, LigneCommandeClientDTO.class);

            // Set commandeClient info
            if (ligne.getCommandeClient() != null) {
                ligneCommandeClientFromDb.setCommandeClientId(ligne.getCommandeClient().getId());
                ligneCommandeClientFromDb.setCommandeClientName(ligne.getCommandeClient().getCode());
            }

            if (ligne.getArticle() != null) {
                ligneCommandeClientFromDb.setArticleId(ligne.getArticle().getId());
                ligneCommandeClientFromDb.setArticleName(ligne.getArticle().getDesignation());
            }

            if (ligne.getPrixUnitaireTtc() != null && ligne.getQuantite() != null) {
                BigDecimal prixTotal = ligne.getPrixUnitaireTtc().multiply(ligne.getQuantite());
                ligneCommandeClientFromDb.setPrixTotal(prixTotal);
            }

            return ligneCommandeClientFromDb;
        }).collect(Collectors.toList());
    }


    @Override
    public LigneCommandeClientDTO getLigneCommandeClientById(Long ligneId) {
        LigneCommandeClient ligne = ligneCommandeClientRepository.findById(ligneId)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne commande client non trouvée avec l'id " + ligneId));

        LigneCommandeClientDTO ligneCommandeClientDTO = modelMapper.map(ligne, LigneCommandeClientDTO.class);

        ligneCommandeClientDTO.setCommandeClientId(ligne.getCommandeClient().getId());
        ligneCommandeClientDTO.setCommandeClientName(ligne.getCommandeClient().getCode());
        ligneCommandeClientDTO.setArticleId(ligne.getArticle().getId());
        ligneCommandeClientDTO.setArticleName(ligne.getArticle().getDesignation());

        if (ligne.getPrixUnitaireTtc() != null && ligne.getQuantite() != null) {
            BigDecimal prixTotal = ligne.getPrixUnitaireTtc().multiply(ligne.getQuantite());
            ligneCommandeClientDTO.setPrixTotal(prixTotal);
        }

        return ligneCommandeClientDTO ;
    }

    @Override
    public LigneCommandeClientDTO updateLigneCommandeClient(Long ligneId, LigneCommandeClientDTO ligneDTO) {
        LigneCommandeClient ligne = ligneCommandeClientRepository.findById(ligneId)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne de commande non trouvée avec l'id " + ligneId));

        CommandeClient commande = ligne.getCommandeClient();


        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new IllegalStateException("Impossible de modifier une ligne de commande déjà livrée.");
        }

        Article article = articleRepository.findById(ligneDTO.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé avec l'id " + ligneDTO.getArticleId()));

        ligne.setArticle(article);
        ligne.setQuantite(ligneDTO.getQuantite());

        BigDecimal prixHT = article.getPrixUnitaireHt();
        BigDecimal tauxTVA = article.getTauxTva();
        BigDecimal tva = prixHT.multiply(tauxTVA).divide(BigDecimal.valueOf(100));
        BigDecimal prixTTC = prixHT.add(tva);
        BigDecimal prixTotal = prixTTC.multiply(ligneDTO.getQuantite());

        ligne.setPrixUnitaireHt(prixHT);
        ligne.setTauxTva(tauxTVA);
        ligne.setPrixUnitaireTtc(prixTTC);

        LigneCommandeClient updatedLigne = ligneCommandeClientRepository.save(ligne);

        LigneCommandeClientDTO result = modelMapper.map(updatedLigne, LigneCommandeClientDTO.class);
        result.setCommandeClientId(commande.getId());
        result.setCommandeClientName(commande.getCode());
        result.setArticleId(article.getId());
        result.setArticleName(article.getDesignation());
        result.setPrixTotal(prixTotal);

        return result;
    }



}
