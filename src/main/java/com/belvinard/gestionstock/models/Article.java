package com.belvinard.gestionstock.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "article")
public class Article extends AbstractEntity {

  @NotBlank(message = "Le code article est obligatoire")
  @Size(min = 4, max = 50, message = "Le code article doit etre entre 4 et 50 caractères")
  private String codeArticle;

  @NotBlank(message = "La désignation est obligatoire")
  @Size(min = 4, max = 100, message = "La désignation doit etre entre 4 et 100 caractères")
  private String designation;

  private Long quantiteEnStock;

  @NotNull(message = "Le prix HT est obligatoire")
  @DecimalMin(value = "0.0", inclusive = false,
          message = "Le prix HT doit être positif")
  private BigDecimal prixUnitaireHt; // Prix Hors Taxe

  @NotNull(message = "Le taux de TVA est obligatoire")
  @DecimalMin(value = "0.0", inclusive = true,
          message = "Le taux de TVA ne peut pas être négatif")
  private BigDecimal tauxTva;  // Taux de TVA applicable

  @Schema(hidden = true)
  private BigDecimal prixUnitaireTtc; // Prix Toutes Taxes Comprises


  @ManyToOne(optional = false)
  @JoinColumn(name = "idcategory", nullable = false)
  private Category category;

  @ManyToOne(optional = false)
  @JoinColumn(name = "entrepriseId", nullable = false)
  private Entreprise entreprise;

  @OneToMany(mappedBy = "article")
  private List<LigneVente> ligneVentes;

  @OneToMany(mappedBy = "article")
  private List<LigneCommandeClient> ligneCommandeClients;

  @OneToMany(mappedBy = "article")
  private List<MvtStk> mvtStks;

  @ManyToOne
  @JoinColumn(name = "commande_id")
  private CommandeFournisseur commandeFournisseur;

  @OneToMany(mappedBy = "article")
  private List<LigneCommandeFournisseur> ligneCommandeFournisseurs;


}
