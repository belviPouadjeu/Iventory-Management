package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.CommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommandeClientRepository extends JpaRepository<CommandeClient, Long> {

  Optional<CommandeClient> findCommandeClientByCode(String code);

  List<CommandeClient> findAllByClientId(Long id);

  Optional<CommandeClient> findByCode(String code);
}
