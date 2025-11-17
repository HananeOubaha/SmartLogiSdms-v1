package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ProduitDto;
import com.smartlogi.sdms.mapper.ProduitMapper;
import com.smartlogi.sdms.model.Produit;
import com.smartlogi.sdms.repository.ProduitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitaires pour ProduitService (Tâche TU-03).
 */
public class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ProduitMapper produitMapper;

    @InjectMocks
    private ProduitService produitService;

    private String testId;
    private Produit mockEntity;
    private ProduitDto mockDto;
    private ProduitDto updateDto;
    private ProduitDto createdDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testId = java.util.UUID.randomUUID().toString();

        // DTO initial
        mockDto = new ProduitDto(
                null, "Drone DJI", "Électronique", 1.5, 12000.00
        );

        // Entité mockée
        mockEntity = new Produit(
                testId, "Drone DJI", "Électronique", 1.5, 12000.00, null
        );

        // DTO attendu
        createdDto = new ProduitDto(
                testId, "Drone DJI", "Électronique", 1.5, 12000.00
        );

        // DTO pour la mise à jour
        updateDto = new ProduitDto(
                testId, "Batterie Drone", "Accessoire", 0.5, 500.00
        );
    }

    // =================================================================
    // 1. TESTS CREATE / READ
    // =================================================================

    @Test
    void createProduit_ShouldReturnCreatedProduct() {
        when(produitRepository.save(any(Produit.class))).thenReturn(mockEntity);
        when(produitMapper.toEntity(any(ProduitDto.class))).thenReturn(mockEntity);
        when(produitMapper.toDto(mockEntity)).thenReturn(createdDto);

        ProduitDto result = produitService.createProduit(mockDto);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(produitRepository, times(1)).save(mockEntity);
    }

    @Test
    void getProduitById_ShouldReturnProduitDto_WhenFound() {
        when(produitRepository.findById(testId)).thenReturn(Optional.of(mockEntity));
        when(produitMapper.toDto(mockEntity)).thenReturn(createdDto);

        ProduitDto result = produitService.getProduitById(testId);

        assertNotNull(result);
        assertEquals(testId, result.getId());
    }

    @Test
    void getAllProduits_ShouldReturnListOfDtos() {
        List<Produit> entityList = Arrays.asList(mockEntity);
        List<ProduitDto> dtoList = Arrays.asList(createdDto);

        when(produitRepository.findAll()).thenReturn(entityList);
        when(produitMapper.toDto(entityList)).thenReturn(dtoList);

        List<ProduitDto> result = produitService.getAllProduits();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // =================================================================
    // 2. TESTS UPDATE / DELETE
    // =================================================================

    @Test
    void updateProduit_ShouldUpdateProduct_WhenFound() {
        // GIVEN:
        when(produitRepository.findById(testId)).thenReturn(Optional.of(mockEntity));
        when(produitRepository.save(any(Produit.class))).thenReturn(mockEntity);
        when(produitMapper.toDto(mockEntity)).thenReturn(updateDto);

        // WHEN
        ProduitDto result = produitService.updateProduit(testId, updateDto);

        // THEN
        assertNotNull(result);
        assertEquals("Batterie Drone", mockEntity.getNom());
        assertEquals(0.5, mockEntity.getPoids());
        verify(produitRepository, times(1)).save(mockEntity);
    }

    @Test
    void deleteProduit_ShouldSucceed_WhenFound() {
        // GIVEN: L'entité existe
        when(produitRepository.existsById(testId)).thenReturn(true);

        // WHEN: Appel de la suppression
        produitService.deleteProduit(testId);

        // THEN: Vérifie que deleteById a été appelé une fois
        verify(produitRepository, times(1)).deleteById(testId);
    }

    @Test
    void deleteProduit_ShouldThrowException_WhenNotFound() {
        // GIVEN: L'entité n'existe pas
        when(produitRepository.existsById(testId)).thenReturn(false);

        // WHEN & THEN: Vérifie l'exception 404
        assertThrows(EntityNotFoundException.class, () -> {
            produitService.deleteProduit(testId);
        });

        // THEN: Vérifie que deleteById n'a jamais été appelé
        verify(produitRepository, never()).deleteById(anyString());
    }
}