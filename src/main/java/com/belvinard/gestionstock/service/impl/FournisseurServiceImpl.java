package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.ArticleDTO;
import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.dto.FournisseurDTO;
import com.belvinard.gestionstock.exceptions.APIException;
import com.belvinard.gestionstock.exceptions.DuplicateEntityException;
import com.belvinard.gestionstock.exceptions.InvalidOperationException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.models.Fournisseur;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.repositories.FournisseurRepository;
import com.belvinard.gestionstock.service.FournisseurService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FournisseurServiceImpl implements FournisseurService {
    private final FournisseurRepository fournisseurRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final ModelMapper modelMapper;

    /**
     * Crée un nouveau fournisseur pour une entreprise donnée.
     *
     * @param entrepriseId   L'identifiant de l'entreprise
     * @param fournisseurDTO Les données du fournisseur à créer
     * @return FournisseurDTO contenant les informations du fournisseur créé
     * @throws ResourceNotFoundException si l'entreprise n'existe pas
     * @throws DuplicateEntityException si un fournisseur avec le même nom et prénom existe déjà
     *
     * Cette méthode :
     * 1. Vérifie l'existence de l'entreprise
     * 2. Vérifie si un fournisseur avec le même nom et prénom existe déjà
     * 3. Crée le nouveau fournisseur
     * 4. Associe le fournisseur à l'entreprise
     * 5. Retourne les informations du fournisseur créé
     */
    @Override
    public FournisseurDTO createFournisseur(Long entrepriseId, FournisseurDTO fournisseurDTO) {
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée avec l'id " + entrepriseId));

        boolean fournisseurExists = fournisseurRepository.existsByNomIgnoreCaseAndPrenomIgnoreCaseAndEntrepriseId(
                fournisseurDTO.getNom(),
                fournisseurDTO.getPrenom(),
                entrepriseId
        );

        if (fournisseurExists) {
            throw new DuplicateEntityException(
                    String.format("Un fournisseur avec le nom '%s' et le prénom '%s' existe déjà dans cette entreprise",
                            fournisseurDTO.getNom(),
                            fournisseurDTO.getPrenom())
            );
        }

        Fournisseur fournisseur = modelMapper.map(fournisseurDTO, Fournisseur.class);
        fournisseur.setEntreprise(entreprise);

        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);

        FournisseurDTO savedDTO = modelMapper.map(savedFournisseur, FournisseurDTO.class);
        savedDTO.setEntrepriseId(entreprise.getId());
        savedDTO.setEntrepriseName(entreprise.getNom());

        return savedDTO;
    }

    /**
     * Récupère la liste de tous les fournisseurs avec leurs informations d'entreprise.
     *
     * @return Liste de FournisseurDTO contenant les informations des fournisseurs et de leurs entreprises
     * @throws APIException si aucun fournisseur n'est trouvé dans la base de données
     *
     * Cette méthode :
     * 1. Récupère tous les fournisseurs de la base de données
     * 2. Vérifie si la liste n'est pas vide
     * 3. Convertit chaque fournisseur en DTO en incluant :
     *    - Les informations du fournisseur
     *    - L'ID de l'entreprise
     *    - Le nom de l'entreprise
     */
    @Override
    public List<FournisseurDTO> getAllFournisseur() {
        List<Fournisseur> fournisseurs = fournisseurRepository.findAll();

        if (fournisseurs.isEmpty()) {
            throw new APIException("Aucun fournisseur trouvé dans la base de données");
        }

        return fournisseurs.stream()
                .map(fournisseur -> {
                    FournisseurDTO fournisseurDTO = modelMapper.map(fournisseur, FournisseurDTO.class);
                    if (fournisseur.getEntreprise() != null) {
                        fournisseurDTO.setEntrepriseId(fournisseur.getEntreprise().getId());
                        fournisseurDTO.setEntrepriseName(fournisseur.getEntreprise().getNom());
                    }
                    return fournisseurDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * Récupère un fournisseur par son identifiant avec les informations de son entreprise.
     *
     * @param fournisseurId L'identifiant du fournisseur à rechercher
     * @return FournisseurDTO contenant les informations du fournisseur et de son entreprise
     * @throws ResourceNotFoundException si le fournisseur n'existe pas
     */
    @Override
    public FournisseurDTO findFournisseurById(Long fournisseurId) {
        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non trouvé avec l'id " + fournisseurId));

        FournisseurDTO fournisseurDTO = modelMapper.map(fournisseur, FournisseurDTO.class);

        if (fournisseur.getEntreprise() != null) {
            fournisseurDTO.setEntrepriseId(fournisseur.getEntreprise().getId());
            fournisseurDTO.setEntrepriseName(fournisseur.getEntreprise().getNom());
        }

        return fournisseurDTO;
    }

    /**
     * Supprime un fournisseur par son identifiant.
     *
     * @param fournisseurId L'identifiant du fournisseur à supprimer
     * @return FournisseurDTO Les informations du fournisseur qui a été supprimé
     * @throws ResourceNotFoundException si le fournisseur n'existe pas
     * @throws InvalidOperationException dans les cas suivants :
     *         - Si le fournisseur a des commandes associées
     *         - Si le fournisseur est référencé par d'autres entités dans la base de données
     *
     * Cette méthode :
     * 1. Vérifie l'existence du fournisseur
     * 2. Contrôle les dépendances (commandes associées)
     * 3. Procède à la suppression si aucune contrainte n'est violée
     * 4. Convertit et retourne les informations du fournisseur supprimé
     *
     * Note : La méthode est transactionnelle pour garantir l'intégrité des données
     */
    @Override
    @Transactional
    public FournisseurDTO deleteFournisseur(Long fournisseurId) {
        // Récupérer le fournisseur
        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non trouvé avec l'id " + fournisseurId));

        // Vérifier si le fournisseur a des commandes
        if (!fournisseur.getCommandeFournisseurs().isEmpty()) {
            throw new InvalidOperationException(
                    "Impossible de supprimer le fournisseur car il a des commandes associées"
            );
        }

        try {
            // Supprimer le fournisseur
            fournisseurRepository.delete(fournisseur);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidOperationException(
                    "Impossible de supprimer le fournisseur car il est référencé par d'autres entités"
            );
        }

        // Convertir en DTO pour le retour
        FournisseurDTO fournisseurDTO = modelMapper.map(fournisseur, FournisseurDTO.class);

        // Ajouter les informations de l'entreprise au DTO
        if (fournisseur.getEntreprise() != null) {
            fournisseurDTO.setEntrepriseId(fournisseur.getEntreprise().getId());
            fournisseurDTO.setEntrepriseName(fournisseur.getEntreprise().getNom());
        }

        return fournisseurDTO;
    }

    /**
     * Met à jour les informations d'un fournisseur.
     *
     * @param fournisseurId L'identifiant du fournisseur à mettre à jour
     * @param fournisseurDTO Les nouvelles données du fournisseur
     * @return FournisseurDTO Les informations du fournisseur mises à jour
     * @throws ResourceNotFoundException si le fournisseur n'existe pas
     * @throws DuplicateEntityException si le nom et prénom existent déjà pour un autre fournisseur
     */
    @Override
    @Transactional
    public FournisseurDTO updateFournisseur(Long fournisseurId, FournisseurDTO fournisseurDTO) {

        Fournisseur existingFournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur non trouvé avec l'id " + fournisseurId));

        boolean fournisseurExists = fournisseurRepository.existsByNomAndPrenomAndEntrepriseIdAndIdNot(
                fournisseurDTO.getNom(),
                fournisseurDTO.getPrenom(),
                existingFournisseur.getEntreprise().getId(),
                fournisseurId
        );

        if (fournisseurExists) {
            throw new DuplicateEntityException(
                    "Un fournisseur avec le nom '" + fournisseurDTO.getNom() +
                            "' et le prénom '" + fournisseurDTO.getPrenom() +
                            "' existe déjà dans cette entreprise"
            );
        }

        existingFournisseur.setNom(fournisseurDTO.getNom());
        existingFournisseur.setPrenom(fournisseurDTO.getPrenom());
        existingFournisseur.setNumTel(fournisseurDTO.getNumTel());
        existingFournisseur.setAdresse(fournisseurDTO.getAdresse());
        existingFournisseur.setPhoto(fournisseurDTO.getPhoto());

        Fournisseur updatedFournisseur = fournisseurRepository.save(existingFournisseur);

        FournisseurDTO updatedDTO = modelMapper.map(updatedFournisseur, FournisseurDTO.class);

        if (updatedFournisseur.getEntreprise() != null) {
            updatedDTO.setEntrepriseId(updatedFournisseur.getEntreprise().getId());
            updatedDTO.setEntrepriseName(updatedFournisseur.getEntreprise().getNom());
        }

        return updatedDTO;
    }


}



