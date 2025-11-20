package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.DTO.ClientExpéditeurDto;
import com.smartlogi.sdms.service.ClientExpéditeurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import darouri
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients-expediteurs")
@RequiredArgsConstructor
@Tag(name = "B. Gestion des Clients Expéditeurs", description = "Endpoints sécurisés pour la gestion des clients.")
public class ClientExpéditeurController {

    private final ClientExpéditeurService clientExpéditeurService;

    // Création de compte (Inscription) : soit par le Gestionnaire, soit accès libre (selon ton choix)
    // Ici on autorise le Manager et l'accès anonyme si c'est pour l'inscription
    @Operation(summary = "Crée un nouveau client (Manager ou Inscription)")
    @PostMapping
    public ResponseEntity<ClientExpéditeurDto> createClient(@Valid @RequestBody ClientExpéditeurDto clientDto) {
        ClientExpéditeurDto createdClient = clientExpéditeurService.createClient(clientDto);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    // Seul le Gestionnaire peut voir tous les clients
    @Operation(summary = "Récupère tous les clients (Gestionnaire uniquement)")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<ClientExpéditeurDto>> getAllClients() {
        List<ClientExpéditeurDto> clients = clientExpéditeurService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    // Le Gestionnaire voit tout, le Client voit uniquement son profil
    @Operation(summary = "Récupère un client par ID (Manager ou le Client lui-même)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or #id == authentication.principal.username")
    public ResponseEntity<ClientExpéditeurDto> getClientById(@PathVariable String id) {
        ClientExpéditeurDto clientDto = clientExpéditeurService.getClientById(id);
        return ResponseEntity.ok(clientDto);
    }

    // Modification réservée au Gestionnaire ou au Client concerné
    @Operation(summary = "Met à jour un client (Manager ou le Client lui-même)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER') or #id == authentication.principal.username")
    public ResponseEntity<ClientExpéditeurDto> updateClient(@PathVariable String id, @Valid @RequestBody ClientExpéditeurDto clientDto) {
        ClientExpéditeurDto updatedClient = clientExpéditeurService.updateClient(id, clientDto);
        return ResponseEntity.ok(updatedClient);
    }

    // Suppression strictement réservée au Gestionnaire
    @Operation(summary = "Supprime un client (Gestionnaire uniquement)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<Void> deleteClient(@PathVariable String id) {
        clientExpéditeurService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}