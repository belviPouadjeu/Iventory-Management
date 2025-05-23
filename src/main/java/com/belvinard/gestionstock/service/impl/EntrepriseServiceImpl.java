package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.exceptions.APIException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.responses.EntrepriseResponse;
import com.belvinard.gestionstock.service.EntrepriseService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntrepriseServiceImpl implements EntrepriseService {
    private final EntrepriseRepository entrepriseRepository;
    private final ModelMapper modelMapper;

    public EntrepriseServiceImpl(EntrepriseRepository entrepriseRepository, ModelMapper modelMapper) {
        this.entrepriseRepository = entrepriseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public EntrepriseDTO createEntreprise(EntrepriseDTO entrepriseDTO) {

        Entreprise entreprise = modelMapper.map(entrepriseDTO, Entreprise.class);

        Entreprise entrepriseFromDb = entrepriseRepository.findByNom(entreprise.getNom());
        if (entrepriseFromDb != null) {
            throw new ResourceNotFoundException("Entreprise with the name " + entreprise.getNom() + " already exist !!");
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


}