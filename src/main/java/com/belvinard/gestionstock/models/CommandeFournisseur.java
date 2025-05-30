package com.belvinard.gestionstock.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "commandefournisseur")
public class CommandeFournisseur extends AbstractEntity {

  @NotBlank(message = "Le code de la commande est obligatoire")
  @Size(min = 1, max = 50, message = "Le code de la commande doit avoir entre 1 et 50 caractères")
  @Column(name = "code", unique = true, nullable = false)
  private String code;

  @NotNull(message = "L'état de la commande est obligatoire")
  @Column(name = "etatcommande")
  @Enumerated(EnumType.STRING)
  private EtatCommande etatCommande;

  @ManyToOne
  @JoinColumn(name = "fournisseur_id")
  private Fournisseur fournisseur;


}