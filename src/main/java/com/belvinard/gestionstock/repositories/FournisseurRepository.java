package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.Fournisseur;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {

    boolean existsByNomIgnoreCaseAndPrenomIgnoreCaseAndEntrepriseId(String nom, String prenom, Long entrepriseId);

    boolean existsByNomAndPrenomAndEntrepriseIdAndIdNot(
            String nom,
            String prenom,
            Long entrepriseId,
            Long fournisseurId
    );


}
