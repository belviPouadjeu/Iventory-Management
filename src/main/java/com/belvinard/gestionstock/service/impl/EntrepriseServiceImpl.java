package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.exceptions.APIException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.responses.EntrepriseResponse;
import com.belvinard.gestionstock.service.EntrepriseService;
import com.belvinard.gestionstock.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntrepriseServiceImpl implements EntrepriseService {
    private final EntrepriseRepository entrepriseRepository;
    private final ModelMapper modelMapper;
    private final MinioService minioService;

    @Override
    public EntrepriseDTO createEntreprise(EntrepriseDTO entrepriseDTO) {

        Entreprise entreprise = modelMapper.map(entrepriseDTO, Entreprise.class);

        Entreprise entrepriseFromDb = entrepriseRepository.findByNom(entreprise.getNom());
        if (entrepriseFromDb != null) {
            throw new ResourceNotFoundException(
                    "Entreprise with the name " + entreprise.getNom() + " already exist !!");
        }

        Entreprise savedEntreprise = entrepriseRepository.save(entreprise);

        return modelMapper.map(savedEntreprise, EntrepriseDTO.class);

    }

    @Override
    public EntrepriseResponse getAllEntreprises() {
        List<Entreprise> entreprises = entrepriseRepository.findAll();
        if (entreprises.isEmpty()) {
            throw new APIException("No Entreprises created untill now !!!");
        }

        List<EntrepriseDTO> entrepriseDTOS = entreprises.stream()
                .map(entreprise -> modelMapper.map(entreprise, EntrepriseDTO.class))
                .toList();

        EntrepriseResponse entrepriseResponse = new EntrepriseResponse();
        entrepriseResponse.setContent(entrepriseDTOS);
        return entrepriseResponse;
    }

    @Override
    public EntrepriseDTO findEntrepriseById(Long id) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise with id " + id + " not found !!"));
        return modelMapper.map(entreprise, EntrepriseDTO.class);
    }

    @Override
    public EntrepriseDTO deleteEntrepriseById(Long id) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise with id " + id + " not found !!"));

        EntrepriseDTO entrepriseDTO = modelMapper.map(entreprise, EntrepriseDTO.class);

        entrepriseRepository.delete(entreprise);
        return entrepriseDTO;
    }

    @Override
    public EntrepriseDTO updateEntrepriseImage(Long id, MultipartFile image) throws IOException {
        Entreprise entrepriseFromDb = entrepriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise with id " + id + " not found !!"));
        String fileName = minioService.uploadImage(image);
        entrepriseFromDb.setPhoto(fileName);
        String imageUrl = minioService.getPreSignedUrl(fileName, 15);
        Entreprise updatedEntreprise = entrepriseRepository.save(entrepriseFromDb);
        EntrepriseDTO entrepriseDTO = modelMapper.map(updatedEntreprise, EntrepriseDTO.class);
        entrepriseDTO.setPhoto(imageUrl);

        return entrepriseDTO;
    }

    @Override
    public EntrepriseDTO updateEntreprise(Long id, EntrepriseDTO entrepriseDTO) {
        // 1. Vérifier que l'entreprise existe
        Entreprise existingEntreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise with id " + id + " not found !!"));

        // 2. Vérifier l'unicité du nom si il a changé
        if (!existingEntreprise.getNom().equals(entrepriseDTO.getNom())) {
            Entreprise entrepriseWithSameName = entrepriseRepository.findByNom(entrepriseDTO.getNom());
            if (entrepriseWithSameName != null) {
                throw new APIException("Une entreprise avec le nom '" + entrepriseDTO.getNom() + "' existe déjà !!");
            }
        }

        // 3. Mettre à jour les champs modifiables
        existingEntreprise.setNom(entrepriseDTO.getNom());
        existingEntreprise.setDescription(entrepriseDTO.getDescription());
        existingEntreprise.setCodeFiscal(entrepriseDTO.getCodeFiscal());
        existingEntreprise.setEmail(entrepriseDTO.getEmail());
        existingEntreprise.setNumTel(entrepriseDTO.getNumTel());
        existingEntreprise.setSteWeb(entrepriseDTO.getSteWeb());

        // 4. Mettre à jour l'adresse si fournie
        if (entrepriseDTO.getAdresse() != null) {
            existingEntreprise.setAdresse(entrepriseDTO.getAdresse());
        }

        // 5. Sauvegarder les modifications
        Entreprise updatedEntreprise = entrepriseRepository.save(existingEntreprise);

        // 6. Retourner le DTO mis à jour
        return modelMapper.map(updatedEntreprise, EntrepriseDTO.class);
    }

    @Override
    public String getPresignedImageUrl(Long id) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise with id " + id + " not found !!"));

        String fileName = entreprise.getPhoto();

        if (fileName == null || fileName.isBlank()) {
            throw new APIException("No image found for this entreprise");
        }

        return minioService.getPreSignedUrl(fileName, 900); // 15 minutes
    }

}