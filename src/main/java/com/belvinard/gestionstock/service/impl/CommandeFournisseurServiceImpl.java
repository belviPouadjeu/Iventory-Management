package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.CommandeFournisseurDTO;
import com.belvinard.gestionstock.exceptions.APIException;
import com.belvinard.gestionstock.exceptions.InvalidEntityException;
import com.belvinard.gestionstock.exceptions.InvalidOperationException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.*;
import com.belvinard.gestionstock.repositories.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.belvinard.gestionstock.service.CommandeFournisseurService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeFournisseurServiceImpl implements CommandeFournisseurService {
    private final CommandeFournisseurRepository commandeFournisseurRepository;
    private final FournisseurRepository fournisseurRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository;
    private final ArticleRepository articleRepository;
    private final MvtStkRepository mvtStkRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public CommandeFournisseurDTO saveCommandFournisseur(CommandeFournisseurDTO commandeFournisseurDTO,
                                                         Long fournisseurId) {

        if (commandeFournisseurDTO == null || commandeFournisseurDTO.getCode() == null || commandeFournisseurDTO.getCode().isBlank()) {
            throw new InvalidEntityException("Commande fournisseur invalide : le code est requis");
        }

        boolean codeExists = commandeFournisseurRepository.existsByCode(commandeFournisseurDTO.getCode());
        if (codeExists) {
            throw new APIException("Une commande fournisseur existe déjà avec le code : " + commandeFournisseurDTO.getCode());
        }

        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non trouvé avec l'ID: " + fournisseurId));

        CommandeFournisseur commande = modelMapper.map(commandeFournisseurDTO, CommandeFournisseur.class);
        commande.setFournisseur(fournisseur);
        
        // Empêcher la création directe avec un état autre que EN_PREPARATION
        if (commande.getEtatCommande() != null && commande.getEtatCommande() != EtatCommande.EN_PREPARATION) {
            throw new InvalidOperationException(
                    "Une nouvelle commande ne peut être créée qu'avec l'état EN_PREPARATION. État reçu: " + commande.getEtatCommande()
            );
        }
        
        // Forcer l'état à EN_PREPARATION pour une nouvelle commande
        commande.setEtatCommande(EtatCommande.EN_PREPARATION);

        CommandeFournisseur savedCommandeFournisseur = commandeFournisseurRepository.save(commande);

        // Conversion en DTO avec les informations supplémentaires
        CommandeFournisseurDTO savedDTO = modelMapper.map(savedCommandeFournisseur, CommandeFournisseurDTO.class);

        // Ajout des informations du fournisseur
        savedDTO.setFournisseurId(fournisseur.getId());
        savedDTO.setFournisseurName(fournisseur.getNom() + " " + fournisseur.getPrenom());

        return savedDTO;
    }

    @Override
    public CommandeFournisseurDTO findById(Long commandeId) {
        CommandeFournisseur commandeFournisseur = commandeFournisseurRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande fournisseur non trouvée"));

        CommandeFournisseurDTO commandeFournisseurDTO = modelMapper.map(commandeFournisseur, CommandeFournisseurDTO.class);

        if (commandeFournisseur.getFournisseur() != null) {
            Fournisseur fournisseur = commandeFournisseur.getFournisseur();
            commandeFournisseurDTO.setFournisseurId(fournisseur.getId());
            commandeFournisseurDTO.setFournisseurName(fournisseur.getNom() + " " + fournisseur.getPrenom());
        }

        return commandeFournisseurDTO;
    }



    @Override
    public List<CommandeFournisseurDTO> findAll() {
        List<CommandeFournisseur> commandes = commandeFournisseurRepository.findAll();
        if (commandes.isEmpty()) {
            throw new ResourceNotFoundException("Aucune commande fournisseur trouvée");
        }

        return commandes
                .stream()
                .map(commande -> {
                    CommandeFournisseurDTO dto = modelMapper.map(commande, CommandeFournisseurDTO.class);

                    if (commande.getFournisseur() != null) {
                        Fournisseur fournisseur = commande.getFournisseur();
                        dto.setFournisseurId(fournisseur.getId());
                        dto.setFournisseurName(fournisseur.getNom() + " " + fournisseur.getPrenom());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public CommandeFournisseurDTO delete(Long commandeId) {
        CommandeFournisseur commandeFournisseur = commandeFournisseurRepository.findById(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande fournisseur non trouvée avec l'ID : " + commandeId
                ));

        if (commandeFournisseur.getEtatCommande() == EtatCommande.LIVREE) {
            throw new InvalidOperationException(
                    "Impossible de supprimer la commande " + commandeFournisseur.getCode() +
                            " car elle est en état LIVREE"
            );
        }

        CommandeFournisseurDTO dto = modelMapper.map(commandeFournisseur, CommandeFournisseurDTO.class);

        if (commandeFournisseur.getFournisseur() != null) {
            Fournisseur fournisseur = commandeFournisseur.getFournisseur();
            dto.setFournisseurId(fournisseur.getId());
            dto.setFournisseurName(fournisseur.getNom() + " " + fournisseur.getPrenom());
        }
        commandeFournisseurRepository.deleteById(commandeId);

        return dto;
    }

    @Override
    @Transactional
    public CommandeFournisseurDTO updateEtatCommande(Long idCommande, EtatCommande nouvelEtat) {
        if (nouvelEtat == null) {
            throw new InvalidOperationException("L'état de la commande ne peut pas être null");
        }

        CommandeFournisseur commande = commandeFournisseurRepository.findById(idCommande)
                .orElseThrow(() -> new ResourceNotFoundException("Commande fournisseur non trouvée avec l'ID: " + idCommande));

        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new InvalidOperationException(
                    "Impossible de modifier la commande " + commande.getCode() +
                            " car elle est en état LIVREE"
            );
        }

        // Vérifier qu'une commande ne peut pas passer directement de EN_PREPARATION à LIVREE
        if (commande.getEtatCommande() == EtatCommande.EN_PREPARATION && nouvelEtat == EtatCommande.LIVREE) {
            throw new InvalidOperationException(
                    "Impossible de passer directement de EN_PREPARATION à LIVREE pour la commande " + commande.getCode() +
                            ". La commande doit d'abord être VALIDEE"
            );
        }

        // Vérifier qu'une commande ne peut pas passer directement à LIVREE sans lignes
        if (nouvelEtat == EtatCommande.LIVREE) {
            List<LigneCommandeFournisseur> lignes = ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(commande.getId());
            if (lignes.isEmpty()) {
                throw new InvalidOperationException(
                        "Impossible de livrer la commande " + commande.getCode() +
                                " car elle ne contient aucune ligne de commande"
                );
            }
        }

        commande.setEtatCommande(nouvelEtat);

        // Si la commande passe à LIVREE, augmenter le stock des articles et créer les mouvements
        if (nouvelEtat == EtatCommande.LIVREE) {
            augmenterStockArticles(commande.getId());
        }

        CommandeFournisseur updatedCommande = commandeFournisseurRepository.save(commande);
        CommandeFournisseurDTO dto = modelMapper.map(updatedCommande, CommandeFournisseurDTO.class);

        if (updatedCommande.getFournisseur() != null) {
            Fournisseur fournisseur = updatedCommande.getFournisseur();
            dto.setFournisseurId(fournisseur.getId());
            dto.setFournisseurName(fournisseur.getNom() + " " + fournisseur.getPrenom());
        }

        return dto;
    }

    @Override
    @Transactional
    public CommandeFournisseurDTO annulerCommande(Long idCommande) {
        CommandeFournisseur commande = commandeFournisseurRepository.findById(idCommande)
                .orElseThrow(() -> new ResourceNotFoundException("Commande fournisseur non trouvée avec l'ID: " + idCommande));

        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new InvalidOperationException(
                    "Impossible d'annuler la commande " + commande.getCode() +
                            " car elle est déjà livrée"
            );
        }

        commande.setEtatCommande(EtatCommande.ANNULEE);
        CommandeFournisseur updatedCommande = commandeFournisseurRepository.save(commande);
        
        CommandeFournisseurDTO dto = modelMapper.map(updatedCommande, CommandeFournisseurDTO.class);
        if (updatedCommande.getFournisseur() != null) {
            Fournisseur fournisseur = updatedCommande.getFournisseur();
            dto.setFournisseurId(fournisseur.getId());
            dto.setFournisseurName(fournisseur.getNom() + " " + fournisseur.getPrenom());
        }
        return dto;
    }

    private void augmenterStockArticles(Long commandeId) {
        // Récupérer toutes les lignes validées de la commande
        List<LigneCommandeFournisseur> lignesValidees = ligneCommandeFournisseurRepository
                .findAllByCommandeFournisseurIdAndEtatLigne(commandeId, EtatLigneCommandeFournisseur.VALIDEE);
        
        CommandeFournisseur commande = commandeFournisseurRepository.findById(commandeId).orElse(null);
        Long entrepriseId = commande != null && commande.getFournisseur() != null && commande.getFournisseur().getEntreprise() != null 
                ? commande.getFournisseur().getEntreprise().getId() : 1L;
        
        for (LigneCommandeFournisseur ligne : lignesValidees) {
            Article article = ligne.getArticle();
            BigDecimal stockActuel = BigDecimal.valueOf(article.getQuantiteEnStock());
            BigDecimal nouveauStock = stockActuel.add(ligne.getQuantite());
            article.setQuantiteEnStock(nouveauStock.longValue());
            articleRepository.save(article);
            
            // Créer le mouvement de stock
            MvtStk mvtStk = new MvtStk();
            mvtStk.setArticle(article);
            mvtStk.setQuantite(ligne.getQuantite());
            mvtStk.setTypeMvt(TypeMvtStk.ENTREE);
            mvtStk.setSourceMvt(SourceMvtStk.COMMANDE_FOURNISSEUR);
            mvtStk.setEntrepriseId(entrepriseId);
            mvtStk.setDateMvt(LocalDateTime.now());
            mvtStkRepository.save(mvtStk);
        }
    }


    @Override
    public CommandeFournisseurDTO findByCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Le code ne peut pas être null");
        }

        String normalizedCode = code.trim().toUpperCase();

        CommandeFournisseur commandeFournisseur = commandeFournisseurRepository
                .findByCodeIgnoreCase(normalizedCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune commande fournisseur n'a été trouvée avec le code " + code));

        CommandeFournisseurDTO commandeFournisseurDTO = modelMapper.map(commandeFournisseur, CommandeFournisseurDTO.class);

        if (commandeFournisseur.getFournisseur() != null) {
            commandeFournisseurDTO.setFournisseurName(commandeFournisseur.getFournisseur().getNom());
        }

        return commandeFournisseurDTO;
    }



}