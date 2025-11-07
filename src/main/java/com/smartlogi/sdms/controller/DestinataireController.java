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
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/destinataires")
@RequiredArgsConstructor
@Tag(name = "C. Gestion des Destinataires", description = "Endpoints pour les destinataires de colis.")
public class DestinataireController {

    private final DestinataireService destinataireService;

    // POST /api/destinataires
    @Operation(summary = "Crée un nouveau destinataire")
    @ApiResponse(responseCode = "201", description = "Destinataire créé avec succès")
    @ApiResponse(responseCode = "400", description = "Validation échouée")
    @PostMapping
    public ResponseEntity<DestinataireDto> createDestinataire(@Valid @RequestBody DestinataireDto destinataireDto) {
        DestinataireDto createdDestinataire = destinataireService.createDestinataire(destinataireDto);
        return new ResponseEntity<>(createdDestinataire, HttpStatus.CREATED);
    }

    // GET /api/destinataires
    @Operation(summary = "Récupère tous les destinataires")
    @ApiResponse(responseCode = "200", description = "Liste des destinataires retournée")
    @GetMapping
    public ResponseEntity<List<DestinataireDto>> getAllDestinataires() {
        List<DestinataireDto> destinataires = destinataireService.getAllDestinataires();
        return ResponseEntity.ok(destinataires);
    }

    // GET /api/destinataires/{id}
    @Operation(summary = "Récupère un destinataire par son ID")
    @ApiResponse(responseCode = "200", description = "Destinataire trouvé")
    @ApiResponse(responseCode = "404", description = "Destinataire non trouvé")
    @GetMapping("/{id}")
    public ResponseEntity<DestinataireDto> getDestinataireById(@PathVariable UUID id) {
        DestinataireDto destinataireDto = destinataireService.getDestinataireById(id);
        return ResponseEntity.ok(destinataireDto);
    }

    // PUT /api/destinataires/{id}
    @Operation(summary = "Met à jour un destinataire existant")
    @ApiResponse(responseCode = "200", description = "Destinataire mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Destinataire non trouvé")
    @PutMapping("/{id}")
    public ResponseEntity<DestinataireDto> updateDestinataire(@PathVariable UUID id, @Valid @RequestBody DestinataireDto destinataireDto) {
        DestinataireDto updatedDestinataire = destinataireService.updateDestinataire(id, destinataireDto);
        return ResponseEntity.ok(updatedDestinataire);
    }

    // DELETE /api/destinataires/{id}
    @Operation(summary = "Supprime un destinataire par son ID")
    @ApiResponse(responseCode = "204", description = "Destinataire supprimé (No Content)")
    @ApiResponse(responseCode = "404", description = "Destinataire non trouvé")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestinataire(@PathVariable UUID id) {
        destinataireService.deleteDestinataire(id);
        return ResponseEntity.noContent().build();
    }
}