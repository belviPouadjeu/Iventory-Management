package com.belvinard.gestionstock.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank
    @Size(min = 2, max = 100)
    private String prenom;

    @NotNull
    private LocalDateTime dateDeNaissance;

    @NotNull
    private Long entrepriseId;

    @Setter
    @Getter
    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}
