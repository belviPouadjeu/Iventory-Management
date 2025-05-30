package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.CommandeFournisseurDTO;
import com.belvinard.gestionstock.exceptions.APIException;
import com.belvinard.gestionstock.exceptions.InvalidEntityException;
import com.belvinard.gestionstock.exceptions.InvalidOperationException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.CommandeFournisseur;
import com.belvinard.gestionstock.models.EtatCommande;
import com.belvinard.gestionstock.models.Fournisseur;
import com.belvinard.gestionstock.repositories.CommandeFournisseurRepository;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.repositories.FournisseurRepository;
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
    private final ModelMapper modelMapper;

    /**
     * Crée une nouvelle commande fournisseur
     *
     * @param commandeFournisseurDTO Les données de la commande
     * @param fournisseurId          L'ID du fournisseur
     * @return CommandeFournisseurDTO avec les informations complètes incluant le nom du fournisseur
     * @throws InvalidEntityException    si la commande est invalide
     * @throws APIException              si le code existe déjà
     * @throws ResourceNotFoundException si le fournisseur n'existe pas
     */
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

        CommandeFournisseur savedCommandeFournisseur = commandeFournisseurRepository.save(commande);

        // Conversion en DTO avec les informations supplémentaires
        CommandeFournisseurDTO savedDTO = modelMapper.map(savedCommandeFournisseur, CommandeFournisseurDTO.class);

        // Ajout des informations du fournisseur
        savedDTO.setFournisseurId(fournisseur.getId());
        savedDTO.setFournisseurName(fournisseur.getNom() + " " + fournisseur.getPrenom());

        return savedDTO;
    }

    /**
     * Recherche une commande fournisseur par son ID
     *
     * @param commandeId L'ID de la commande à rechercher
     * @return CommandeFournisseurDTO avec les informations complètes
     * @throws ResourceNotFoundException si la commande n'est pas trouvée
     */
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


    /**
     * Récupère toutes les commandes fournisseurs avec leurs informations détaillées
     *
     * @return Liste des CommandeFournisseurDTO avec les informations des fournisseurs
     */
    /**
     * Récupère toutes les commandes fournisseurs avec leurs informations détaillées.
     *
     * @return Liste des CommandeFournisseurDTO contenant les informations des commandes et de leurs fournisseurs
     * @throws ResourceNotFoundException si aucune commande fournisseur n'est trouvée dans la base de données
     *                                   <p>
     *                                   Cette méthode :
     *                                   1. Récupère toutes les commandes de la base de données
     *                                   2. Vérifie si des commandes existent
     *                                   3. Convertit chaque commande en DTO
     *                                   4. Enrichit les DTOs avec les informations des fournisseurs associés
     */
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


    /**
     * Supprime une commande fournisseur
     *
     * @param commandeId L'ID de la commande à supprimer
     * @return CommandeFournisseurDTO Les informations de la commande supprimée
     * @throws ResourceNotFoundException si la commande n'existe pas
     * @throws InvalidOperationException si la commande ne peut pas être supprimée
     */
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

    /**
     * Met à jour l'état d'une commande fournisseur
     *
     * @param idCommande ID de la commande à modifier
     * @param nouvelEtat Nouvel état de la commande
     * @return CommandeFournisseurDTO avec les informations mises à jour
     * @throws ResourceNotFoundException si la commande n'existe pas
     * @throws InvalidOperationException si la modification n'est pas autorisée
     */
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

        commande.setEtatCommande(nouvelEtat);

        CommandeFournisseur updatedCommande = commandeFournisseurRepository.save(commande);
        CommandeFournisseurDTO dto = modelMapper.map(updatedCommande, CommandeFournisseurDTO.class);

        if (updatedCommande.getFournisseur() != null) {
            Fournisseur fournisseur = updatedCommande.getFournisseur();
            dto.setFournisseurId(fournisseur.getId());
            dto.setFournisseurName(fournisseur.getNom() + " " + fournisseur.getPrenom());
        }

        return dto;
    }

    /**
     * Recherche une commande fournisseur par son code, sans tenir compte de la casse.
     *
     * @param code Le code unique de la commande fournisseur à rechercher
     * @return CommandeFournisseurDTO L'objet DTO contenant les informations de la commande fournisseur
     * @throws IllegalArgumentException si le code fourni est null
     * @throws ResourceNotFoundException si aucune commande fournisseur n'est trouvée avec le code spécifié
     */
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