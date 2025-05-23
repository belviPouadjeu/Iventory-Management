package com.belvinard.gestionstock.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "mvtstk")
public class MvtStk extends AbstractEntity {

  @NotNull(message = "La date du mouvement est obligatoire")
  private LocalDateTime dateMvt;

  @NotNull(message = "La quantité est obligatoire")
  @DecimalMin(value = "0.01", message = "La quantité doit être supérieure à zéro")
  @Column(name = "quantite")
  private BigDecimal quantite;

  @NotNull(message = "L'article est obligatoire")
  @ManyToOne
  @JoinColumn(name = "idarticle")
  private Article article;

  @NotNull(message = "Le type de mouvement est obligatoire")
  @Enumerated(EnumType.STRING)
  @Column(name = "typemvt")
  private TypeMvtStk typeMvt;

  @NotNull(message = "La source du mouvement est obligatoire")
  @Enumerated(EnumType.STRING)
  @Column(name = "sourcemvt")
  private SourceMvtStk sourceMvt;

  @NotNull(message = "L'entreprise est obligatoire")
  @Column(name = "identreprise")
  private Long entrepriseId;
}
