package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ZoneDto;
import com.smartlogi.sdms.mapper.ZoneMapper;
import com.smartlogi.sdms.model.Zone;
import com.smartlogi.sdms.repository.ZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

import java.util.List;
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
     * @param id L'ID de la zone.
     * @return Le DTO de la zone.
     * @throws EntityNotFoundException si la zone n'est pas trouvée.
     */
    public ZoneDto getZoneById(UUID id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l'ID: " + id));
        return zoneMapper.toDto(zone);
    }

    /**
     * Met à jour une Zone existante.
     * @param id L'ID de la zone à mettre à jour.
     * @param zoneDto Le DTO contenant les nouvelles données.
     * @return Le DTO de la zone mise à jour.
     * @throws EntityNotFoundException si la zone n'est pas trouvée.
     */
    public ZoneDto updateZone(UUID id, ZoneDto zoneDto) {
        // 1. Vérifie si l'entité existe
        Zone existingZone = zoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l'ID: " + id));

        // 2. Met à jour les champs
        existingZone.setNom(zoneDto.getNom());
        existingZone.setCodePostal(zoneDto.getCodePostal());
        // L'ID n'est pas modifié

        // 3. Sauvegarde et retourne
        Zone updatedZone = zoneRepository.save(existingZone);
        return zoneMapper.toDto(updatedZone);
    }

    /**
     * Supprime une Zone par son ID.
     * @param id L'ID de la zone à supprimer.
     * @throws EntityNotFoundException si la zone n'est pas trouvée.
     */
    public void deleteZone(UUID id) {
        if (!zoneRepository.existsById(id)) {
            throw new EntityNotFoundException("Zone non trouvée avec l'ID: " + id);
        }
        zoneRepository.deleteById(id);
    }

    public Zone getZoneEntityById(@NotNull(message = "L'ID de la zone est obligatoire") UUID zoneId) {
        return zoneRepository.findById(zoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone non trouvée avec l'ID: " + zoneId));
    }
}