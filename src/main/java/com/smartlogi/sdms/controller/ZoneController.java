package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.DTO.ZoneDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.smartlogi.sdms.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import java.util.List;

/**
 * Contrôleur REST pour l'entité Zone.
 * Point d'entrée pour les requêtes HTTP.
 */
@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    // POST /api/zones
    @Operation(summary = "Crée une nouvelle zone de livraison") // <-- Opération
    @ApiResponse(responseCode = "201", description = "Zone créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données d'entrée invalides (validation)")
    @PostMapping
    public ResponseEntity<ZoneDto> createZone(@Valid @RequestBody ZoneDto zoneDto) {
        ZoneDto createdZone = zoneService.createZone(zoneDto);
        return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
    }

    // GET /api/zones
    @Operation(summary = "Récupère toutes les zones")
    @ApiResponse(responseCode = "200", description = "Liste des zones retournée")
    @GetMapping
    public ResponseEntity<List<ZoneDto>> getAllZones() {
        List<ZoneDto> zones = zoneService.getAllZones();
        return ResponseEntity.ok(zones);
    }

    // GET /api/zones/{id}
    @Operation(summary = "Récupère une zone par son ID")
    @ApiResponse(responseCode = "200", description = "Zone trouvée")
    @ApiResponse(responseCode = "404", description = "Zone non trouvée")
    @GetMapping("/{id}")
    public ResponseEntity<ZoneDto> getZoneById(@PathVariable UUID id) {
        ZoneDto zoneDto = zoneService.getZoneById(id);
        return ResponseEntity.ok(zoneDto);
    }

    // PUT /api/zones/{id}
    @Operation(summary = "Met à jour une zone existante")
    @ApiResponse(responseCode = "200", description = "Zone mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Zone non trouvée")
    @PutMapping("/{id}")
    public ResponseEntity<ZoneDto> updateZone(@PathVariable UUID id, @Valid @RequestBody ZoneDto zoneDto) {
        ZoneDto updatedZone = zoneService.updateZone(id, zoneDto);
        return ResponseEntity.ok(updatedZone);
    }

    // DELETE /api/zones/{id}
    @Operation(summary = "Supprime une zone par son ID")
    @ApiResponse(responseCode = "204", description = "Zone supprimée (No Content)")
    @ApiResponse(responseCode = "404", description = "Zone non trouvée")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable UUID id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }
}