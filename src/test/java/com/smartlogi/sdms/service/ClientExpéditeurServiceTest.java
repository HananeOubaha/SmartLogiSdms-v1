package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ClientExpéditeurDto;
import com.smartlogi.sdms.mapper.ClientExpéditeurMapper;
import com.smartlogi.sdms.model.ClientExpéditeur;
import com.smartlogi.sdms.repository.ClientExpéditeurRepository;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitaires pour ClientExpéditeurService.
 * Utilise Mockito pour isoler le service des dépendances (BDD, Mapper).
 */
public class ClientExpéditeurServiceTest {

    @Mock
    private ClientExpéditeurRepository clientExpéditeurRepository;

    @Mock
    private ClientExpéditeurMapper clientExpéditeurMapper;

    @InjectMocks
    private ClientExpéditeurService clientExpéditeurService;

    // Données de test (ID String car nous avons migré)
    private String testId;
    private ClientExpéditeur mockEntity;
    private ClientExpéditeurDto mockDto;
    private ClientExpéditeurDto updateDto;
    private ClientExpéditeurDto createdDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // ID généré en String
        testId = java.util.UUID.randomUUID().toString();

        // DTO initial pour la création/lecture
        mockDto = new ClientExpéditeurDto(
                null, "Akermi", "Youssef", "initial@email.com", "0611223344", "12 Rue Casa"
        );

        // Entité mockée avec l'ID simulé
        mockEntity = new ClientExpéditeur(
                testId, "Akermi", "Youssef", "initial@email.com", "0611223344", "12 Rue Casa", null
        );

        // DTO attendu après la création/lecture
        createdDto = new ClientExpéditeurDto(
                testId, "Akermi", "Youssef", "initial@email.com", "0611223344", "12 Rue Casa"
        );

        // DTO pour la mise à jour
        updateDto = new ClientExpéditeurDto(
                testId, "Dupont", "Jean", "new.email@test.com", "0700000000", "15 Avenue Modifiée"
        );
    }

    // =================================================================
    // 1. TESTS CREATE (Création)
    // =================================================================

    @Test
    void createClient_ShouldReturnCreatedClient_WhenEmailIsUnique() {
        // GIVEN:
        // 1. Le Repository vérifie que l'email est unique (Optional.empty())
        when(clientExpéditeurRepository.findByEmail(mockDto.getEmail())).thenReturn(Optional.empty());
        // 2. Le Mapper convertit le DTO en entité (l'ID sera généré par @PrePersist lors de save)
        when(clientExpéditeurMapper.toEntity(mockDto)).thenReturn(mockEntity);
        // 3. La sauvegarde retourne l'entité avec l'ID
        when(clientExpéditeurRepository.save(mockEntity)).thenReturn(mockEntity);
        // 4. Le Mapper reconvertit en DTO final
        when(clientExpéditeurMapper.toDto(mockEntity)).thenReturn(createdDto);

        // WHEN
        ClientExpéditeurDto result = clientExpéditeurService.createClient(mockDto);

        // THEN
        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(clientExpéditeurRepository, times(1)).save(mockEntity);
    }

    @Test
    void createClient_ShouldThrowException_WhenEmailExists() {
        // GIVEN: Le Repository trouve un client existant avec le même email
        when(clientExpéditeurRepository.findByEmail(mockDto.getEmail())).thenReturn(Optional.of(mockEntity));

        // WHEN & THEN: Vérifie que la méthode lance DataIntegrityViolationException
        assertThrows(DataIntegrityViolationException.class, () -> {
            clientExpéditeurService.createClient(mockDto);
        });

        // THEN: Vérifie que save() n'a JAMAIS été appelé
        verify(clientExpéditeurRepository, never()).save(any(ClientExpéditeur.class));
    }

    // =================================================================
    // 2. TESTS READ (Lecture)
    // =================================================================

    @Test
    void getClientById_ShouldReturnClientDto_WhenFound() {
        // GIVEN
        when(clientExpéditeurRepository.findById(testId)).thenReturn(Optional.of(mockEntity));
        when(clientExpéditeurMapper.toDto(mockEntity)).thenReturn(createdDto);

        // WHEN
        ClientExpéditeurDto result = clientExpéditeurService.getClientById(testId);

        // THEN
        assertNotNull(result);
        assertEquals(testId, result.getId());
    }

    @Test
    void getClientById_ShouldThrowException_WhenNotFound() {
        // GIVEN: L'ID n'est pas trouvé
        when(clientExpéditeurRepository.findById(testId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(EntityNotFoundException.class, () -> {
            clientExpéditeurService.getClientById(testId);
        });
    }

    @Test
    void getAllClients_ShouldReturnListOfDtos() {
        // GIVEN
        List<ClientExpéditeur> entityList = Arrays.asList(mockEntity, mockEntity);
        List<ClientExpéditeurDto> dtoList = Arrays.asList(createdDto, createdDto);

        when(clientExpéditeurRepository.findAll()).thenReturn(entityList);
        // Utilisation de la méthode stream().map().collect() pour simuler le Mapper.
        // NOTE: MapStruct crée aussi une méthode toDto(List<Entity>).
        when(clientExpéditeurMapper.toDto(entityList)).thenReturn(dtoList);

        // WHEN
        List<ClientExpéditeurDto> result = clientExpéditeurService.getAllClients();

        // THEN
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        verify(clientExpéditeurRepository, times(1)).findAll();
    }

    // =================================================================
    // 3. TESTS UPDATE (Mise à Jour)
    // =================================================================

    @Test
    void updateClient_ShouldUpdateClient_WhenFoundAndEmailNotChanged() {
        // GIVEN:
        when(clientExpéditeurRepository.findById(testId)).thenReturn(Optional.of(mockEntity));
        when(clientExpéditeurRepository.save(any(ClientExpéditeur.class))).thenReturn(mockEntity);
        when(clientExpéditeurMapper.toDto(mockEntity)).thenReturn(updateDto);

        // Le DTO de mise à jour utilise l'email non changé pour éviter le conflit dans ce cas
        updateDto.setEmail("initial@email.com");

        // WHEN
        ClientExpéditeurDto result = clientExpéditeurService.updateClient(testId, updateDto);

        // THEN
        assertNotNull(result);
        assertEquals("Dupont", mockEntity.getNom());
        verify(clientExpéditeurRepository, times(1)).save(mockEntity);
        verify(clientExpéditeurRepository, never()).findByEmail(anyString()); // Vérifie qu'on n'a pas vérifié l'unicité
    }

    @Test
    void updateClient_ShouldThrowNotFound_WhenClientNotFound() {
        // GIVEN: findById retourne vide
        when(clientExpéditeurRepository.findById(testId)).thenReturn(Optional.empty());

        // WHEN & THEN: Vérifie l'exception 404
        assertThrows(EntityNotFoundException.class, () -> {
            clientExpéditeurService.updateClient(testId, updateDto);
        });
        verify(clientExpéditeurRepository, never()).save(any());
    }

    @Test
    void updateClient_ShouldThrowDataIntegrity_WhenNewEmailExists() {
        // GIVEN:
        // 1. L'entité initiale est trouvée (mockEntity)
        when(clientExpéditeurRepository.findById(testId)).thenReturn(Optional.of(mockEntity));

        // 2. Le Repository trouve un conflit avec le NOUVEL email
        ClientExpéditeur otherClient = new ClientExpéditeur();
        when(clientExpéditeurRepository.findByEmail(updateDto.getEmail())).thenReturn(Optional.of(otherClient));

        // WHEN & THEN: Vérifie l'exception de conflit
        assertThrows(DataIntegrityViolationException.class, () -> {
            clientExpéditeurService.updateClient(testId, updateDto);
        });

        // THEN: Vérifie que l'entité n'est pas sauvegardée
        verify(clientExpéditeurRepository, never()).save(any());
    }

    // =================================================================
    // 4. TESTS DELETE (Suppression)
    // =================================================================

    @Test
    void deleteClient_ShouldSucceed_WhenFound() {
        // GIVEN: L'entité existe
        when(clientExpéditeurRepository.existsById(testId)).thenReturn(true);

        // WHEN: Appel de la suppression
        clientExpéditeurService.deleteClient(testId);

        // THEN: Vérifie que deleteById a été appelé une fois
        verify(clientExpéditeurRepository, times(1)).deleteById(testId);
    }

    @Test
    void deleteClient_ShouldThrowException_WhenNotFound() {
        // GIVEN: L'entité n'existe pas
        when(clientExpéditeurRepository.existsById(testId)).thenReturn(false);

        // WHEN & THEN: Vérifie l'exception 404
        assertThrows(EntityNotFoundException.class, () -> {
            clientExpéditeurService.deleteClient(testId);
        });

        // THEN: Vérifie que deleteById n'a jamais été appelé
        verify(clientExpéditeurRepository, never()).deleteById(anyString());
    }
}