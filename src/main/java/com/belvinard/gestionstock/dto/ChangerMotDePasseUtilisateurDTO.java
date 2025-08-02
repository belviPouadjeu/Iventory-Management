package com.belvinard.gestionstock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangerMotDePasseUtilisateurDTO {

  private Long id;

  private String motDePasseActuel;

  private String nouveauMotDePasse;

  private String confirmMotDePasse;

}