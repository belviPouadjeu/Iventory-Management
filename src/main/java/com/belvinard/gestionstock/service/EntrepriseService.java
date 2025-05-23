package com.belvinard.gestionstock.service;


import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.responses.EntrepriseResponse;

public interface EntrepriseService {
    EntrepriseDTO createEntreprise(EntrepriseDTO entrepriseDTO);
    EntrepriseResponse getAllEntreprises();
    EntrepriseDTO findEntrepriseById(Long id);
    EntrepriseDTO deleteEntrepriseById(Long id);
}