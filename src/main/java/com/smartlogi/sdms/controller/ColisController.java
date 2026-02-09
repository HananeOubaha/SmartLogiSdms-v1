package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.DTO.ColisCreationDto;
import com.smartlogi.sdms.DTO.ColisDto;
import com.smartlogi.sdms.enums.StatutColis;
import com.smartlogi.sdms.service.ColisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // ✅ Import 1
import org.springframework.security.core.context.SecurityContextHolder; // ✅ Import 2
import org.springframework.web.bind.annotation.*;
import com.smartlogi.sdms.repository.UserRepository;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/colis")
@RequiredArgsConstructor
@Tag(name = "E. Gestion du Flux des Colis", description = "Endpoints sécurisés par rôles.")
public class ColisController {

    private final ColisService colisService;
    private final UserRepository userRepository; // ✅ Darouri bach n-jibou ID

    // ============================================
    // 1. ESPACE CLIENT (ROLE_CLIENT)
    // ============================================

    @Operation(summary = "Crée une demande de colis (Réservé au Client)")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<ColisDto> createColis(@Valid @RequestBody ColisCreationDto creationDto) {
        ColisDto createdColis = colisService.createColis(creationDto);
        return new ResponseEntity<>(createdColis, HttpStatus.CREATED);
    }

    // 👇👇 NOUVELLE METHODE POUR REGLER TON PROBLEME 👇👇
    @Operation(summary = "Récupère UNIQUEMENT les colis du client connecté")
    @GetMapping("/mes-colis")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<List<ColisDto>> getMesColis() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        var user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Hna convertiti l'ID l'String ✅
        String userIdString = String.valueOf(user.getId());

        // 👇 CORRECTION HNA: Sta3mli userIdString (String) machi user.getId() (Long)
        return ResponseEntity.ok(colisService.getColisByClientId(userIdString));
    }

    // ============================================
    // 2. ESPACE GESTIONNAIRE (ROLE_MANAGER)
    // ============================================

    @Operation(summary = "Récupère tous les colis (Réservé au Gestionnaire)")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<ColisDto>> getAllColis() {
        return ResponseEntity.ok(colisService.getAllColis());
    }

    @Operation(summary = "Supprime un colis (Réservé au Gestionnaire)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ResponseEntity<Void> deleteColis(@PathVariable String id) {
        colisService.deleteColis(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Assigne un colis à un livreur (Réservé au Gestionnaire)")
    @PutMapping("/assigner/{colisId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
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
    @PreAuthorize("hasRole('ROLE_DELIVERYMAN')")
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

    @Operation(summary = "Récupère un colis par son ID (Suivi Public)")
    @GetMapping("/{id}")
    public ResponseEntity<ColisDto> getColisById(@PathVariable String id) {
        return ResponseEntity.ok(colisService.getColisById(id));
    }
}