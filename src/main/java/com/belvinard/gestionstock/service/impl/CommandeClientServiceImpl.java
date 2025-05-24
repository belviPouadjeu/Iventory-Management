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

    // Création d'une commande (retourne seulement clientName)
    @Override
    public CommandeClientDTO createCommandeClient(Long clientId, Long entrepriseId, CommandeClientDTO commandeClientDTO) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise", "id", entrepriseId));

        if (!client.getEntreprise().getId().equals(entreprise.getId())) {
            throw new BusinessRuleException("Ce client n'appartient pas à cette entreprise.");
        }

        CommandeClient commandeClient = modelMapper.map(commandeClientDTO, CommandeClient.class);
        commandeClient.setClient(client);
        commandeClient.setEntreprise(entreprise);

        if (commandeClient.getCode() == null || commandeClient.getCode().isEmpty()) {
            commandeClient.setCode("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        CommandeClient savedCommande = commandeClientRepository.save(commandeClient);

        CommandeClientDTO savedDTO = modelMapper.map(savedCommande, CommandeClientDTO.class);
        savedDTO.setClientName(client.getNom());
        savedDTO.setClientId(client.getId());
        savedDTO.setEntrepriseId(entreprise.getId());

        return savedDTO;
    }

    @Override
    public CommandeClientDTO updateEtatCommande(Long idCommande, EtatCommande etatCommande) {

        // 1. Vérifier l'existence de la commande
        CommandeClient commande = commandeClientRepository.findById(idCommande)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CommandeClient", "id", idCommande
                ));

        // 2. Vérifier que la commande n'est pas déjà livrée
        if (commande.getEtatCommande() == EtatCommande.LIVREE) {
            throw new BusinessRuleException("Impossible de modifier une commande déjà livrée.");
        }

        log.info("Mise à jour de la commande ID {} vers l'état {}", idCommande, etatCommande);

        // 3. Mettre à jour l'état de la commande
        commande.setEtatCommande(etatCommande);

        // 4. Sauvegarder la commande modifiée
        CommandeClient updatedCommande = commandeClientRepository.save(commande);

        // 5. Retourner le DTO complet
        return convertToDTO(updatedCommande);
    }


    @Override
    public CommandeClientDTO findById(Long id) {
        CommandeClient commandeClient = commandeClientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucune commande client trouvée avec l'ID : " + id
                ));

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


    // Recherche par code (retourne seulement clientName)
    @Override
    public CommandeClientDTO findByCode(String code) {
        CommandeClient commandeClient = commandeClientRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable avec le code : " + code));

        CommandeClientDTO dto = modelMapper.map(commandeClient, CommandeClientDTO.class);
        dto.setClientName(commandeClient.getClient().getNom());
        dto.setEntrepriseId(commandeClient.getEntreprise().getId());

        if (commandeClient.getLigneCommandeClients() != null) {
            List<LigneCommandeClientDTO> lignesDTO = commandeClient.getLigneCommandeClients()
                    .stream()
                    .map(ligne -> modelMapper.map(ligne, LigneCommandeClientDTO.class))
                    .collect(Collectors.toList());
            dto.setLigneCommandeClients(lignesDTO);
        }

        return dto;
    }

    @Override
    public List<CommandeClientDTO> findAll() {
        return commandeClientRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Convertit une CommandeClient en DTO (utilisé dans findAll(), findById(), etc.)
    private CommandeClientDTO convertToDTO(CommandeClient commandeClient) {
        CommandeClientDTO dto = modelMapper.map(commandeClient, CommandeClientDTO.class);
        dto.setClientName(commandeClient.getClient().getNom());
        dto.setClientId(commandeClient.getClient().getId());
        dto.setEntrepriseId(commandeClient.getEntreprise().getId());

        if (commandeClient.getLigneCommandeClients() != null) {
            List<LigneCommandeClientDTO> lignesDTO = commandeClient.getLigneCommandeClients()
                    .stream()
                    .map(ligne -> modelMapper.map(ligne, LigneCommandeClientDTO.class))
                    .collect(Collectors.toList());
            dto.setLigneCommandeClients(lignesDTO);
        }

        return dto;
    }


}