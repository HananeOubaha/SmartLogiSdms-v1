package com.smartlogi.sdms.service;

import com.smartlogi.sdms.DTO.ColisCreationDto;
import com.smartlogi.sdms.DTO.ColisDto;
import com.smartlogi.sdms.enums.StatutColis;
import com.smartlogi.sdms.enums.PrioriteColis;
import com.smartlogi.sdms.mapper.ColisMapper;
import com.smartlogi.sdms.model.*;
import com.smartlogi.sdms.repository.ColisRepository;
import com.smartlogi.sdms.repository.HistoriqueLivraisonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitaires pour ColisService, couvrant le workflow et les vérifications de clés étrangères.
 */
public class ColisServiceTest {

    // Repositories et Mappers mockés
    @Mock
    private ColisRepository colisRepository;
    @Mock
    private ColisMapper colisMapper;
    @Mock
    private HistoriqueLivraisonRepository historiqueRepository;

    // Services de dépendance (pour valider les FKs)
    @Mock
    private ClientExpéditeurService clientExpéditeurService;
    @Mock
    private DestinataireService destinataireService;
    @Mock
    private ZoneService zoneService;
    @Mock
    private LivreurService livreurService;

    // Service à tester
    @InjectMocks
    private ColisService colisService;

    private String colisId;
    private String clientExpediteurId;
    private String destinataireId;
    private String zoneId;
    private String livreurId;

    private ColisCreationDto creationDto;
    private Colis mockColisEntity;
    private ColisDto mockColisDto;
    private ClientExpéditeur mockClient;
    private Livreur mockLivreur;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // IDs de Test (String)
        colisId = java.util.UUID.randomUUID().toString();
        clientExpediteurId = java.util.UUID.randomUUID().toString();
        destinataireId = java.util.UUID.randomUUID().toString();
        zoneId = java.util.UUID.randomUUID().toString();
        livreurId = java.util.UUID.randomUUID().toString();

        // 1. DTO de Création (Payload entrant)
        creationDto = new ColisCreationDto(
                "Colis Urgent A", 5.5, "Casablanca", "HAUTE",
                clientExpediteurId, destinataireId, zoneId
        );

        // 2. Entités de dépendance mockées (doivent exister pour la FK)
        mockClient = new ClientExpéditeur(clientExpediteurId, "Ali", "M.", "a@a.com", "06", "Adr", null);
        mockLivreur = new Livreur(livreurId, "Rachid", "I.", "07", "Moto", "Zone X", null);

        // 3. Entité Colis résultante (après save)
        mockColisEntity = new Colis(
                colisId, "Colis Urgent A", 5.5, StatutColis.CREE, PrioriteColis.HAUTE,
                "Casablanca", LocalDateTime.now(), mockLivreur, mockClient, null, null, null, null
        );

        // 4. DTO de réponse mocké
        mockColisDto = new ColisDto(
                colisId, "Colis Urgent A", 5.5, StatutColis.CREE.name(), PrioriteColis.HAUTE.name(),
                "Casablanca", LocalDateTime.now(), null, "Ali M.", "Zone X"
        );

        // Mock par défaut pour le Mapper (Entité -> DTO)
        when(colisMapper.toDto(any(Colis.class))).thenReturn(mockColisDto);
    }

    // =================================================================
    // 1. TESTS DE CRÉATION ET VÉRIFICATION DES FKs
    // =================================================================

    @Test
    void createColis_ShouldSucceed_WhenAllFksExist() {
        // GIVEN: Simule l'existence de toutes les clés étrangères (FKs)
        when(clientExpéditeurService.getClientEntityById(clientExpediteurId)).thenReturn(mockClient);
        when(destinataireService.getDestinataireEntityById(destinataireId)).thenReturn(new Destinataire());
        when(zoneService.getZoneEntityById(zoneId)).thenReturn(new Zone());

        // Simule la conversion DTO -> Entité (avant la sauvegarde)
        when(colisMapper.toEntity(creationDto)).thenReturn(mockColisEntity);
        // Simule l'enregistrement en BDD
        when(colisRepository.save(any(Colis.class))).thenReturn(mockColisEntity);

        // WHEN
        ColisDto result = colisService.createColis(creationDto);

        // THEN
        assertNotNull(result);
        assertEquals(colisId, result.getId());
        assertEquals(StatutColis.CREE.name(), result.getStatut());

        // Vérifie que l'historique a été enregistré une fois
        verify(historiqueRepository, times(1)).save(any(HistoriqueLivraison.class));
        // Vérifie que les services de vérification ont été appelés
        verify(clientExpéditeurService, times(1)).getClientEntityById(clientExpediteurId);
    }

    @Test
    void createColis_ShouldThrowException_WhenClientFKNotFound() {
        // GIVEN: Le Client Expéditeur n'est pas trouvé (FK échouée)
        when(clientExpéditeurService.getClientEntityById(clientExpediteurId)).thenThrow(new EntityNotFoundException("Client non trouvé"));

        // WHEN & THEN: L'exception doit être propagée
        assertThrows(EntityNotFoundException.class, () -> {
            colisService.createColis(creationDto);
        });

        // THEN: Vérifie qu'aucune sauvegarde n'a été effectuée
        verify(colisRepository, never()).save(any());
        verify(historiqueRepository, never()).save(any());
    }

    // =================================================================
    // 2. TESTS DE WORKFLOW (AFFECTION / UPDATE STATUT)
    // =================================================================

    @Test
    void assignerLivreur_ShouldUpdateStatusToEnTransitAndRecordHistory() {
        // GIVEN: Colis trouvé et Livreur trouvé
        when(colisRepository.findById(colisId)).thenReturn(Optional.of(mockColisEntity));
        when(livreurService.getLivreurEntityById(livreurId)).thenReturn(mockLivreur);

        // Simule la sauvegarde de la mise à jour
        when(colisRepository.save(any(Colis.class))).thenReturn(mockColisEntity);

        // WHEN
        colisService.assignerLivreur(colisId, livreurId);

        // THEN
        // Vérifie que le statut a été mis à jour dans l'entité
        assertEquals(StatutColis.EN_TRANSIT, mockColisEntity.getStatut());
        // Vérifie l'enregistrement de l'historique
        verify(historiqueRepository, times(1)).save(any(HistoriqueLivraison.class));
    }

    @Test
    void updateStatut_ShouldChangeStatusAndRecordHistory() {
        // GIVEN
        when(colisRepository.findById(colisId)).thenReturn(Optional.of(mockColisEntity));
        when(colisRepository.save(any(Colis.class))).thenReturn(mockColisEntity);

        StatutColis nouveauStatut = StatutColis.LIVRE;
        String commentaire = "Colis livré par Rachid.";

        // WHEN
        colisService.updateStatut(colisId, nouveauStatut, commentaire);

        // THEN
        // Vérifie que le statut a changé
        assertEquals(StatutColis.LIVRE, mockColisEntity.getStatut());
        // Vérifie l'enregistrement de l'historique
        verify(historiqueRepository, times(1)).save(any(HistoriqueLivraison.class));
    }

    @Test
    void updateStatut_ShouldThrowException_WhenColisNotFound() {
        // GIVEN
        when(colisRepository.findById(colisId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(EntityNotFoundException.class, () -> {
            colisService.updateStatut(colisId, StatutColis.COLLECTE, "Tentative.");
        });
    }

    // =================================================================
    // 3. TESTS DELETE
    // =================================================================

    @Test
    void deleteColis_ShouldSucceed_WhenFound() {
        // GIVEN
        when(colisRepository.existsById(colisId)).thenReturn(true);

        // WHEN
        colisService.deleteColis(colisId);

        // THEN
        verify(colisRepository, times(1)).deleteById(colisId);
    }

    @Test
    void deleteColis_ShouldThrowException_WhenNotFound() {
        // GIVEN
        when(colisRepository.existsById(colisId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(EntityNotFoundException.class, () -> {
            colisService.deleteColis(colisId);
        });
        verify(colisRepository, never()).deleteById(anyString());
    }
}