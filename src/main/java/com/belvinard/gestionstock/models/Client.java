package com.belvinard.gestionstock.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "client")
public class Client extends AbstractEntity {

  @NotBlank(message = "Le nom du client est obligatoire")
  @Size(min = 5, max = 100, message = "Le nom doit contenir entre 5 et 100 caractères")
  private String nom;

  @NotBlank(message = "Le prénom du client est obligatoire")
  @Size(min = 4, max = 100, message = "Le prénom doit contenir 4 et 100 caractères")
  private String prenom;

  @Embedded
  private Adresse adresse;

  @Pattern(
          regexp = ".*\\.(jpg|jpeg|png|gif|bmp)$",
          message = "Le nom du fichier photo doit être une image (jpg, jpeg, png, gif, bmp)"
  )
  private String photo;

  @NotBlank(message = "L'Email du client est obligatoire")
  @Email(message = "L'adresse email est invalide")
  private String mail;

  @Pattern(
          regexp = "^[0-9+\\s().-]{6,20}$",
          message = "Le numéro de téléphone doit contenir entre 6 et 20 caractères " +
                  "et peut inclure des chiffres, espaces, +, (), ou -"
  )
  private String numTel;

  @ManyToOne
  @JoinColumn(name = "entrepriseId")
  private Entreprise entreprise;

  @OneToMany(mappedBy = "client")
  private List<CommandeClient> commandeClients;

}
