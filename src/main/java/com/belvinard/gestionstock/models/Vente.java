package com.belvinard.gestionstock.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class Vente extends AbstractEntity {

  @NotBlank(message = "Le code de la vente est obligatoire")
  @Size(min = 4, max = 50, message = "Le code doit contenir entre 4 et 50 caractères")
  private String code;

  @NotNull(message = "La date de vente est obligatoire")
  private LocalDateTime dateVente;

  @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
  private String commentaire;

  @Enumerated(EnumType.STRING)
  private EtatVente etatVente;

  @NotNull(message = "Le client est obligatoire")
  @ManyToOne
  @JoinColumn(name = "idclient", referencedColumnName = "id")
  private Client client;

  @ManyToOne
  @JoinColumn(name = "idEntreprise", referencedColumnName = "id")
  private Entreprise entreprise;

  @OneToMany(mappedBy = "vente")
  private List<LigneVente> ligneVentes;

}