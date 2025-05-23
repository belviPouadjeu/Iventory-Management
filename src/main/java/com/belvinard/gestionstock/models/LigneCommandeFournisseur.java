package com.belvinard.gestionstock.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "ligneCommandeFournisseurs")
public class LigneCommandeFournisseur extends AbstractEntity {

    @NotNull(message = "Le prix unitaire HT est obligatoire")
    @DecimalMin(value = "1.0", message = "Le prix unitaire HT doit être au moins 1")
    private BigDecimal prixUnitaireHt;

    @DecimalMin(value = "0.0", message = "Le taux de TVA ne peut pas être négatif")
    private BigDecimal tauxTva;

    @DecimalMin(value = "1.0", message = "Le prix unitaire TTC doit être au moins 1")
    private BigDecimal prixUnitaireTtc;

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.1", message = "La quantité doit être supérieure à 0")
    private BigDecimal quantiteCommande;

    private String photo;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private CommandeFournisseur commandeFournisseur;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @PrePersist
    @PreUpdate
    public void calculerPrixTtc() {
        if (prixUnitaireHt != null && tauxTva != null) {
            this.prixUnitaireTtc = prixUnitaireHt.add(prixUnitaireHt.multiply(tauxTva).divide(BigDecimal.valueOf(100)));
        }
    }
}