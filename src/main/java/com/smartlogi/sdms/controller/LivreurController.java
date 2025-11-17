package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.DTO.LivreurDto;
import com.smartlogi.sdms.service.LivreurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// Suppression de l'import java.util.UUID

import java.util.List;

@RestController
@RequestMapping("/api/livreurs")
@RequiredArgsConstructor
@Tag(name = "D. Gestion des Livreurs", description = "Endpoints pour la gestion des employés de livraison.")
public class LivreurController {

    private final LivreurService livreurService;

    // POST /api/livreurs
    @Operation(summary = "Crée un nouveau livreur")
    @ApiResponse(responseCode = "201", description = "Livreur créé avec succès")
    @ApiResponse(responseCode = "400", description = "Validation échouée")
    @PostMapping
    public ResponseEntity<LivreurDto> createLivreur(@Valid @RequestBody LivreurDto livreurDto) {
        LivreurDto createdLivreur = livreurService.createLivreur(livreurDto);
        return new ResponseEntity<>(createdLivreur, HttpStatus.CREATED);
    }

    // GET /api/livreurs
    @Operation(summary = "Récupère tous les livreurs")
    @ApiResponse(responseCode = "200", description = "Liste des livreurs retournée")
    @GetMapping
    public ResponseEntity<List<LivreurDto>> getAllLivreurs() {
        List<LivreurDto> livreurs = livreurService.getAllLivreurs();
        return ResponseEntity.ok(livreurs);
    }

    // GET /api/livreurs/{id}
    @Operation(summary = "Récupère un livreur par son ID")
    @ApiResponse(responseCode = "200", description = "Livreur trouvé")
    @ApiResponse(responseCode = "404", description = "Livreur non trouvé")
    @GetMapping("/{id}")
    // CORRECTION : id doit être String
    public ResponseEntity<LivreurDto> getLivreurById(@PathVariable String id) {
        LivreurDto livreurDto = livreurService.getLivreurById(id);
        return ResponseEntity.ok(livreurDto);
    }

    // PUT /api/livreurs/{id}
    @Operation(summary = "Met à jour un livreur existant")
    @ApiResponse(responseCode = "200", description = "Livreur mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Livreur non trouvé")
    @PutMapping("/{id}")
    // CORRECTION : id doit être String
    public ResponseEntity<LivreurDto> updateLivreur(@PathVariable String id, @Valid @RequestBody LivreurDto livreurDto) {
        LivreurDto updatedLivreur = livreurService.updateLivreur(id, livreurDto);
        return ResponseEntity.ok(updatedLivreur);
    }

    // DELETE /api/livreurs/{id}
    @Operation(summary = "Supprime un livreur par son ID")
    @ApiResponse(responseCode = "204", description = "Livreur supprimé (No Content)")
    @ApiResponse(responseCode = "404", description = "Livreur non trouvé")
    @DeleteMapping("/{id}")
    // CORRECTION : id doit être String
    public ResponseEntity<Void> deleteLivreur(@PathVariable String id) {
        livreurService.deleteLivreur(id);
        return ResponseEntity.noContent().build();
    }
}