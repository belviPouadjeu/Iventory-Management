package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByNomAndEntrepriseId(String nom, Long entrepriseId);


    List<Client> findByEntrepriseId(Long entrepriseId);
}
