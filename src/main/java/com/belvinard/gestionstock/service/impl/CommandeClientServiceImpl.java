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

        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new BusinessRuleException("Impossible de modifier une commande déjà livrée.");
        }

        log.info("Mise à jour de la commande ID {} vers l'état {}", idCommande, etatCommande);

        commande.setEtatCommande(etatCommande);

        CommandeClient updatedCommande = commandeClientRepository.save(commande);

        return convertToDTO(updatedCommande);
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
        dto.setClientName(commandeClient.getClient().getNom());
        dto.setClientId(commandeClient.getClient().getId());
        dto.setEntrepriseId(commandeClient.getEntreprise().getId());

        return dto;
    }

}