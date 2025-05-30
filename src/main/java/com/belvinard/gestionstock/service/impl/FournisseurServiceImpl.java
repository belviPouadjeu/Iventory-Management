package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.ArticleDTO;
import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.dto.FournisseurDTO;
import com.belvinard.gestionstock.exceptions.APIException;
import com.belvinard.gestionstock.exceptions.DuplicateEntityException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.models.Fournisseur;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.repositories.FournisseurRepository;
import com.belvinard.gestionstock.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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




}



