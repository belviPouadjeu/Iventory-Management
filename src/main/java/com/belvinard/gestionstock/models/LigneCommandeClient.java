package com.belvinard.gestionstock.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lignecommandeclient")
public class LigneCommandeClient extends AbstractEntity {

  @NotNull(message = "La quantité est obligatoire")
  @DecimalMin(value = "0.01", message = "La quantité doit être supérieure à zéro")
  private BigDecimal quantite;

  @NotNull(message = "Le prix unitaire est obligatoire")
  @DecimalMin(value = "0.01", message = "Le prix unitaire doit être supérieur à zéro")
  private BigDecimal prixUnitaire;

  @ManyToOne
  @JoinColumn(name = "idcommandeclient")
  private CommandeClient commandeClient;

  @ManyToOne
  @JoinColumn(name = "idarticle")
  private Article article;


}

