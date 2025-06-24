package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.LigneCommandeFournisseurDTO;
import com.belvinard.gestionstock.exceptions.BusinessRuleException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.*;
import com.belvinard.gestionstock.repositories.ArticleRepository;
import com.belvinard.gestionstock.repositories.CommandeFournisseurRepository;
import com.belvinard.gestionstock.repositories.LigneCommandeFournisseurRepository;
import com.belvinard.gestionstock.service.LigneCommandeFournisseurService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LigneCommandeFournisseurServiceImpl implements LigneCommandeFournisseurService {
    private final LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository;
    private final CommandeFournisseurRepository commandeFournisseurRepository;
    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    /**
     * Sauvegarde une nouvelle ligne de commande fournisseur.
     *
     * @param ligneCommandeFournisseurDTO Les données de la ligne de commande
     * @param commandeFournisseurId L'ID de la commande fournisseur
     * @param articleId L'ID de l'article
     * @return LigneCommandeFournisseurDTO La ligne de commande créée
     * @throws ResourceNotFoundException si la commande ou l'article n'existe pas
     * @throws BusinessRuleException si la commande est déjà livrée
     */
    @Override
    @Transactional
    public LigneCommandeFournisseurDTO save(LigneCommandeFournisseurDTO ligneCommandeFournisseurDTO,
                                            Long commandeFournisseurId, Long articleId) {
        // Vérification de l'existence de la commande
        CommandeFournisseur commande = commandeFournisseurRepository.findById(commandeFournisseurId)
                .orElseThrow(() -> new ResourceNotFoundException("CommandeFournisseur", "ID", commandeFournisseurId));

        // Vérification de l'existence de l'article
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "ID", articleId));

        // Vérification de l'état de la commande
        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new BusinessRuleException("Impossible d'ajouter une ligne : la commande est déjà livrée.");
        }

        // Mise à jour du stock
        BigDecimal stockDisponible = BigDecimal.valueOf(article.getQuantiteEnStock());
        BigDecimal nouveauStock = stockDisponible.add(ligneCommandeFournisseurDTO.getQuantite());
        article.setQuantiteEnStock(nouveauStock.longValue());
        articleRepository.save(article);

        // Vérification de la quantité
        if (ligneCommandeFournisseurDTO.getQuantite() == null) {
            throw new BusinessRuleException("La quantité ne peut pas être null");
        }

        // DTO TO ENTITY: Création manuelle de l'entité à partir du DTO
        LigneCommandeFournisseur ligneCommandeFournisseur = new LigneCommandeFournisseur();
        ligneCommandeFournisseur.setCommandeFournisseur(commande);
        ligneCommandeFournisseur.setArticle(article);
        ligneCommandeFournisseur.setQuantite(ligneCommandeFournisseurDTO.getQuantite());

        // Calculs des prix
        BigDecimal prixHT = article.getPrixUnitaireHt();
        BigDecimal tauxTVA = article.getTauxTva();
        BigDecimal tva = prixHT.multiply(tauxTVA)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal prixTTC = prixHT.add(tva);
        BigDecimal prixTotal = prixTTC.multiply(ligneCommandeFournisseurDTO.getQuantite());

        // Configuration des prix dans la ligne de commande
        ligneCommandeFournisseur.setPrixUnitaireHt(prixHT);
        ligneCommandeFournisseur.setTauxTva(tauxTVA);
        ligneCommandeFournisseur.setPrixUnitaireTtc(prixTTC);

        // Sauvegarde de la ligne de commande
        LigneCommandeFournisseur savedLigneCommandeFournisseur = ligneCommandeFournisseurRepository.save(ligneCommandeFournisseur);

        // ENTITY TO DTO: Conversion de l'entité sauvegardée vers DTO avec ModelMapper
        LigneCommandeFournisseurDTO saveLigneCommandeFournisseurDTO = modelMapper.map(savedLigneCommandeFournisseur, LigneCommandeFournisseurDTO.class);
        saveLigneCommandeFournisseurDTO.setCommandeFournisseurId(commande.getId());
        saveLigneCommandeFournisseurDTO.setCommandeFournisseurName(commande.getCode());
        saveLigneCommandeFournisseurDTO.setArticleId(article.getId());
        saveLigneCommandeFournisseurDTO.setArticleName(article.getDesignation());
        saveLigneCommandeFournisseurDTO.setPrixTotal(prixTotal);

        return saveLigneCommandeFournisseurDTO;
    }

    @Override
    public List<LigneCommandeFournisseurDTO> getAll() {
        List<LigneCommandeFournisseur> lignes = ligneCommandeFournisseurRepository.findAll();
        
        return lignes.stream()
                .map(ligne -> {
                    // ENTITY TO DTO: Conversion de chaque entité vers DTO avec ModelMapper
                    LigneCommandeFournisseurDTO dto = modelMapper.map(ligne, LigneCommandeFournisseurDTO.class);
                    dto.setCommandeFournisseurId(ligne.getCommandeFournisseur().getId());
                    dto.setCommandeFournisseurName(ligne.getCommandeFournisseur().getCode());
                    dto.setArticleId(ligne.getArticle().getId());
                    dto.setArticleName(ligne.getArticle().getDesignation());
                    dto.setPrixTotal(ligne.getPrixUnitaireTtc().multiply(ligne.getQuantite()));
                    return dto;
                })
                .toList();
    }

    @Override
    public LigneCommandeFournisseurDTO findById(Long id) {
        if (id == null) {
            throw new BusinessRuleException("L'ID ne peut pas être null");
        }
        
        LigneCommandeFournisseur ligne = ligneCommandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LigneCommandeFournisseur", "ID", id));
        
        // ENTITY TO DTO: Conversion de l'entité trouvée vers DTO avec ModelMapper
        LigneCommandeFournisseurDTO dto = modelMapper.map(ligne, LigneCommandeFournisseurDTO.class);
        dto.setCommandeFournisseurId(ligne.getCommandeFournisseur().getId());
        dto.setCommandeFournisseurName(ligne.getCommandeFournisseur().getCode());
        dto.setArticleId(ligne.getArticle().getId());
        dto.setArticleName(ligne.getArticle().getDesignation());
        dto.setPrixTotal(ligne.getPrixUnitaireTtc().multiply(ligne.getQuantite()));
        
        return dto;
    }

    @Override
    @Transactional
    public LigneCommandeFournisseurDTO update(Long id, LigneCommandeFournisseurDTO ligneCommandeFournisseurDTO) {
        if (id == null) {
            throw new BusinessRuleException("L'ID ne peut pas être null");
        }
        
        // Vérification de l'existence de la ligne
        LigneCommandeFournisseur existingLigne = ligneCommandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LigneCommandeFournisseur", "ID", id));
        
        // Vérification de l'état de la commande
        if (existingLigne.getCommandeFournisseur().getEtatCommande() == EtatCommande.LIVREE) {
            throw new BusinessRuleException("Impossible de modifier une ligne : la commande est déjà livrée.");
        }
        
        // Vérification de la quantité
        if (ligneCommandeFournisseurDTO.getQuantite() == null) {
            throw new BusinessRuleException("La quantité ne peut pas être null");
        }
        
        // Mise à jour des champs modifiables
        existingLigne.setQuantite(ligneCommandeFournisseurDTO.getQuantite());
        
        // Recalcul du prix total
        BigDecimal prixTotal = existingLigne.getPrixUnitaireTtc().multiply(ligneCommandeFournisseurDTO.getQuantite());
        
        // Sauvegarde
        LigneCommandeFournisseur updatedLigne = ligneCommandeFournisseurRepository.save(existingLigne);
        
        // ENTITY TO DTO: Conversion de l'entité mise à jour vers DTO avec ModelMapper
        LigneCommandeFournisseurDTO dto = modelMapper.map(updatedLigne, LigneCommandeFournisseurDTO.class);
        dto.setCommandeFournisseurId(updatedLigne.getCommandeFournisseur().getId());
        dto.setCommandeFournisseurName(updatedLigne.getCommandeFournisseur().getCode());
        dto.setArticleId(updatedLigne.getArticle().getId());
        dto.setArticleName(updatedLigne.getArticle().getDesignation());
        dto.setPrixTotal(prixTotal);
        
        return dto;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuleException("L'ID ne peut pas être null");
        }
        
        // Vérification de l'existence de la ligne
        LigneCommandeFournisseur ligne = ligneCommandeFournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LigneCommandeFournisseur", "ID", id));
        
        // Vérification de l'état de la commande
        if (ligne.getCommandeFournisseur().getEtatCommande() == EtatCommande.LIVREE) {
            throw new BusinessRuleException("Impossible de supprimer une ligne : la commande est déjà livrée.");
        }
        
        // Suppression
        ligneCommandeFournisseurRepository.delete(ligne);
    }

    @Override
    public List<LigneCommandeFournisseurDTO> findByCommandeFournisseurId(Long commandeFournisseurId) {
        if (commandeFournisseurId == null) {
            throw new BusinessRuleException("L'ID de la commande fournisseur ne peut pas être null");
        }
        
        List<LigneCommandeFournisseur> lignes = ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(commandeFournisseurId);
        
        return lignes.stream()
                .map(ligne -> {
                    // ENTITY TO DTO: Conversion de chaque entité vers DTO avec ModelMapper
                    LigneCommandeFournisseurDTO dto = modelMapper.map(ligne, LigneCommandeFournisseurDTO.class);
                    dto.setCommandeFournisseurId(ligne.getCommandeFournisseur().getId());
                    dto.setCommandeFournisseurName(ligne.getCommandeFournisseur().getCode());
                    dto.setArticleId(ligne.getArticle().getId());
                    dto.setArticleName(ligne.getArticle().getDesignation());
                    dto.setPrixTotal(ligne.getPrixUnitaireTtc().multiply(ligne.getQuantite()));
                    return dto;
                })
                .toList();
    }

    @Override
    public List<LigneCommandeFournisseurDTO> findByArticleId(Long articleId) {
        if (articleId == null) {
            throw new BusinessRuleException("L'ID de l'article ne peut pas être null");
        }
        
        List<LigneCommandeFournisseur> lignes = ligneCommandeFournisseurRepository.findByArticleId(articleId);
        
        return lignes.stream()
                .map(ligne -> {
                    // ENTITY TO DTO: Conversion de chaque entité vers DTO avec ModelMapper
                    LigneCommandeFournisseurDTO dto = modelMapper.map(ligne, LigneCommandeFournisseurDTO.class);
                    dto.setCommandeFournisseurId(ligne.getCommandeFournisseur().getId());
                    dto.setCommandeFournisseurName(ligne.getCommandeFournisseur().getCode());
                    dto.setArticleId(ligne.getArticle().getId());
                    dto.setArticleName(ligne.getArticle().getDesignation());
                    dto.setPrixTotal(ligne.getPrixUnitaireTtc().multiply(ligne.getQuantite()));
                    return dto;
                })
                .toList();
    }

    @Override
    public BigDecimal getTotalByCommandeFournisseurId(Long commandeFournisseurId) {
        if (commandeFournisseurId == null) {
            throw new BusinessRuleException("L'ID de la commande fournisseur ne peut pas être null");
        }
        
        return ligneCommandeFournisseurRepository.getTotalByCommandeFournisseurId(commandeFournisseurId);
    }

}
