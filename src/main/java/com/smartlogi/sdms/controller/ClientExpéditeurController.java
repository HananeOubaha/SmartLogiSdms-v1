package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ClientExpéditeurDto;
import com.smartlogi.sdms.service.ClientExpéditeurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clients-expediteurs")
@RequiredArgsConstructor
@Tag(name = "B. Gestion des Clients Expéditeurs", description = "Endpoints pour les clients qui envoient des colis.")
public class ClientExpéditeurController {

    private final ClientExpéditeurService clientExpéditeurService;

    // POST /api/clients-expediteurs
    @Operation(summary = "Crée un nouveau client expéditeur")
    @ApiResponse(responseCode = "201", description = "Client créé avec succès")
    @ApiResponse(responseCode = "400", description = "Validation ou email déjà utilisé")
    @PostMapping
    public ResponseEntity<ClientExpéditeurDto> createClient(@Valid @RequestBody ClientExpéditeurDto clientDto) {
        ClientExpéditeurDto createdClient = clientExpéditeurService.createClient(clientDto);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    // GET /api/clients-expediteurs
    @Operation(summary = "Récupère tous les clients expéditeurs")
    @ApiResponse(responseCode = "200", description = "Liste des clients retournée")
    @GetMapping
    public ResponseEntity<List<ClientExpéditeurDto>> getAllClients() {
        List<ClientExpéditeurDto> clients = clientExpéditeurService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    // GET /api/clients-expediteurs/{id}
    @Operation(summary = "Récupère un client par son ID")
    @ApiResponse(responseCode = "200", description = "Client trouvé")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<ClientExpéditeurDto> getClientById(@PathVariable Long id) {
        ClientExpéditeurDto clientDto = clientExpéditeurService.getClientById(id);
        return ResponseEntity.ok(clientDto);
    }

    // PUT /api/clients-expediteurs/{id}
    @Operation(summary = "Met à jour un client existant")
    @ApiResponse(responseCode = "200", description = "Client mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    @PutMapping("/{id}")
    public ResponseEntity<ClientExpéditeurDto> updateClient(@PathVariable Long id, @Valid @RequestBody ClientExpéditeurDto clientDto) {
        ClientExpéditeurDto updatedClient = clientExpéditeurService.updateClient(id, clientDto);
        return ResponseEntity.ok(updatedClient);
    }

    // DELETE /api/clients-expediteurs/{id}
    @Operation(summary = "Supprime un client par son ID")
    @ApiResponse(responseCode = "204", description = "Client supprimé (No Content)")
    @ApiResponse(responseCode = "404", description = "Client non trouvé")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientExpéditeurService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}