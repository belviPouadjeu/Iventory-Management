package com.belvinard.gestionstock.dto;

import com.belvinard.gestionstock.models.EtatCommande;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeFournisseurDTO {

    @Schema(hidden = true)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    @NotBlank(message = "Le code de la commande est obligatoire")
    @Size(min = 1, max = 50, message = "Le code de la commande doit avoir entre 1 et 50 caractères")
    private String code;

    //private LocalDateTime dateCommande;

    @NotNull(message = "L'état de la commande est obligatoire")
    private EtatCommande etatCommande;

    @Schema(hidden = true)
    private LocalDateTime creationDate;

    @Schema(hidden = true)
    private LocalDateTime lastModifiedDate;

    @Schema(hidden = true)
    private Long fournisseurId;

    // Ce champ est automatiquement rempli côté backend
    @Schema(hidden = true)
    private FournisseurDTO fournisseurDetails;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

//    public LocalDateTime getDateCommande() {
//        return dateCommande;
//    }

//    public void setDateCommande(LocalDateTime dateCommande) {
//        this.dateCommande = dateCommande;
//    }

    public EtatCommande getEtatCommande() {
        return etatCommande;
    }

    public void setEtatCommande(EtatCommande etatCommande) {
        this.etatCommande = etatCommande;
    }

    public FournisseurDTO getFournisseurDetails() {
        return fournisseurDetails;
    }

    public void setFournisseurDetails(FournisseurDTO fournisseurDetails) {
        this.fournisseurDetails = fournisseurDetails;
    }




}
