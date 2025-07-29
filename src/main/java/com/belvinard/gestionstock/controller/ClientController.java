package com.belvinard.gestionstock.controller;


import com.belvinard.gestionstock.dto.ClientDTO;
import com.belvinard.gestionstock.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@Tag(name = "Clients-Controller", description = "Opérations liées à la gestion des clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Operation(
            summary = "Créer un client",
            description = "Cette opération permet d'enregistrer un nouveau client pour une entreprise donnée.",
            parameters = {
                    @Parameter(
                            name = "entrepriseId",
                            description = "Identifiant de l'entreprise à laquelle le client sera rattaché",
                            required = true,
                            example = "1"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Représentation JSON du client à créer",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Exemple Cameroun",
                                            summary = "Client situé au Cameroun",
                                            value = "{\n" +
                                                    "  \"nom\": \"Mbarga\",\n" +
                                                    "  \"prenom\": \"Clarisse\",\n" +
                                                    "  \"adresse\": {\n" +
                                                    "    \"adresse1\": \"Quartier Bastos\",\n" +
                                                    "    \"adresse2\": \"Immeuble Socrate\",\n" +
                                                    "    \"ville\": \"Yaoundé\",\n" +
                                                    "    \"codePostal\": \"237\",\n" +
                                                    "    \"pays\": \"Cameroun\"\n" +
                                                    "  },\n" +
                                                    "  \"photo\": \"clarisse.jpg\",\n" +
                                                    "  \"mail\": \"clarisse.mbarga@exemple.cm\",\n" +
                                                    "  \"numTel\": \"+237 670123456\"\n" +
                                                    "}"
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de validation invalides"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
    })
    @PostMapping("/entreprises/{entrepriseId}")
    public ResponseEntity<ClientDTO> createClient(
            @PathVariable Long entrepriseId,
            @Valid @RequestBody ClientDTO clientDTO
    ) {
        ClientDTO savedClient = clientService.createClient(entrepriseId, clientDTO);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
    }

    @Operation(summary = "Rechercher un client par ID",
            description = "Cette opération permet de rechercher un client grâce à son identifiant.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client trouvé"),
        @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        ClientDTO clientDTO = clientService.findByClientId(id);
        return ResponseEntity.ok(clientDTO);
    }

    @Operation(summary = "Liste des clients", description = "Cette opération retourne tous les clients enregistrés.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès")
    })
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<ClientDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @Operation(summary = "Supprimer un client",
            description = "Cette opération permet de supprimer un client grâce à son ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @DeleteMapping("admin/client/{id}")
    public ResponseEntity<ClientDTO> deleteClient(@PathVariable Long id) {
        ClientDTO deletedClient = clientService.deleteClient(id);
        return ResponseEntity.ok(deletedClient);
    }



}
