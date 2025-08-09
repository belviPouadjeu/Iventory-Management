package com.belvinard.gestionstock.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "commandeclient")
public class CommandeClient extends AbstractEntity {

  @NotBlank(message = "Le code de la commande est obligatoire")
  @Size(min = 4, max = 50, message = "Le code de la commande doit contenir entre 4 et 50 caractères")
  private String code;

  /**
   * Date de livraison souhaitée pour la commande
   * (différente de creationDate qui est la date de création automatique)
   */
  @Column(name = "date_commande")
  private LocalDate dateCommande;

  private String commentaire;

  @Enumerated(EnumType.STRING)
  private EtatCommande etatCommande;

  @ManyToOne
  @JoinColumn(name = "entrepriseiId")
  private Entreprise entreprise;

  @ManyToOne
  @JoinColumn(name = "idclient")
  private Client client;

  @OneToMany(mappedBy = "commandeClient")
  private List<LigneCommandeClient> ligneCommandeClients;

}
