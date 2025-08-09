package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.CommandeClientDTO;
import com.belvinard.gestionstock.dto.LigneCommandeClientDTO;
import com.belvinard.gestionstock.exceptions.BusinessRuleException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Client;
import com.belvinard.gestionstock.models.CommandeClient;
import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.models.EtatCommande;
import com.belvinard.gestionstock.repositories.ClientRepository;
import com.belvinard.gestionstock.repositories.CommandeClientRepository;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.repositories.LigneCommandeClientRepository;
import com.belvinard.gestionstock.service.CommandeClientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeClientServiceImpl implements CommandeClientService {
    private final CommandeClientRepository commandeClientRepository;
    private final ClientRepository clientRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    private final ModelMapper modelMapper;
    private static final Logger log = LoggerFactory.getLogger(CommandeClientServiceImpl.class);

    @Override
    public CommandeClientDTO createCommandeClient(Long clientId, CommandeClientDTO commandeClientDTO) {
        // Validation des paramètres obligatoires
        if (commandeClientDTO.getEntrepriseId() == null) {
            throw new IllegalArgumentException("L'ID de l'entreprise est obligatoire dans le JSON");
        }

        // Validation de la date de commande
        if (commandeClientDTO.getDateCommande() == null) {
            throw new IllegalArgumentException("La date de commande est obligatoire");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        Entreprise entreprise = entrepriseRepository.findById(commandeClientDTO.getEntrepriseId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Entreprise", "id", commandeClientDTO.getEntrepriseId()));

        // Vérifier que le client appartient bien à cette entreprise
        if (!client.getEntreprise().getId().equals(entreprise.getId())) {
            throw new BusinessRuleException("Ce client n'appartient pas à cette entreprise.");
        }

        // Validation : Une nouvelle commande ne peut pas être créée directement avec
        // l'état VALIDEE ou LIVREE
        if (commandeClientDTO.getEtatCommande() == EtatCommande.VALIDEE ||
                commandeClientDTO.getEtatCommande() == EtatCommande.LIVREE) {
            throw new BusinessRuleException(
                    "Une nouvelle commande ne peut pas être créée directement avec l'état VALIDEE ou LIVREE. Créez d'abord la commande en état EN_PREPARATION, ajoutez des lignes de commande, puis modifiez l'état.");
        }

        CommandeClient commandeClient = modelMapper.map(commandeClientDTO, CommandeClient.class);
        commandeClient.setClient(client);
        commandeClient.setEntreprise(entreprise);

        // Générer un code automatique si non fourni
        if (commandeClient.getCode() == null || commandeClient.getCode().isEmpty()) {
            commandeClient.setCode("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        CommandeClient savedCommande = commandeClientRepository.save(commandeClient);

        // Mapper vers DTO avec toutes les informations
        CommandeClientDTO savedDTO = modelMapper.map(savedCommande, CommandeClientDTO.class);
        savedDTO.setClientName(client.getNom() + " " + client.getPrenom());
        savedDTO.setClientId(client.getId());
        savedDTO.setEntrepriseId(entreprise.getId());

        return savedDTO;
    }

    @Override
    public CommandeClientDTO updateEtatCommande(Long idCommande, EtatCommande etatCommande) {

        CommandeClient commande = commandeClientRepository.findById(idCommande)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CommandeClient", "id", idCommande));

        // Valider la transition d'état
        validateEtatTransition(commande, etatCommande);

        log.info("Mise à jour de la commande ID {} vers l'état {}", idCommande, etatCommande);

        commande.setEtatCommande(etatCommande);

        CommandeClient updatedCommande = commandeClientRepository.save(commande);

        return convertToDTO(updatedCommande);
    }

    @Override
    public CommandeClientDTO annulerCommande(Long idCommande) {
        CommandeClient commande = commandeClientRepository.findById(idCommande)
                .orElseThrow(() -> new ResourceNotFoundException("CommandeClient", "id", idCommande));

        // Vérifier que la commande peut être annulée
        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new BusinessRuleException("Impossible d'annuler une commande déjà livrée.");
        }

        if (commande.getEtatCommande() == EtatCommande.VALIDEE) {
            throw new BusinessRuleException(
                    "Impossible d'annuler une commande déjà validée. Une commande validée est en cours de préparation.");
        }

        if (commande.getEtatCommande() == EtatCommande.ANNULEE) {
            throw new BusinessRuleException("Cette commande est déjà annulée.");
        }

        // Remettre en stock les articles si la commande avait des lignes
        if (commande.getLigneCommandeClients() != null && !commande.getLigneCommandeClients().isEmpty()) {
            for (LigneCommandeClient ligne : commande.getLigneCommandeClients()) {
                Article article = ligne.getArticle();
                // Remettre la quantité en stock
                Long nouvelleQuantite = article.getQuantiteEnStock() + ligne.getQuantite().longValue();
                article.setQuantiteEnStock(nouvelleQuantite);
                articleRepository.save(article);

                log.info("Remise en stock de {} unités pour l'article {} (ID: {})",
                        ligne.getQuantite(), article.getDesignation(), article.getId());
            }
        }

        // Changer l'état vers ANNULEE
        commande.setEtatCommande(EtatCommande.ANNULEE);
        CommandeClient commandeAnnulee = commandeClientRepository.save(commande);

        log.info("Commande {} annulée avec succès", commande.getCode());

        return convertToDTO(commandeAnnulee);
    }

    @Override
    public CommandeClientDTO findById(Long id) {
        CommandeClient commandeClient = commandeClientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucune commande client trouvée avec l'ID : " + id));

        log.info("Commande client trouvée avec ID: {}", id);

        return convertToDTO(commandeClient);
    }

    @Override
    public List<LigneCommandeClientDTO> findAllLignesCommandesClientByCommandeClientId(Long idCommande) {
        return List.of();
    }

    @Override
    public CommandeClientDTO deleteCommandeClient(Long id) {
        CommandeClient commande = commandeClientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande client introuvable avec l'ID " + id));

        // Vérification : si la commande est déjà livrée, on bloque la suppression
        if (EtatCommande.LIVREE.equals(commande.getEtatCommande())) {
            throw new BusinessRuleException("Impossible de supprimer une commande déjà livrée.");
        }

        commandeClientRepository.delete(commande);

        return modelMapper.map(commande, CommandeClientDTO.class);
    }

    @Override
    public CommandeClientDTO findByCode(String code) {
        CommandeClient commandeClient = commandeClientRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable avec le code : " + code));

        CommandeClientDTO dto = modelMapper.map(commandeClient, CommandeClientDTO.class);
        dto.setClientName(commandeClient.getClient().getNom());
        dto.setEntrepriseId(commandeClient.getEntreprise().getId());

        return dto;
    }

    @Override
    public List<CommandeClientDTO> findAll() {
        return commandeClientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CommandeClientDTO convertToDTO(CommandeClient commandeClient) {
        CommandeClientDTO dto = modelMapper.map(commandeClient, CommandeClientDTO.class);
        dto.setClientName(commandeClient.getClient().getNom() + " " + commandeClient.getClient().getPrenom());
        dto.setClientId(commandeClient.getClient().getId());
        dto.setEntrepriseId(commandeClient.getEntreprise().getId());

        // Enrichir les lignes de commande avec les informations manquantes
        if (commandeClient.getLigneCommandeClients() != null && !commandeClient.getLigneCommandeClients().isEmpty()) {
            List<LigneCommandeClientDTO> lignesEnrichies = commandeClient.getLigneCommandeClients().stream()
                    .map(ligne -> {
                        LigneCommandeClientDTO ligneDTO = modelMapper.map(ligne, LigneCommandeClientDTO.class);

                        // Ajouter les informations de l'article
                        if (ligne.getArticle() != null) {
                            ligneDTO.setArticleId(ligne.getArticle().getId());
                            ligneDTO.setArticleName(ligne.getArticle().getDesignation());
                        }

                        // Ajouter les informations de la commande
                        ligneDTO.setCommandeClientId(commandeClient.getId());
                        ligneDTO.setCommandeClientName(commandeClient.getCode());

                        // Calculer le prix total
                        if (ligne.getPrixUnitaireTtc() != null && ligne.getQuantite() != null) {
                            ligneDTO.setPrixTotal(ligne.getPrixUnitaireTtc().multiply(ligne.getQuantite()));
                        }

                        return ligneDTO;
                    })
                    .collect(Collectors.toList());

            dto.setLigneCommandeClients(lignesEnrichies);
        }

        return dto;
    }

    /**
     * Valide qu'une commande peut être mise dans l'état demandé
     * 
     * @param commande   La commande à valider
     * @param nouvelEtat Le nouvel état souhaité
     */
    private void validateEtatTransition(CommandeClient commande, EtatCommande nouvelEtat) {
        // Vérifier qu'une commande livrée ne peut plus être modifiée
        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new BusinessRuleException("Impossible de modifier une commande déjà livrée.");
        }

        // Vérifier qu'une commande ne peut être validée ou livrée sans lignes de
        // commande
        if (nouvelEtat == EtatCommande.VALIDEE || nouvelEtat == EtatCommande.LIVREE) {
            if (commande.getLigneCommandeClients() == null || commande.getLigneCommandeClients().isEmpty()) {
                throw new BusinessRuleException(
                        "Impossible de valider ou livrer une commande sans lignes de commande. " +
                                "Veuillez d'abord ajouter des articles à la commande.");
            }
        }

        // Vérifier la logique de transition d'état
        EtatCommande etatActuel = commande.getEtatCommande();

        // Une commande annulée ne peut plus être modifiée
        if (etatActuel == EtatCommande.ANNULEE) {
            throw new BusinessRuleException(
                    "Impossible de modifier une commande annulée. Utilisez la méthode d'annulation pour annuler une commande.");
        }

        // Empêcher de passer directement de EN_PREPARATION à LIVREE sans passer par
        // VALIDEE
        if (etatActuel == EtatCommande.EN_PREPARATION && nouvelEtat == EtatCommande.LIVREE) {
            throw new BusinessRuleException(
                    "Une commande doit être validée avant d'être livrée. " +
                            "Changez d'abord l'état vers VALIDEE, puis vers LIVREE.");
        }

        // Pour annuler une commande, utiliser la méthode dédiée
        if (nouvelEtat == EtatCommande.ANNULEE) {
            throw new BusinessRuleException(
                    "Pour annuler une commande, utilisez l'endpoint dédié /annuler au lieu de modifier directement l'état.");
        }
    }

}