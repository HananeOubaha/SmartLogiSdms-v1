package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.LivreurDto;
import com.smartlogi.sdms.mapper.LivreurMapper;
import com.smartlogi.sdms.model.Livreur;
import com.smartlogi.sdms.repository.LivreurRepository;
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
 * Tests Unitaires pour LivreurService.
 */
public class LivreurServiceTest {

    @Mock
    private LivreurRepository livreurRepository;

    @Mock
    private LivreurMapper livreurMapper;

    @InjectMocks
    private LivreurService livreurService;

    private String testId;
    private Livreur mockEntity;
    private LivreurDto mockDto;
    private LivreurDto updateDto;
    private LivreurDto createdDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testId = java.util.UUID.randomUUID().toString();

        // DTO initial
        mockDto = new LivreurDto(
                null, "Idrissi", "Rachid", "0701020304", "Camionnette", "Marrakech Gueliz"
        );

        // Entité mockée avec l'ID simulé
        // CORRECTION ASSUMÉE (6 champs + List<Colis>)
        mockEntity = new Livreur(
                testId, "Idrissi", "Rachid", "0701020304", "Camionnette", "Marrakech Gueliz", null
                // Si Livreur n'a que 7 champs (6 attributs + 1 List), retirez un 'null'
        );

        // DTO attendu
        createdDto = new LivreurDto(
                testId, "Idrissi", "Rachid", "0701020304", "Camionnette", "Marrakech Gueliz"
        );

        // DTO pour la mise à jour
        updateDto = new LivreurDto(
                testId, "Cherkaoui", "Fouad", "0699887766", "Moto", "Rabat Centre"
        );
    }

    // =================================================================
    // 1. TESTS CREATE (Création)
    // =================================================================

    @Test
    void createLivreur_ShouldReturnCreatedLivreur() {
        // GIVEN
        when(livreurRepository.save(any(Livreur.class))).thenReturn(mockEntity);
        when(livreurMapper.toEntity(any(LivreurDto.class))).thenReturn(mockEntity); // Assurez la conversion
        when(livreurMapper.toDto(mockEntity)).thenReturn(createdDto);

        // WHEN
        LivreurDto result = livreurService.createLivreur(mockDto);

        // THEN
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Idrissi", result.getNom());
        verify(livreurRepository, times(1)).save(any(Livreur.class));
    }

    // =================================================================
    // 2. TESTS READ (Lecture)
    // =================================================================

    @Test
    void getLivreurById_ShouldReturnLivreurDto_WhenFound() {
        // GIVEN
        when(livreurRepository.findById(testId)).thenReturn(Optional.of(mockEntity));
        when(livreurMapper.toDto(mockEntity)).thenReturn(createdDto);

        // WHEN
        LivreurDto result = livreurService.getLivreurById(testId);

        // THEN
        assertNotNull(result);
        assertEquals(testId, result.getId());
    }

    @Test
    void getLivreurById_ShouldThrowException_WhenNotFound() {
        // GIVEN: L'ID n'est pas trouvé
        when(livreurRepository.findById(testId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(EntityNotFoundException.class, () -> {
            livreurService.getLivreurById(testId);
        });
    }

    @Test
    void getAllLivreurs_ShouldReturnListOfDtos() {
        // GIVEN
        List<Livreur> entityList = Arrays.asList(mockEntity, mockEntity);
        List<LivreurDto> dtoList = Arrays.asList(createdDto, createdDto);

        when(livreurRepository.findAll()).thenReturn(entityList);
        when(livreurMapper.toDto(entityList)).thenReturn(dtoList);

        // WHEN
        List<LivreurDto> result = livreurService.getAllLivreurs();

        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(livreurRepository, times(1)).findAll();
    }

    // =================================================================
    // 3. TESTS UPDATE (Mise à Jour)
    // =================================================================

    @Test
    void updateLivreur_ShouldUpdateLivreur_WhenFound() {
        // GIVEN:
        when(livreurRepository.findById(testId)).thenReturn(Optional.of(mockEntity));
        when(livreurRepository.save(any(Livreur.class))).thenReturn(mockEntity);
        when(livreurMapper.toDto(mockEntity)).thenReturn(updateDto);

        // WHEN
        LivreurDto result = livreurService.updateLivreur(testId, updateDto);

        // THEN
        assertNotNull(result);
        // Vérifie que le champ critique a été mis à jour dans l'entité mockée
        assertEquals("Cherkaoui", mockEntity.getNom());
        assertEquals("Moto", mockEntity.getVehicule());
        verify(livreurRepository, times(1)).save(mockEntity);
    }

    @Test
    void updateLivreur_ShouldThrowNotFound_WhenLivreurNotFound() {
        // GIVEN: findById retourne vide
        when(livreurRepository.findById(testId)).thenReturn(Optional.empty());

        // WHEN & THEN: Vérifie l'exception 404
        assertThrows(EntityNotFoundException.class, () -> {
            livreurService.updateLivreur(testId, updateDto);
        });
        verify(livreurRepository, never()).save(any());
    }

    // =================================================================
    // 4. TESTS DELETE (Suppression)
    // =================================================================

    @Test
    void deleteLivreur_ShouldSucceed_WhenFound() {
        // GIVEN: L'entité existe
        when(livreurRepository.existsById(testId)).thenReturn(true);

        // WHEN: Appel de la suppression
        livreurService.deleteLivreur(testId);

        // THEN: Vérifie que deleteById a été appelé une fois
        verify(livreurRepository, times(1)).deleteById(testId);
    }

    @Test
    void deleteLivreur_ShouldThrowException_WhenNotFound() {
        // GIVEN: L'entité n'existe pas
        when(livreurRepository.existsById(testId)).thenReturn(false);

        // WHEN & THEN: Vérifie l'exception 404
        assertThrows(EntityNotFoundException.class, () -> {
            livreurService.deleteLivreur(testId);
        });

        // THEN: Vérifie que deleteById n'a jamais été appelé
        verify(livreurRepository, never()).deleteById(anyString());
    }
}