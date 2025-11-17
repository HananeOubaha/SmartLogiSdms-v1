package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.DTO.ColisCreationDto;
import com.smartlogi.sdms.DTO.ZoneDto;
import com.smartlogi.sdms.DTO.ClientExpéditeurDto;
import com.smartlogi.sdms.DTO.DestinataireDto;
import com.smartlogi.sdms.DTO.LivreurDto;
import com.smartlogi.sdms.enums.StatutColis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests d'Intégration REST pour le flux complet du Colis (Tâche TI-01).
 * Utilise une base de données H2 en mémoire.
 */
@SpringBootTest // Charge tout le contexte Spring Boot
@AutoConfigureMockMvc // Configure MockMvc pour le test des contrôleurs
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@Transactional// Utilise un profil de test (pour la BDD H2)
public class ColisIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // IDs dynamiques qui seront créés pendant l'exécution
    private String clientExpediteurId;
    private String destinataireId;
    private String zoneId;
    private String livreurId;

    private ColisCreationDto baseCreationDto;

    // URLs de base
    private final String BASE_URL_CLIENT = "/api/clients-expediteurs";
    private final String BASE_URL_ZONE = "/api/zones";
    private final String BASE_URL_LIVREUR = "/api/livreurs";
    private final String BASE_URL_DESTINATAIRE = "/api/destinataires";
    private final String BASE_URL_COLIS = "/api/colis";

    @BeforeEach
    void setUp() throws Exception {
        // Nettoyage implicite via H2 après chaque classe de test si @Transactional est utilisé,
        // mais pour s'assurer que les IDs sont propres, nous les recréons.

        // 1. Création des dépendances (FKs) nécessaires pour le colis
        // 1. Création des dépendances (FKs) nécessaires pour le colis
        zoneId = createDependency(BASE_URL_ZONE, new ZoneDto(null, "Casablanca Anfa", "20000"));
        clientExpediteurId = createDependency(BASE_URL_CLIENT, new ClientExpéditeurDto(null, "Ali", "M.", "ali@test.com", "0600000001", "Adr Ali"));
        // L'appel utilise maintenant la constante déclarée ci-dessus
        destinataireId = createDependency(BASE_URL_DESTINATAIRE, new DestinataireDto(null, "Fatima", "Z.", "fatima@test.com", "0600000002", "Adr Fatima"));
        livreurId = createDependency(BASE_URL_LIVREUR, new LivreurDto(null, "Rachid", "I.", "0700000000", "Moto", "Zone Sud"));

        // 2. DTO de base pour la création du Colis
        baseCreationDto = new ColisCreationDto(
                "Colis Urgent Alpha", 3.0, "Casablanca", "HAUTE",
                clientExpediteurId, destinataireId, zoneId
        );
    }

    // =================================================================
    // MÉTHODE UTILITAIRE POUR CRÉER LES DÉPENDANCES
    // =================================================================

    // Méthode pour POSTer et extraire l'ID (UUID en String)
    private String createDependency(String url, Object dto) throws Exception {
        MvcResult result = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extrait l'ID de la réponse JSON
        String jsonResponse = result.getResponse().getContentAsString();
        return objectMapper.readTree(jsonResponse).get("id").asText();
    }

    // =================================================================
    // TESTS DU FLUX COLIS COMPLET
    // =================================================================

    @Test
    void A_fullColisWorkflow_ShouldSucceed() throws Exception {
        String colisId;

        // 1. Création du Colis (Statut: CRÉÉ)
        // ------------------------------------
        MvcResult creationResult = mockMvc.perform(post(BASE_URL_COLIS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseCreationDto)))
                .andExpect(status().isCreated()) // 201
                .andExpect(jsonPath("$.statut").value(StatutColis.CREE.name()))
                .andReturn();

        colisId = objectMapper.readTree(creationResult.getResponse().getContentAsString()).get("id").asText();
        assertTrue(colisId.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"));

        // 2. Affectation au Livreur (Statut: EN_TRANSIT) - Tâche TI-01
        // ----------------------------------------------------
        mockMvc.perform(put(BASE_URL_COLIS + "/assigner/{colisId}", colisId)
                        .param("livreurId", livreurId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.statut").value(StatutColis.EN_TRANSIT.name()))
                .andExpect(jsonPath("$.livreurId").value(livreurId));

        // 3. Changement de Statut (Livreur -> LIVRE) - Tâche TI-01
        // --------------------------------------------------------
        String commentaire = "Livré à la porte par Rachid.";

        mockMvc.perform(put(BASE_URL_COLIS + "/statut/{colisId}", colisId)
                        .param("statut", StatutColis.LIVRE.name())
                        .param("commentaire", commentaire)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.statut").value(StatutColis.LIVRE.name()));

        // 4. Vérification Finale et Historique
        // ------------------------------------
        mockMvc.perform(get(BASE_URL_COLIS + "/{id}", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value(StatutColis.LIVRE.name()));

        // (Optionnel) VÉRIFICATION de l'Historique (nécessite un HistoriqueController ou une requête directe)
        // Pour l'instant, on se contente de la traçabilité via le statut final.
    }

    @Test
    void B_createColis_ShouldReturn404NotFound_WhenFKIsInvalid() throws Exception {
        // GIVEN: Colis avec un ID de zone invalide (le service de zone lancera 404)
        ColisCreationDto invalidFKDto = new ColisCreationDto(
                "Colis test 404", 1.0, "Inconnu", "NORMALE",
                clientExpediteurId, destinataireId, "a1a1a1a1-dead-dead-dead-a1a1a1a1a1a1" // UUID invalide
        );

        // WHEN & THEN: L'erreur 404 doit être gérée par le GlobalExceptionHandler
        mockMvc.perform(post(BASE_URL_COLIS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFKDto)))
                .andExpect(status().isNotFound()) // 404
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }
}