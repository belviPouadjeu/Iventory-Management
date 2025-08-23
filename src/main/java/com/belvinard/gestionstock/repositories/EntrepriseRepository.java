package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
    Entreprise findByNom(String nom);

    boolean existsByNom(String defaultCompany);
}