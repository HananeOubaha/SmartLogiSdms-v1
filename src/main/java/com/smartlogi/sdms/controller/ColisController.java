package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.DTO.ColisCreationDto;
import com.smartlogi.sdms.DTO.ColisDto;
import com.smartlogi.sdms.enums.StatutColis;
import com.smartlogi.sdms.service.ColisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import pour la sécurité des méthodes
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colis")
@RequiredArgsConstructor
@Tag(name = "E. Gestion du Flux des Colis", description = "Endpoints sécurisés par rôles pour le cycle de vie des colis.")
public class ColisController {

    private final ColisService colisService;

    // ============================================
    // 1. ESPACE CLIENT (ROLE_CLIENT)
    // ============================================

    @Operation(summary = "Crée une demande de colis (Réservé au Client)")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_CLIENT')") // Seul le client peut créer une demande
    public ResponseEntity<ColisDto> createColis(@Valid @RequestBody ColisCreationDto creationDto) {
        ColisDto createdColis = colisService.createColis(creationDto);
        return new ResponseEntity<>(createdColis, HttpStatus.CREATED);
    }

    // ============================================
    // 2. ESPACE GESTIONNAIRE (ROLE_MANAGER)
    // ============================================

    @Operation(summary = "Récupère tous les colis (Réservé au Gestionnaire)")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')") // Accès complet pour le gestionnaire
    public ResponseEntity<List<ColisDto>> getAllColis() {
        return ResponseEntity.ok(colisService.getAllColis());
    }

    @Operation(summary = "Supprime un colis (Réservé au Gestionnaire)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')") // Le gestionnaire gère la suppression
    public ResponseEntity<Void> deleteColis(@PathVariable String id) {
        colisService.deleteColis(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assigne un colis à un livreur (Réservé au Gestionnaire)")
    @PutMapping("/assigner/{colisId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')") // Gestion des affectations livreurs
    public ResponseEntity<ColisDto> assignerLivreur(
            @PathVariable String colisId,
            @RequestParam String livreurId) {
        ColisDto updatedColis = colisService.assignerLivreur(colisId, livreurId);
        return ResponseEntity.ok(updatedColis);
    }

    // ============================================
    // 3. ESPACE LIVREUR (ROLE_DELIVERYMAN)
    // ============================================

    @Operation(summary = "Met à jour le statut du colis (Réservé au Livreur)")
    @PutMapping("/statut/{colisId}")
    @PreAuthorize("hasRole('ROLE_DELIVERYMAN')") // Le livreur ne peut que modifier le statut
    public ResponseEntity<ColisDto> updateStatut(
            @PathVariable String colisId,
            @RequestParam StatutColis statut,
            @RequestParam String commentaire) {
        ColisDto updatedColis = colisService.updateStatut(colisId, statut, commentaire);
        return ResponseEntity.ok(updatedColis);
    }

    // ============================================
    // 4. ACCÈS COMMUN (Suivi de colis)
    // ============================================

    @Operation(summary = "Récupère un colis par son ID (Suivi)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_CLIENT', 'ROLE_DELIVERYMAN')") // Tout le monde peut suivre
    public ResponseEntity<ColisDto> getColisById(@PathVariable String id) {
        return ResponseEntity.ok(colisService.getColisById(id));
    }
}