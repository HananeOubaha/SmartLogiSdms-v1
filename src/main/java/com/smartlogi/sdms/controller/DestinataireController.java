package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.DTO.DestinataireDto;
import com.smartlogi.sdms.service.DestinataireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import obligatoire
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinataires")
@RequiredArgsConstructor
@Tag(name = "C. Gestion des Destinataires", description = "Endpoints sécurisés pour la gestion des destinataires de colis.")
public class DestinataireController {

    private final DestinataireService destinataireService;

    // Seul le Gestionnaire ou le Client (pour créer un envoi) peuvent créer un destinataire
    @Operation(summary = "Crée un nouveau destinataire (Gestionnaire ou Client)")
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_CLIENT')")
    public ResponseEntity<DestinataireDto> createDestinataire(@Valid @RequestBody DestinataireDto destinataireDto) {
        DestinataireDto createdDestinataire = destinataireService.createDestinataire(destinataireDto);
        return new ResponseEntity<>(createdDestinataire, HttpStatus.CREATED);
    }

    // Seul le Gestionnaire peut voir la liste complète
    @Operation(summary = "Récupère tous les destinataires (Gestionnaire uniquement)")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<DestinataireDto>> getAllDestinataires() {
        List<DestinataireDto> destinataires = destinataireService.getAllDestinataires();
        return ResponseEntity.ok(destinataires);
    }

    // Le Gestionnaire ou les autres rôles autorisés (pour vérification)
    @Operation(summary = "Récupère un destinataire par son ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_CLIENT')")
    public ResponseEntity<DestinataireDto> getDestinataireById(@PathVariable String id) {
        DestinataireDto destinataireDto = destinataireService.getDestinataireById(id);
        return ResponseEntity.ok(destinataireDto);
    }

    // Modification et Suppression réservées au Gestionnaire
    @Operation(summary = "Met à jour un destinataire (Gestionnaire uniquement)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<DestinataireDto> updateDestinataire(@PathVariable String id, @Valid @RequestBody DestinataireDto destinataireDto) {
        DestinataireDto updatedDestinataire = destinataireService.updateDestinataire(id, destinataireDto);
        return ResponseEntity.ok(updatedDestinataire);
    }

    @Operation(summary = "Supprime un destinataire (Gestionnaire uniquement)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<Void> deleteDestinataire(@PathVariable String id) {
        destinataireService.deleteDestinataire(id);
        return ResponseEntity.noContent().build();
    }
}