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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/colis")
@RequiredArgsConstructor
@Tag(name = "E. Gestion du Flux des Colis", description = "Endpoints principaux pour la création, l'affectation et le suivi des livraisons.")
public class ColisController {

    private final ColisService colisService;

    // ============================================
    // CRUD de Base / Création (User Story Client Expéditeur)
    // ============================================

    // POST /api/colis
    @Operation(summary = "Crée une demande de colis (début du flux)")
    @ApiResponse(responseCode = "201", description = "Colis créé avec statut 'CRÉÉ'")
    @ApiResponse(responseCode = "404", description = "Client, Destinataire ou Zone ID non trouvée")
    @PostMapping
    public ResponseEntity<ColisDto> createColis(@Valid @RequestBody ColisCreationDto creationDto) {
        ColisDto createdColis = colisService.createColis(creationDto);
        return new ResponseEntity<>(createdColis, HttpStatus.CREATED);
    }

    // GET /api/colis
    @Operation(summary = "Récupère tous les colis (pour le Gestionnaire Logistique)")
    @GetMapping
    public ResponseEntity<List<ColisDto>> getAllColis() {
        return ResponseEntity.ok(colisService.getAllColis());
    }

    // GET /api/colis/{id}
    @Operation(summary = "Récupère un colis par son ID (pour le suivi)")
    @ApiResponse(responseCode = "404", description = "Colis non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<ColisDto> getColisById(@PathVariable UUID id) {
        return ResponseEntity.ok(colisService.getColisById(id));
    }

    // DELETE /api/colis/{id}
    @Operation(summary = "Supprime un colis (Gestionnaire)")
    @ApiResponse(responseCode = "204", description = "Colis supprimé")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColis(@PathVariable UUID id) {
        colisService.deleteColis(id);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // WORKFLOW / LOGIQUE MÉTIER
    // ============================================

    // PUT /api/colis/assigner/{colisId} (User Story Gestionnaire Logistique)
    @Operation(summary = "Assigne un colis à un livreur et le passe en EN_TRANSIT")
    @ApiResponse(responseCode = "200", description = "Affectation réussie")
    @ApiResponse(responseCode = "404", description = "Colis ou Livreur ID non trouvé")
    @PutMapping("/assigner/{colisId}")
    public ResponseEntity<ColisDto> assignerLivreur(
            @PathVariable UUID colisId,
            @RequestParam UUID livreurId) {

        ColisDto updatedColis = colisService.assignerLivreur(colisId, livreurId);
        return ResponseEntity.ok(updatedColis);
    }

    // PUT /api/colis/statut/{colisId} (User Story Livreur)
    @Operation(summary = "Met à jour le statut du colis (COLLECTE, LIVRE, etc.)")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour et historique enregistré")
    @PutMapping("/statut/{colisId}")
    public ResponseEntity<ColisDto> updateStatut(
            @PathVariable UUID colisId,
            @RequestParam StatutColis statut,
            @RequestParam String commentaire) {

        ColisDto updatedColis = colisService.updateStatut(colisId, StatutColis.CREE, commentaire);
        return ResponseEntity.ok(updatedColis);
    }
}