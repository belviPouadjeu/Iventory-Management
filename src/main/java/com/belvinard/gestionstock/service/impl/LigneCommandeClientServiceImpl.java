package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.exceptions.APIException;
import com.belvinard.gestionstock.exceptions.BusinessRuleException;
import com.belvinard.gestionstock.exceptions.InvalidOperationException;
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

    @Override
    public LigneCommandeClientDTO createLigneCommandeClient(Long commandeId, LigneCommandeClientDTO ligneDTO) {
        // Validation des paramètres obligatoires
        if (ligneDTO.getArticleId() == null) {
            throw new IllegalArgumentException("L'ID de l'article est obligatoire dans le JSON");
        }

        CommandeClient commande = commandeClientRepository.findById(commandeId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Commande client non trouvée avec l'id " + commandeId));

        Article article = articleRepository.findById(ligneDTO.getArticleId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Article non trouvé avec l'id " + ligneDTO.getArticleId()));

        // Vérifier le stock disponible sans le diminuer
        BigDecimal stockDisponible = BigDecimal.valueOf(article.getQuantiteEnStock());
        if (stockDisponible.compareTo(ligneDTO.getQuantite()) < 0) {
            throw new IllegalArgumentException("Stock insuffisant pour l'article : quantité demandée = "
                    + ligneDTO.getQuantite() + ", en stock = " + stockDisponible);
        }
        // NE PAS diminuer le stock ici - il sera diminué à la finalisation de la vente

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
                .orElseThrow(
                        () -> new ResourceNotFoundException("Ligne commande client non trouvée avec l'id " + ligneId));

        LigneCommandeClientDTO ligneCommandeClientDTO = modelMapper.map(ligne, LigneCommandeClientDTO.class);

        ligneCommandeClientDTO.setCommandeClientId(ligne.getCommandeClient().getId());
        ligneCommandeClientDTO.setCommandeClientName(ligne.getCommandeClient().getCode());
        ligneCommandeClientDTO.setArticleId(ligne.getArticle().getId());
        ligneCommandeClientDTO.setArticleName(ligne.getArticle().getDesignation());

        if (ligne.getPrixUnitaireTtc() != null && ligne.getQuantite() != null) {
            BigDecimal prixTotal = ligne.getPrixUnitaireTtc().multiply(ligne.getQuantite());
            ligneCommandeClientDTO.setPrixTotal(prixTotal);
        }

        return ligneCommandeClientDTO;
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
                .orElseThrow(
                        () -> new ResourceNotFoundException("Article non trouvé avec l'id " + ligneDTO.getArticleId()));

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

    @Override
    public LigneCommandeClientDTO deleteLigneCommandeClient(Long ligneId) {
        LigneCommandeClient ligneFromDb = ligneCommandeClientRepository.findById(ligneId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ligne de commande client introuvable avec ID : " + ligneId));

        if (ligneFromDb.getCommandeClient().getEtatCommande() == EtatCommande.LIVREE) {
            throw new InvalidOperationException("Impossible de supprimer une ligne : la commande est déjà livrée");
        }

        LigneCommandeClientDTO deletedLigne = modelMapper.map(ligneFromDb, LigneCommandeClientDTO.class);

        ligneCommandeClientRepository.delete(ligneFromDb);
        return deletedLigne;
    }

    @Override
    public List<LigneCommandeClientDTO> findHistoriqueCommandeClient(Long idArticle) {

        Article article = articleRepository.findById(idArticle)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", idArticle));

        List<LigneCommandeClient> ligneCommandAndArticle = ligneCommandeClientRepository.findAllByArticleId(idArticle);

        return ligneCommandAndArticle.stream()
                .map(ligne -> {
                    LigneCommandeClientDTO ligneCommandeClientDTO = modelMapper.map(ligne,
                            LigneCommandeClientDTO.class);

                    ligneCommandeClientDTO.setArticleId(ligne.getArticle().getId());
                    ligneCommandeClientDTO.setArticleName(ligne.getArticle().getDesignation());

                    if (ligne.getCommandeClient() != null) {
                        ligneCommandeClientDTO.setCommandeClientId(ligne.getCommandeClient().getId());
                        ligneCommandeClientDTO.setCommandeClientName(ligne.getCommandeClient().getCode());
                    }

                    if (ligne.getPrixUnitaireTtc() != null && ligne.getQuantite() != null) {
                        ligneCommandeClientDTO.setPrixTotal(ligne.getPrixUnitaireTtc().multiply(ligne.getQuantite()));
                    }

                    return ligneCommandeClientDTO;
                })
                .collect(Collectors.toList());
    }

}
