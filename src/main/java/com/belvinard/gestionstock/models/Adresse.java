package com.belvinard.gestionstock.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class Adresse implements Serializable {

  @NotBlank(message = "L'adresse 1 est obligatoire")
  @Size(min = 5, max = 100, message = "L'adresse 1 doit contenir entre 5 et 100 caractères")
  private String adresse1;

  @Size(min = 4,  message = "L'adresse 2 doit contenir au moins 4 caractères")
  private String adresse2;

  @NotBlank(message = "La ville est obligatoire")
  @Size(min = 4, max = 50, message = "La ville doit contenir entre 4 et 50 caractères")
  private String ville;

  @NotBlank(message = "Le code postal est obligatoire")
  @Size(min = 3, max = 10, message = "Le code postal doit contenir entre 4 et 10 caractères")
  private String codePostale;

  @NotBlank(message = "Le pays est obligatoire")
  @Size(min = 2, max = 50, message = "Le pays doit contenir entre 2 et 50 caractères")
  private String pays;

}