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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/clients")
@Tag(name = "Clients Controller", description = "Opérations liées à la gestion des clients")
@RequiredArgsConstructor
public class ClientController {

        private final ClientService clientService;

        @Operation(summary = "ADMIN ou MANAGERS: Créer un client", description = "Permet d'enregistrer un nouveau client pour une entreprise donnée. Accessible aux ADMIN et MANAGERS.", parameters = {
                        @Parameter(name = "entrepriseId", description = "Identifiant de l'entreprise à laquelle le client sera rattaché", required = true, example = "1")
        }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Représentation JSON du client à créer", required = true, content = @Content(mediaType = "application/json", examples = {
                        @ExampleObject(name = "Exemple Cameroun", summary = "Client situé au Cameroun", value = "{\n" +
                                        "  \"nom\": \"Mbarga\",\n" +
                                        "  \"prenom\": \"Clarisse\",\n" +
                                        "  \"adresse\": {\n" +
                                        "    \"adresse1\": \"Quartier Bastos\",\n" +
                                        "    \"adresse2\": \"Immeuble Socrate\",\n" +
                                        "    \"ville\": \"Yaoundé\",\n" +
                                        "    \"codePostale\": \"237\",\n" +
                                        "    \"pays\": \"Cameroun\"\n" +
                                        "  },\n" +
                                        "  \"photo\": \"clarisse.jpg\",\n" +
                                        "  \"mail\": \"clarisse.mbarga@exemple.cm\",\n" +
                                        "  \"numTel\": \"+237 670123456\"\n" +
                                        "}")
        })))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
                        @ApiResponse(responseCode = "400", description = "Données de validation invalides"),
                        @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
        })
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @PostMapping("/create/entreprises/{entrepriseId}")
        public ResponseEntity<ClientDTO> createClient(
                        @PathVariable Long entrepriseId,
                        @Valid @RequestBody ClientDTO clientDTO) {
                ClientDTO savedClient = clientService.createClient(entrepriseId, clientDTO);
                return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
        }

        @Operation(summary = "MANAGER ou ADMIN: Rechercher un client par ID", description = "Recherche un client à partir de son identifiant. Accessible aux MANAGER ou ADMIN.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Client trouvé"),
                        @ApiResponse(responseCode = "404", description = "Client non trouvé")
        })
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @GetMapping("/{id}")
        public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
                ClientDTO clientDTO = clientService.findByClientId(id);
                return ResponseEntity.ok(clientDTO);
        }

        @Operation(summary = "MANAGERS ou ADMIN: Liste des clients", description = "Retourne tous les clients enregistrés. Accessible aux MANAGERS ou ADMIN.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès")
        })
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @GetMapping("/all")
        public ResponseEntity<List<ClientDTO>> getAllClients() {
                List<ClientDTO> clients = clientService.getAllClients();
                return ResponseEntity.ok(clients);
        }

        @Operation(summary = "ADMIN ou MANAGERS: Supprimer un client", description = "Permet de supprimer un client grâce à son ID. Accessible aux ADMIN et MANAGERS.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Client supprimé avec succès"),
                        @ApiResponse(responseCode = "404", description = "Client non trouvé")
        })
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ClientDTO> deleteClient(@PathVariable Long id) {
                ClientDTO deletedClient = clientService.deleteClient(id);
                return ResponseEntity.ok(deletedClient);
        }

        @Operation(summary = "MANAGERS ou ADMIN: Modifier un client", description = "Permet de modifier les informations d'un client. Accessible aux MANAGERS et ADMIN.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Client modifié avec succès"),
                        @ApiResponse(responseCode = "404", description = "Client non trouvé"),
                        @ApiResponse(responseCode = "400", description = "Données de validation invalides")
        })
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @PutMapping("/{id}")
        public ResponseEntity<ClientDTO> updateClient(
                        @PathVariable Long id,
                        @Valid @RequestBody ClientDTO clientDTO) {
                ClientDTO updatedClient = clientService.updateClient(id, clientDTO);
                return ResponseEntity.ok(updatedClient);
        }

        @Operation(summary = "MANAGERS ou ADMIN: Clients par entreprise", description = "Retourne tous les clients d'une entreprise donnée. Accessible aux MANAGERS et ADMIN.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste des clients de l'entreprise récupérée avec succès"),
                        @ApiResponse(responseCode = "404", description = "Entreprise non trouvée")
        })
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STOCK_MANAGER', 'ROLE_SALES_MANAGER')")
        @GetMapping("/entreprise/{entrepriseId}")
        public ResponseEntity<List<ClientDTO>> getClientsByEntreprise(@PathVariable Long entrepriseId) {
                List<ClientDTO> clients = clientService.findByEntreprise(entrepriseId);
                return ResponseEntity.ok(clients);
        }
}
