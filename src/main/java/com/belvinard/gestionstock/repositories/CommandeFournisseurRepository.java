package com.belvinard.gestionstock.repositories;


import com.belvinard.gestionstock.models.CommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommandeFournisseurRepository extends JpaRepository<CommandeFournisseur, Long> {

    Optional<CommandeFournisseur> findByCode(String code);

    List<CommandeFournisseur> findAllByFournisseurId(Long fournisseurId);

    boolean existsByCode(String code);

    Optional<CommandeFournisseur> findByCodeIgnoreCase(String code);
}
