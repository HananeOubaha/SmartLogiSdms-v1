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
import org.springframework.security.access.prepost.PreAuthorize; // Import obligatoire
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livreurs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_MANAGER')")
@Tag(name = "D. Gestion des Livreurs", description = "Endpoints réservés au Gestionnaire pour la gestion des employés.")
public class LivreurController {

    private final LivreurService livreurService;

    @Operation(summary = "Crée un nouveau livreur (Admin/Manager)")
    @PostMapping
    public ResponseEntity<LivreurDto> createLivreur(@Valid @RequestBody LivreurDto livreurDto) {
        LivreurDto createdLivreur = livreurService.createLivreur(livreurDto);
        return new ResponseEntity<>(createdLivreur, HttpStatus.CREATED);
    }

    @Operation(summary = "Récupère tous les livreurs (Admin/Manager)")
    @GetMapping
    public ResponseEntity<List<LivreurDto>> getAllLivreurs() {
        List<LivreurDto> livreurs = livreurService.getAllLivreurs();
        return ResponseEntity.ok(livreurs);
    }

    @Operation(summary = "Récupère un livreur par son ID (Admin/Manager)")
    @GetMapping("/{id}")
    public ResponseEntity<LivreurDto> getLivreurById(@PathVariable String id) {
        LivreurDto livreurDto = livreurService.getLivreurById(id);
        return ResponseEntity.ok(livreurDto);
    }

    @Operation(summary = "Met à jour un livreur (Admin/Manager)")
    @PutMapping("/{id}")
    public ResponseEntity<LivreurDto> updateLivreur(@PathVariable String id, @Valid @RequestBody LivreurDto livreurDto) {
        LivreurDto updatedLivreur = livreurService.updateLivreur(id, livreurDto);
        return ResponseEntity.ok(updatedLivreur);
    }

    @Operation(summary = "Supprime un livreur (Admin/Manager)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivreur(@PathVariable String id) {
        livreurService.deleteLivreur(id);
        return ResponseEntity.noContent().build();
    }
}