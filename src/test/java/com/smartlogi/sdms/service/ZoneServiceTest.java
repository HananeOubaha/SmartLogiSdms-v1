package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ZoneDto;
import com.smartlogi.sdms.mapper.ZoneMapper;
import com.smartlogi.sdms.model.Zone;
import com.smartlogi.sdms.repository.ZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitaires pour ZoneService.
 */
public class ZoneServiceTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private ZoneMapper zoneMapper;

    @InjectMocks
    private ZoneService zoneService;

    private String testId;
    private Zone mockEntity;
    private ZoneDto mockDto;
    private ZoneDto updateDto;
    private ZoneDto createdDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testId = java.util.UUID.randomUUID().toString();

        // DTO initial
        mockDto = new ZoneDto(
                null, "Casablanca Ouest", "21000"
        );

        // Entité mockée avec l'ID simulé
        mockEntity = new Zone(
                testId,
                "Casablanca Ouest",
                "21000",
                null
        );

        // DTO attendu
        createdDto = new ZoneDto(
                testId, "Casablanca Ouest", "21000"
        );

        // DTO pour la mise à jour
        updateDto = new ZoneDto(
                testId, "Casablanca Anfa", "20000"
        );
    }

    // =================================================================
    // 1. TESTS CREATE (Création)
    // =================================================================

    @Test
    void createZone_ShouldReturnCreatedZone() {
        // GIVEN
        when(zoneRepository.save(any(Zone.class))).thenReturn(mockEntity);
        when(zoneMapper.toEntity(any(ZoneDto.class))).thenReturn(mockEntity);
        when(zoneMapper.toDto(mockEntity)).thenReturn(createdDto);

        // WHEN
        ZoneDto result = zoneService.createZone(mockDto);

        // THEN
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Casablanca Ouest", result.getNom());
        verify(zoneRepository, times(1)).save(any(Zone.class));
    }

    // =================================================================
    // 2. TESTS READ (Lecture)
    // =================================================================

    @Test
    void getZoneById_ShouldReturnZoneDto_WhenFound() {
        // GIVEN
        when(zoneRepository.findById(testId)).thenReturn(Optional.of(mockEntity));
        when(zoneMapper.toDto(mockEntity)).thenReturn(createdDto);

        // WHEN
        ZoneDto result = zoneService.getZoneById(testId);

        // THEN
        assertNotNull(result);
        assertEquals(testId, result.getId());
    }

    @Test
    void getZoneById_ShouldThrowException_WhenNotFound() {
        // GIVEN: L'ID n'est pas trouvé
        when(zoneRepository.findById(testId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(EntityNotFoundException.class, () -> {
            zoneService.getZoneById(testId);
        });
    }

    @Test
    void getAllZones_ShouldReturnListOfDtos() {
        // GIVEN
        List<Zone> entityList = Arrays.asList(mockEntity, mockEntity);
        List<ZoneDto> dtoList = Arrays.asList(createdDto, createdDto);

        when(zoneRepository.findAll()).thenReturn(entityList);
        when(zoneMapper.toDto(entityList)).thenReturn(dtoList);

        // WHEN
        List<ZoneDto> result = zoneService.getAllZones();

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(zoneRepository, times(1)).findAll();
    }

    // =================================================================
    // 3. TESTS UPDATE (Mise à Jour)
    // =================================================================

    @Test
    void updateZone_ShouldUpdateZone_WhenFound() {
        // GIVEN:
        when(zoneRepository.findById(testId)).thenReturn(Optional.of(mockEntity));
        when(zoneRepository.save(any(Zone.class))).thenReturn(mockEntity);
        when(zoneMapper.toDto(mockEntity)).thenReturn(updateDto);

        // WHEN
        ZoneDto result = zoneService.updateZone(testId, updateDto);

        // THEN
        assertNotNull(result);
        // Vérifie que le nom a été mis à jour sur l'entité mockée
        assertEquals("Casablanca Anfa", mockEntity.getNom());
        verify(zoneRepository, times(1)).save(mockEntity);
    }

    @Test
    void updateZone_ShouldThrowNotFound_WhenZoneNotFound() {
        // GIVEN: findById retourne vide
        when(zoneRepository.findById(testId)).thenReturn(Optional.empty());

        // WHEN & THEN: Vérifie l'exception 404
        assertThrows(EntityNotFoundException.class, () -> {
            zoneService.updateZone(testId, updateDto);
        });
        verify(zoneRepository, never()).save(any());
    }

    // =================================================================
    // 4. TESTS DELETE (Suppression)
    // =================================================================

    @Test
    void deleteZone_ShouldSucceed_WhenFound() {
        // GIVEN: L'entité existe
        when(zoneRepository.existsById(testId)).thenReturn(true);

        // WHEN: Appel de la suppression
        zoneService.deleteZone(testId);

        // THEN: Vérifie que deleteById a été appelé une fois
        verify(zoneRepository, times(1)).deleteById(testId);
    }

    @Test
    void deleteZone_ShouldThrowException_WhenNotFound() {
        // GIVEN: L'entité n'existe pas
        when(zoneRepository.existsById(testId)).thenReturn(false);

        // WHEN & THEN: Vérifie l'exception 404
        assertThrows(EntityNotFoundException.class, () -> {
            zoneService.deleteZone(testId);
        });

        // THEN: Vérifie que deleteById n'a jamais été appelé
        verify(zoneRepository, never()).deleteById(anyString());
    }

    // =================================================================
    // 5. TEST UTILITY (Utilisé par ColisService pour vérifier la FK)
    // =================================================================

    @Test
    void getZoneEntityById_ShouldReturnEntity_WhenFound() {
        // GIVEN
        when(zoneRepository.findById(testId)).thenReturn(Optional.of(mockEntity));

        // WHEN
        Zone result = zoneService.getZoneEntityById(testId);

        // THEN
        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(zoneRepository, times(1)).findById(testId);
    }

    @Test
    void getZoneEntityById_ShouldThrowException_WhenNotFound() {
        // GIVEN: L'ID n'est pas trouvé
        when(zoneRepository.findById(testId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(EntityNotFoundException.class, () -> {
            zoneService.getZoneEntityById(testId);
        });
    }
}