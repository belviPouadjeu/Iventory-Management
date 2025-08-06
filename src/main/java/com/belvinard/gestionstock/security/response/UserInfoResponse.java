package com.belvinard.gestionstock.security.response;

import com.belvinard.gestionstock.dto.AdresseDTO;
import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.dto.RolesDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {

    private String nom;
    private String prenom;
    private String email;
    private LocalDateTime dateDeNaissance;
    private String photo;
    private AdresseDTO adresse;
    private EntrepriseDTO entreprise;
    private List<RolesDTO> roles;
    private Boolean actif;

}
