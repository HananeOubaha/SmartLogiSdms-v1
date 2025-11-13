package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ZoneDto;
import com.smartlogi.sdms.mapper.ZoneMapper;
import com.smartlogi.sdms.model.Zone;
import com.smartlogi.sdms.repository.ZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
// Suppression de l'import java.util.UUID

import java.util.List;
// Suppression de l'import java.util.UUID car il est remplacé par String
import java.util.stream.Collectors;

/**
 * Service pour la gestion des opérations CRUD sur l'entité Zone.
 */
@Service
@RequiredArgsConstructor // Injecte automatiquement les dépendances finales (Repository et Mapper)
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;

    /**
     * Crée une nouvelle Zone.
     * @param zoneDto Le DTO contenant les données de la nouvelle zone.
     * @return Le DTO de la zone créée avec son ID.
     */
    public ZoneDto createZone(ZoneDto zoneDto) {
        Zone zone = zoneMapper.toEntity(zoneDto);
        // L'ID String est généré par @PrePersist dans l'Entité
        Zone savedZone = zoneRepository.save(zone);
        return zoneMapper.toDto(savedZone);
    }

    /**
     * Récupère toutes les Zones.
     * @return Une liste de ZoneDto.
     */
    public List<ZoneDto> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(zoneMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une Zone par son ID.
     * @param id L'ID de la zone (String).
     * @return Le DTO de la zone.
     * @throws EntityNotFoundException si la zone n'est pas trouvée.
     */
    // CORRECTION : id doit être String
    public ZoneDto getZoneById(String id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l'ID: " + id));
        return zoneMapper.toDto(zone);
    }

    /**
     * Met à jour une Zone existante.
     * @param id L'ID de la zone à mettre à jour (String).
     * @param zoneDto Le DTO contenant les nouvelles données.
     * @return Le DTO de la zone mise à jour.
     * @throws EntityNotFoundException si la zone n'est pas trouvée.
     */
    // CORRECTION : id doit être String
    public ZoneDto updateZone(String id, ZoneDto zoneDto) {
        // 1. Vérifie si l'entité existe
        Zone existingZone = zoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l'ID: " + id));

        // 2. Met à jour les champs
        existingZone.setNom(zoneDto.getNom());
        existingZone.setCodePostal(zoneDto.getCodePostal());

        // 3. Sauvegarde et retourne
        Zone updatedZone = zoneRepository.save(existingZone);
        return zoneMapper.toDto(updatedZone);
    }

    /**
     * Supprime une Zone par son ID.
     * @param id L'ID de la zone à supprimer (String).
     * @throws EntityNotFoundException si la zone n'est pas trouvée.
     */
    // CORRECTION : id doit être String
    public void deleteZone(String id) {
        if (!zoneRepository.existsById(id)) {
            throw new EntityNotFoundException("Zone non trouvée avec l'ID: " + id);
        }
        zoneRepository.deleteById(id);
    }

    /**
     * Récupère l'entité Zone pour validation (utilisé par ColisService).
     * @param zoneId L'ID de la zone (String).
     * @return L'entité Zone.
     * @throws EntityNotFoundException si la zone n'est pas trouvée.
     */
    // CORRECTION : zoneId doit être String
    public Zone getZoneEntityById(@NotNull(message = "L'ID de la zone est obligatoire") String zoneId) {
        return zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l'ID: " + zoneId));
    }
}