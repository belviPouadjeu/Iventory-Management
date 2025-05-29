package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.exceptions.BusinessRuleException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Article;
import com.belvinard.gestionstock.models.CommandeClient;
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

@Service
@RequiredArgsConstructor
public class LigneCommandeClientServiceImpl implements LigneCommandeClientService {

    private final CommandeClientRepository commandeClientRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

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



}
