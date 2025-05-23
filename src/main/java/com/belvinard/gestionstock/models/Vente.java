package com.belvinard.gestionstock.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class Vente extends AbstractEntity {

  @NotBlank(message = "Le code de la vente est obligatoire")
  @Size(min = 4, max = 50, message = "Le code doit contenir entre 4 et 50 caractères")
  private String code;

  @NotBlank(message = "Le commentaire est obligatoire")
  @Size(min = 4, max = 500, message = "Le commentaire est compris entre 500 caractères")
  private String commentaire;

  @Enumerated(EnumType.STRING)
  private EtatVente etatVente;


  @ManyToOne
  @JoinColumn(name = "idEntreprise", referencedColumnName = "id")
  private Entreprise entreprise;

  @OneToMany(mappedBy = "vente")
  private List<LigneVente> ligneVentes;

}