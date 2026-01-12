package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.DTO.ZoneDto;
import com.smartlogi.sdms.service.ZoneService;
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

/**
 * Contrôleur REST pour l'entité Zone sécurisé par rôles.
 */
@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_MANAGER')") // Seul le gestionnaire peut manipuler les zones
@Tag(name = "A. Gestion des Zones", description = "Endpoints réservés au Gestionnaire pour configurer le réseau logistique.")
public class ZoneController {

    private final ZoneService zoneService;

    @Operation(summary = "Crée une nouvelle zone (Gestionnaire uniquement)")
    @PostMapping
    public ResponseEntity<ZoneDto> createZone(@Valid @RequestBody ZoneDto zoneDto) {
        ZoneDto createdZone = zoneService.createZone(zoneDto);
        return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
    }

    @Operation(summary = "Récupère toutes les zones (Gestionnaire uniquement)")
    @GetMapping
    public ResponseEntity<List<ZoneDto>> getAllZones() {
        List<ZoneDto> zones = zoneService.getAllZones();
        return ResponseEntity.ok(zones);
    }

    @Operation(summary = "Récupère une zone par ID (Gestionnaire uniquement)")
    @GetMapping("/{id}")
    public ResponseEntity<ZoneDto> getZoneById(@PathVariable String id) {
        ZoneDto zoneDto = zoneService.getZoneById(id);
        return ResponseEntity.ok(zoneDto);
    }

    @Operation(summary = "Met à jour une zone (Gestionnaire uniquement)")
    @PutMapping("/{id}")
    public ResponseEntity<ZoneDto> updateZone(@PathVariable String id, @Valid @RequestBody ZoneDto zoneDto) {
        ZoneDto updatedZone = zoneService.updateZone(id, zoneDto);
        return ResponseEntity.ok(updatedZone);
    }

    @Operation(summary = "Supprime une zone (Gestionnaire uniquement)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable String id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }
}