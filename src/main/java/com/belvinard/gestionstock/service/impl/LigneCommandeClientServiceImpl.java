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

    /**
     * Récupère toutes les lignes de commande client et les convertit en DTOs.
     *
     * @return Liste de LigneCommandeClientDTO contenant toutes les lignes de commande client
     * @throws APIException si aucune ligne de commande n'est trouvée dans la base de données
     *
     * Cette méthode :
     * 1. Récupère toutes les lignes de commande client depuis le repository
     * 2. Convertit chaque ligne de commande en objet DTO
     * 3. Définit les informations supplémentaires incluant :
     *    - Détails de la commande client (ID et code)
     *    - Détails de l'article (ID et désignation)
     *    - Calcule le prix total (prix unitaire * quantité)
     */
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

    /**
     * Récupère une ligne de commande client par son identifiant.
     *
     * @param ligneId L'identifiant de la ligne de commande à rechercher
     * @return LigneCommandeClientDTO contenant les informations détaillées de la ligne de commande
     * @throws ResourceNotFoundException si la ligne de commande n'existe pas
     *
     * Cette méthode :
     * 1. Recherche la ligne de commande dans la base de données
     * 2. Convertit l'entité en DTO
     * 3. Enrichit le DTO avec :
     *    - Les informations de la commande client (ID et code)
     *    - Les informations de l'article (ID et désignation)
     *    - Le calcul du prix total (prix unitaire TTC * quantité) si les données sont disponibles
     *
     * Note : Le prix total n'est calculé que si le prix unitaire TTC et la quantité sont non null
     */
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

    /**
     * Met à jour une ligne de commande client existante.
     *
     * @param ligneId  L'identifiant de la ligne de commande à modifier
     * @param ligneDTO Les nouvelles données de la ligne de commande
     * @return LigneCommandeClientDTO contenant les informations mises à jour
     * @throws ResourceNotFoundException si la ligne de commande ou l'article n'existe pas
     * @throws IllegalStateException si la commande est déjà dans l'état "LIVREE"
     *
     * Cette méthode :
     * 1. Vérifie l'existence de la ligne de commande
     * 2. Contrôle que la commande n'est pas déjà livrée
     * 3. Vérifie l'existence de l'article
     * 4. Met à jour les informations de la ligne avec :
     *    - Le nouvel article et la nouvelle quantité
     *    - Recalcul automatique des prix :
     *      * Prix HT
     *      * TVA
     *      * Prix TTC
     *      * Prix total (Prix TTC * quantité)
     * 5. Sauvegarde les modifications
     * 6. Retourne un DTO avec toutes les informations mises à jour
     */
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

    /**
     * Supprime une ligne de commande client.
     *
     * @param ligneId L'identifiant de la ligne de commande à supprimer
     * @return LigneCommandeClientDTO contenant les informations de la ligne supprimée
     * @throws ResourceNotFoundException si la ligne de commande n'existe pas
     * @throws InvalidOperationException si la commande associée est déjà livrée
     *
     * Cette méthode :
     * 1. Vérifie l'existence de la ligne de commande dans la base de données
     * 2. Contrôle que la commande associée n'est pas dans l'état "LIVREE"
     * 3. Convertit la ligne en DTO avant sa suppression
     * 4. Supprime la ligne de commande
     * 5. Retourne les informations de la ligne supprimée
     *
     * Note : La suppression n'est possible que si la commande n'est pas encore livrée
     */
    @Override
    public LigneCommandeClientDTO deleteLigneCommandeClient(Long ligneId) {
        LigneCommandeClient ligneFromDb = ligneCommandeClientRepository.findById(ligneId)
                .orElseThrow(() -> new ResourceNotFoundException("Ligne de commande client introuvable avec ID : " + ligneId));

        if (ligneFromDb.getCommandeClient().getEtatCommande() == EtatCommande.LIVREE) {
            throw new InvalidOperationException("Impossible de supprimer une ligne : la commande est déjà livrée");
        }

        LigneCommandeClientDTO deletedLigne = modelMapper.map(ligneFromDb, LigneCommandeClientDTO.class);

        ligneCommandeClientRepository.delete(ligneFromDb);
        return deletedLigne ;
    }

    /**
     * Récupère l'historique des commandes clients pour un article spécifique.
     *
     * @param idArticle L'identifiant de l'article dont on souhaite obtenir l'historique des commandes
     * @return Liste de LigneCommandeClientDTO contenant l'historique des commandes pour l'article spécifié
     * @throws ResourceNotFoundException si l'article avec l'ID spécifié n'existe pas
     *
     * Cette méthode :
     * 1. Vérifie l'existence de l'article dans la base de données
     * 2. Récupère toutes les lignes de commande associées à cet article
     * 3. Pour chaque ligne de commande, crée un DTO contenant :
     *    - Les informations de l'article (ID et désignation)
     *    - Les informations de la commande client (ID et code)
     *    - Le calcul du prix total (prix unitaire TTC * quantité)
     */
    @Override
    public List<LigneCommandeClientDTO> findHistoriqueCommandeClient(Long idArticle) {

        Article article = articleRepository.findById(idArticle)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", idArticle));

        List<LigneCommandeClient> ligneCommandAndArticle = ligneCommandeClientRepository.findAllByArticleId(idArticle);

        return ligneCommandAndArticle.stream()
                .map(ligne -> {
                    LigneCommandeClientDTO ligneCommandeClientDTO = modelMapper.map(ligne, LigneCommandeClientDTO.class);

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
