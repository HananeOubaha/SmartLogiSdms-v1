package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.DestinataireDto;
import com.smartlogi.sdms.mapper.DestinataireMapper;
import com.smartlogi.sdms.model.Destinataire;
import com.smartlogi.sdms.repository.DestinataireRepository;
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
 * Tests Unitaires pour DestinataireService.
 */
public class DestinataireServiceTest {

    @Mock
    private DestinataireRepository destinataireRepository;

    @Mock
    private DestinataireMapper destinataireMapper;

    @InjectMocks
    private DestinataireService destinataireService;

    private String testId;
    private Destinataire mockEntity;
    private DestinataireDto mockDto;
    private DestinataireDto updateDto;
    private DestinataireDto createdDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testId = java.util.UUID.randomUUID().toString();

        // DTO initial
        mockDto = new DestinataireDto(
                null, "Hassani", "Samira", "samira@domicile.com", "0600000000", "12 Rue Rabat"
        );

        // Entité mockée avec l'ID simulé
        mockEntity = new Destinataire(
                testId, "Hassani", "Samira", "samira@domicile.com", "0600000000", "12 Rue Rabat", null
        );

        // DTO attendu
        createdDto = new DestinataireDto(
                testId, "Hassani", "Samira", "samira@domicile.com", "0600000000", "12 Rue Rabat"
        );

        // DTO pour la mise à jour
        updateDto = new DestinataireDto(
                testId, "Alaoui", "Fatima", "fatima.alaoui@test.com", "0711223344", "25 Av. Marrakech"
        );
    }

    // =================================================================
    // 1. TESTS CREATE (Création)
    // =================================================================

    @Test
    void createDestinataire_ShouldReturnCreatedDestinataire() {
        // GIVEN: La sauvegarde retourne l'entité avec l'ID
        when(destinataireRepository.save(any(Destinataire.class))).thenReturn(mockEntity);
        // GIVEN: Le Mapper reconvertit en DTO final
        when(destinataireMapper.toDto(mockEntity)).thenReturn(createdDto);
        when(destinataireMapper.toEntity(any(DestinataireDto.class))).thenReturn(mockEntity);
        // WHEN
        DestinataireDto result = destinataireService.createDestinataire(mockDto);

        // THEN
        assertNotNull(result);
        assertEquals(testId, result.getId());
//        assertEquals("Hassani", result.getNom());
        verify(destinataireRepository, times(1)).save(any(Destinataire.class));
    }

    // =================================================================
    // 2. TESTS READ (Lecture)
    // =================================================================

    @Test
    void getDestinataireById_ShouldReturnDestinataireDto_WhenFound() {
        // GIVEN
        when(destinataireRepository.findById(testId)).thenReturn(Optional.of(mockEntity));
        when(destinataireMapper.toDto(mockEntity)).thenReturn(createdDto);

        // WHEN
        DestinataireDto result = destinataireService.getDestinataireById(testId);

        // THEN
        assertNotNull(result);
        assertEquals(testId, result.getId());
    }

    @Test
    void getDestinataireById_ShouldThrowException_WhenNotFound() {
        // GIVEN: L'ID n'est pas trouvé
        when(destinataireRepository.findById(testId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(EntityNotFoundException.class, () -> {
            destinataireService.getDestinataireById(testId);
        });
    }

    @Test
    void getAllDestinataires_ShouldReturnListOfDtos() {
        // GIVEN
        List<Destinataire> entityList = Arrays.asList(mockEntity);
        List<DestinataireDto> dtoList = Arrays.asList(createdDto);

        when(destinataireRepository.findAll()).thenReturn(entityList);
        when(destinataireMapper.toDto(entityList)).thenReturn(dtoList);

        // WHEN
        List<DestinataireDto> result = destinataireService.getAllDestinataires();

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(destinataireRepository, times(1)).findAll();
    }

    // =================================================================
    // 3. TESTS UPDATE (Mise à Jour)
    // =================================================================

    @Test
    void updateDestinataire_ShouldUpdateDestinataire_WhenFound() {
        // GIVEN:
        when(destinataireRepository.findById(testId)).thenReturn(Optional.of(mockEntity));
        when(destinataireRepository.save(any(Destinataire.class))).thenReturn(mockEntity);
        when(destinataireMapper.toDto(mockEntity)).thenReturn(updateDto);

        // WHEN
        DestinataireDto result = destinataireService.updateDestinataire(testId, updateDto);

        // THEN
        assertNotNull(result);
        // Vérifie que le nom a été mis à jour sur l'entité mockée
        assertEquals("Alaoui", mockEntity.getNom());
        verify(destinataireRepository, times(1)).save(mockEntity);
    }

    @Test
    void updateDestinataire_ShouldThrowNotFound_WhenDestinataireNotFound() {
        // GIVEN: findById retourne vide
        when(destinataireRepository.findById(testId)).thenReturn(Optional.empty());

        // WHEN & THEN: Vérifie l'exception 404
        assertThrows(EntityNotFoundException.class, () -> {
            destinataireService.updateDestinataire(testId, updateDto);
        });
        verify(destinataireRepository, never()).save(any());
    }

    // =================================================================
    // 4. TESTS DELETE (Suppression)
    // =================================================================

    @Test
    void deleteDestinataire_ShouldSucceed_WhenFound() {
        // GIVEN: L'entité existe
        when(destinataireRepository.existsById(testId)).thenReturn(true);

        // WHEN: Appel de la suppression
        destinataireService.deleteDestinataire(testId);

        // THEN: Vérifie que deleteById a été appelé une fois
        verify(destinataireRepository, times(1)).deleteById(testId);
    }

    @Test
    void deleteDestinataire_ShouldThrowException_WhenNotFound() {
        // GIVEN: L'entité n'existe pas
        when(destinataireRepository.existsById(testId)).thenReturn(false);

        // WHEN & THEN: Vérifie l'exception 404
        assertThrows(EntityNotFoundException.class, () -> {
            destinataireService.deleteDestinataire(testId);
        });

        // THEN: Vérifie que deleteById n'a jamais été appelé
        verify(destinataireRepository, never()).deleteById(anyString());
    }
}