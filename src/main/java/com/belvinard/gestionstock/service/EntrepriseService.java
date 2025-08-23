package com.belvinard.gestionstock.service;


import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.responses.EntrepriseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EntrepriseService {
    EntrepriseDTO createEntreprise(EntrepriseDTO entrepriseDTO);
    EntrepriseResponse getAllEntreprises();
    EntrepriseDTO findEntrepriseById(Long id);
    EntrepriseDTO deleteEntrepriseById(Long id);
    EntrepriseDTO updateEntrepriseImage(Long id, MultipartFile image) throws IOException;
    EntrepriseDTO updateEntreprise(Long id, EntrepriseDTO entrepriseDTO);
    String getPresignedImageUrl(Long id);



}