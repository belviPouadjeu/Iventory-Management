package com.belvinard.gestionstock.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "utilisateur")
public class Utilisateur extends AbstractEntity {

  @NotBlank(message = "Le nom est obligatoire")
  @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
  private String nom;

  @NotBlank(message = "Le prénom est obligatoire")
  @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
  private String prenom;

  @NotBlank(message = "L'email est obligatoire")
  @Email(message = "L'adresse email est invalide")
  private String email;

  @NotNull(message = "La date de naissance est obligatoire")
  private LocalDateTime dateDeNaissance;

  @NotBlank(message = "Le mot de passe est obligatoire")
  @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
  private String moteDePasse;

  @Embedded
  private Adresse adresse;

  @Column(name = "photo")
  private String photo;

  @NotNull(message = "L'entreprise est obligatoire")
  @ManyToOne
  @JoinColumn(name = "identreprise")
  private Entreprise entreprise;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "utilisateur")
  @JsonIgnore
  private List<Roles> roles;
}
