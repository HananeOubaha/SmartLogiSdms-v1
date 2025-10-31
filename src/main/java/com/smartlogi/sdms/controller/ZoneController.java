package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.dto.ZoneDto;
import com.smartlogi.sdms.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public ResponseEntity<ZoneDto> createZone(@Valid @RequestBody ZoneDto zoneDto) {
        ZoneDto createdZone = zoneService.createZone(zoneDto);
        return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
    }

    // GET /api/zones
    @GetMapping
    public ResponseEntity<List<ZoneDto>> getAllZones() {
        List<ZoneDto> zones = zoneService.getAllZones();
        return ResponseEntity.ok(zones);
    }

    // GET /api/zones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ZoneDto> getZoneById(@PathVariable Long id) {
        ZoneDto zoneDto = zoneService.getZoneById(id);
        return ResponseEntity.ok(zoneDto);
    }

    // PUT /api/zones/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ZoneDto> updateZone(@PathVariable Long id, @Valid @RequestBody ZoneDto zoneDto) {
        ZoneDto updatedZone = zoneService.updateZone(id, zoneDto);
        return ResponseEntity.ok(updatedZone);
    }

    // DELETE /api/zones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }
}