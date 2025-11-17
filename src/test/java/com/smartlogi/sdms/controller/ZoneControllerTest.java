package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.DTO.ZoneDto;
import com.smartlogi.sdms.service.ZoneService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'Unité pour ZoneController en utilisant MockMvc.
 */
@WebMvcTest(ZoneController.class) // Isole le test au contrôleur
public class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ZoneService zoneService;

    private String testId;
    private ZoneDto mockDto;
    private ZoneDto updateDto;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID().toString();
        // DTO de base pour les tests
        mockDto = new ZoneDto(
                testId, "Marrakech Gueliz", "40000"
        );
        // DTO pour la mise à jour
        updateDto = new ZoneDto(
                testId, "Casablanca Anfa", "20000"
        );
    }

    // =================================================================
    // 1. TESTS POST (Création)
    // =================================================================

    @Test
    void createZone_ShouldReturn201Created_WhenValid() throws Exception {
        // GIVEN: Le service retourne le DTO créé
        when(zoneService.createZone(any(ZoneDto.class))).thenReturn(mockDto);

        // WHEN & THEN: Simulation de la requête POST
        mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.id").value(testId))
                .andExpect(jsonPath("$.nom").value("Marrakech Gueliz"));

        verify(zoneService, times(1)).createZone(any(ZoneDto.class));
    }

    @Test
    void createZone_ShouldReturn400BadRequest_WhenValidationFails() throws Exception {
        // GIVEN: Crée un DTO invalide (Nom manquant)
        ZoneDto invalidDto = new ZoneDto(
                null, "", "12345"
        );

        // WHEN & THEN
        mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verify(zoneService, never()).createZone(any());
    }

    // =================================================================
    // 2. TESTS GET (Lecture)
    // =================================================================

    @Test
    void getZoneById_ShouldReturn200AndZone_WhenFound() throws Exception {
        // GIVEN
        when(zoneService.getZoneById(testId)).thenReturn(mockDto);

        // WHEN & THEN
        mockMvc.perform(get("/api/zones/{id}", testId))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.id").value(testId));
    }

    @Test
    void getZoneById_ShouldReturn404NotFound_WhenNotFound() throws Exception {
        // GIVEN: Le service lance l'exception 404
        when(zoneService.getZoneById(testId)).thenThrow(new EntityNotFoundException("Zone non trouvée"));

        // WHEN & THEN
        mockMvc.perform(get("/api/zones/{id}", testId))
                .andExpect(status().isNotFound()) // 404 Not Found
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    void getAllZones_ShouldReturn200AndList() throws Exception {
        // GIVEN
        List<ZoneDto> list = Arrays.asList(mockDto, updateDto);
        when(zoneService.getAllZones()).thenReturn(list);

        // WHEN & THEN
        mockMvc.perform(get("/api/zones"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.length()").value(2));
    }

    // =================================================================
    // 3. TESTS PUT (Mise à Jour)
    // =================================================================

    @Test
    void updateZone_ShouldReturn200Ok_WhenValid() throws Exception {
        // GIVEN: Le service retourne le DTO mis à jour
        when(zoneService.updateZone(eq(testId), any(ZoneDto.class))).thenReturn(updateDto);

        // WHEN & THEN
        mockMvc.perform(put("/api/zones/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.nom").value("Casablanca Anfa"));
    }

    @Test
    void updateZone_ShouldReturn404NotFound_WhenZoneNotFound() throws Exception {
        // GIVEN
        when(zoneService.updateZone(eq(testId), any())).thenThrow(new EntityNotFoundException("Zone non trouvée"));

        // WHEN & THEN
        mockMvc.perform(put("/api/zones/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound()); // 404 Not Found
    }

    // =================================================================
    // 4. TESTS DELETE (Suppression)
    // =================================================================

    @Test
    void deleteZone_ShouldReturn204NoContent_WhenSucceeds() throws Exception {
        // GIVEN: Le service ne renvoie rien (void)
        doNothing().when(zoneService).deleteZone(testId);

        // WHEN & THEN
        mockMvc.perform(delete("/api/zones/{id}", testId))
                .andExpect(status().isNoContent()); // 204 No Content

        verify(zoneService, times(1)).deleteZone(testId);
    }

    @Test
    void deleteZone_ShouldReturn404NotFound_WhenNotFound() throws Exception {
        // GIVEN: Le service lance l'exception 404
        doThrow(new EntityNotFoundException("Zone non trouvée")).when(zoneService).deleteZone(testId);

        // WHEN & THEN
        mockMvc.perform(delete("/api/zones/{id}", testId))
                .andExpect(status().isNotFound()); // 404 Not Found
    }
}