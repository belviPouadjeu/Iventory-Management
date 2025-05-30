package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.ClientDTO;
import com.belvinard.gestionstock.exceptions.DuplicateEntityException;
import com.belvinard.gestionstock.exceptions.ResourceNotFoundException;
import com.belvinard.gestionstock.models.Client;
import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.repositories.ClientRepository;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private final EntrepriseRepository entrepriseRepository;


    @Override
    public ClientDTO createClient(Long entrepriseId, ClientDTO clientDTO) {

        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Entreprise", "id", entrepriseId
                ));

        Optional<Client> existingClient = clientRepository.findByNomAndEntrepriseId(
                clientDTO.getNom(), entrepriseId
        );

        if (existingClient.isPresent()) {
            throw new DuplicateEntityException("Un client nommé '" + clientDTO.getNom()
                    + "' existe déjà pour cette entreprise.");
        }

        System.out.println("Client existant ? " + existingClient.isPresent());
        System.out.println("Client à enregistrer : " + clientDTO);

        Client client = modelMapper.map(clientDTO, Client.class);
        client.setEntreprise(entreprise);

        Client savedClient = clientRepository.save(client);

        ClientDTO savedDTO = modelMapper.map(savedClient, ClientDTO.class);
        savedDTO.setEntrepriseId(entrepriseId);
        savedDTO.setEntrepriseName(entreprise.getNom()); // 👈 Ajout ici

        return savedDTO;
    }




    @Override
    public ClientDTO findByClientId(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client", "id", id
                ));


        ClientDTO clientDTO = modelMapper.map(client, ClientDTO.class);

        if (client.getEntreprise() != null) {
            clientDTO.setEntrepriseId(client.getEntreprise().getId());
        }

        return clientDTO;
    }


    @Override
    public List<ClientDTO> getAllClients() {
        List<Client> clients = clientRepository.findAll();

        return clients.stream()
                .map(client -> {
                    ClientDTO clientDTO = modelMapper.map(client, ClientDTO.class);

                    if (client.getEntreprise() != null) {
                        clientDTO.setEntrepriseId(client.getEntreprise().getId());
                        clientDTO.setEntrepriseName(client.getEntreprise().getNom());
                    }

                    return clientDTO ;
                })
                .collect(Collectors.toList());
    }


    @Override
    public ClientDTO deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun client trouvé avec l'ID : " + id));

        if (client.getCommandeClients() != null && !client.getCommandeClients().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer ce client car il est lié à des commandes.");
        }

        clientRepository.delete(client);

        ClientDTO deletedClientDTO = modelMapper.map(client, ClientDTO.class);
        if (client.getEntreprise() != null) {
            deletedClientDTO.setEntrepriseId(client.getEntreprise().getId());
            deletedClientDTO.setEntrepriseName(client.getEntreprise().getNom());
        }

        return deletedClientDTO;
    }



}
