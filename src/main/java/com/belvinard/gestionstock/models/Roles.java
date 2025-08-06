package com.belvinard.gestionstock.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "roles")
@Builder
public class Roles extends AbstractEntity {
  @Enumerated(EnumType.STRING)
  @Column(name = "role_type", nullable = false)
  @NotNull(message = "Le type de rôle est obligatoire")
  private RoleType roleType;

  @NotBlank(message = "Le nom du rôle est obligatoire")
  @Size(min = 4, max = 50, message = "Le nom du rôle doit contenir entre 4 et 50 caractères")
  private String roleName;

  @ManyToOne
  @JoinColumn(name = "idutilisateur")
  private Utilisateur utilisateur;
}