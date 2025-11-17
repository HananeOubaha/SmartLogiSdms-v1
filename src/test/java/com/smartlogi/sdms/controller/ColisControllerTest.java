package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.DTO.ColisCreationDto;
import com.smartlogi.sdms.DTO.ColisDto;
import com.smartlogi.sdms.enums.StatutColis;
import com.smartlogi.sdms.service.ColisService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'Unité pour ColisController, couvrant le CRUD et le workflow.
 */
@WebMvcTest(ColisController.class)
public class ColisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ColisService colisService;

    private String colisId;
    private String livreurId;
    private ColisDto mockColisDto;
    private ColisCreationDto creationDto;

    @BeforeEach
    void setUp() {
        colisId = UUID.randomUUID().toString();
        livreurId = UUID.randomUUID().toString();

        // DTO de base pour la création (UUIDs convertis en String)
        creationDto = new ColisCreationDto(
                "Colis Urgent", 2.0, "Rabat", "HAUTE",
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()
        );

        // DTO de réponse mocké
        mockColisDto = new ColisDto(
                colisId, "Colis Urgent", 2.0, StatutColis.CREE.name(), "HAUTE",
                "Rabat", LocalDateTime.now(), null, "Client Alpha", "Zone Rabat"
        );
    }

    // =================================================================
    // 1. TESTS CRUD / CRÉATION
    // =================================================================

    @Test
    void createColis_ShouldReturn201Created_WhenValid() throws Exception {
        // GIVEN
        when(colisService.createColis(any(ColisCreationDto.class))).thenReturn(mockColisDto);

        // WHEN & THEN
        mockMvc.perform(post("/api/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationDto)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.statut").value(StatutColis.CREE.name()));

        verify(colisService, times(1)).createColis(any(ColisCreationDto.class));
    }

    @Test
    void getColisById_ShouldReturn200AndColis_WhenFound() throws Exception {
        // GIVEN
        when(colisService.getColisById(colisId)).thenReturn(mockColisDto);

        // WHEN & THEN
        mockMvc.perform(get("/api/colis/{id}", colisId))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.id").value(colisId));
    }

    @Test
    void getColisById_ShouldReturn404NotFound_WhenNotFound() throws Exception {
        // GIVEN
        when(colisService.getColisById(colisId)).thenThrow(new EntityNotFoundException("Colis non trouvé"));

        // WHEN & THEN
        mockMvc.perform(get("/api/colis/{id}", colisId))
                .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    void getAllColis_ShouldReturn200AndList() throws Exception {
        // GIVEN
        List<ColisDto> list = Arrays.asList(mockColisDto);
        when(colisService.getAllColis()).thenReturn(list);

        // WHEN & THEN
        mockMvc.perform(get("/api/colis"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteColis_ShouldReturn204NoContent() throws Exception {
        // GIVEN
        doNothing().when(colisService).deleteColis(colisId);

        // WHEN & THEN
        mockMvc.perform(delete("/api/colis/{id}", colisId))
                .andExpect(status().isNoContent()); // 204 No Content

        verify(colisService, times(1)).deleteColis(colisId);
    }

    // =================================================================
    // 2. TESTS DE WORKFLOW (Affectation et Statut)
    // =================================================================

    @Test
    void assignerLivreur_ShouldReturn200AndEnTransitStatus() throws Exception {
        // GIVEN: Simule l'affectation réussie
        ColisDto assignedDto = new ColisDto(
                colisId, "Colis Urgent", 2.0, StatutColis.EN_TRANSIT.name(), "HAUTE",
                "Rabat", LocalDateTime.now(), livreurId, "Client Alpha", "Zone Rabat"
        );
        when(colisService.assignerLivreur(eq(colisId), eq(livreurId))).thenReturn(assignedDto);

        // WHEN & THEN
        mockMvc.perform(put("/api/colis/assigner/{colisId}", colisId)
                        .param("livreurId", livreurId) // ID du Livreur en paramètre de requête
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.statut").value(StatutColis.EN_TRANSIT.name()))
                .andExpect(jsonPath("$.livreurId").value(livreurId));

        verify(colisService, times(1)).assignerLivreur(colisId, livreurId);
    }

    @Test
    void updateStatut_ShouldReturn200AndLivreStatus() throws Exception {
        // GIVEN: Simule le statut LIVRÉ
        ColisDto deliveredDto = new ColisDto(
                colisId, "Colis Urgent", 2.0, StatutColis.LIVRE.name(), "HAUTE",
                "Rabat", LocalDateTime.now(), livreurId, "Client Alpha", "Zone Rabat"
        );
        String commentaire = "Colis remis au destinataire.";

        // Simule l'appel de mise à jour du statut
        when(colisService.updateStatut(
                eq(colisId),
                eq(StatutColis.LIVRE), // Statut attendu dans le service
                eq(commentaire))
        ).thenReturn(deliveredDto);

        // WHEN & THEN: Utilisation du paramètre statut=LIVRE
        mockMvc.perform(put("/api/colis/statut/{colisId}", colisId)
                        .param("statut", StatutColis.LIVRE.name())
                        .param("commentaire", commentaire)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.statut").value(StatutColis.LIVRE.name()));

        verify(colisService, times(1)).updateStatut(colisId, StatutColis.LIVRE, commentaire);
    }
}