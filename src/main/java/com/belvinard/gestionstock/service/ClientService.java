package com.belvinard.gestionstock.service;

import com.belvinard.gestionstock.dto.ClientDTO;

import java.util.List;

public interface ClientService {
    ClientDTO createClient(Long entrepriseId, ClientDTO clientDTO);

    ClientDTO findByClientId(Long id);

    List<ClientDTO> getAllClients();

    ClientDTO deleteClient(Long id);

    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    
    List<ClientDTO> findByEntreprise(Long entrepriseId);
}
