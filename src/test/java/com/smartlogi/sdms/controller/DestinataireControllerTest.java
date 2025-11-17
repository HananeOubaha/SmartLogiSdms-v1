package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.DTO.DestinataireDto;
import com.smartlogi.sdms.service.DestinataireService;
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
 * Tests d'Unité pour DestinataireController en utilisant MockMvc.
 * Se concentre sur les requêtes HTTP, la validation et les statuts.
 */
@WebMvcTest(DestinataireController.class) // Isole le test au contrôleur
public class DestinataireControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simule le dispatching des requêtes HTTP

    @Autowired
    private ObjectMapper objectMapper; // Convertit les objets Java en JSON

    @MockBean // Simule la couche Service (Avertissement ignoré)
    private DestinataireService destinataireService;

    private String testId;
    private DestinataireDto mockDto;
    private DestinataireDto updateDto;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID().toString();
        // DTO de base pour les tests
        mockDto = new DestinataireDto(
                testId, "Hassani", "Samira", "samira@domicile.com", "0600000000", "12 Rue Rabat"
        );
        // DTO pour la mise à jour
        updateDto = new DestinataireDto(
                testId, "Alaoui", "Fatima", "fatima.alaoui@test.com", "0711223344", "25 Av. Marrakech"
        );
    }

    // =================================================================
    // 1. TESTS POST (Création)
    // =================================================================

    @Test
    void createDestinataire_ShouldReturn201Created_WhenValid() throws Exception {
        // GIVEN: Le service retourne le DTO créé
        when(destinataireService.createDestinataire(any(DestinataireDto.class))).thenReturn(mockDto);

        // WHEN & THEN: Simulation de la requête POST
        mockMvc.perform(post("/api/destinataires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.id").value(testId))
                .andExpect(jsonPath("$.nom").value("Hassani"));

        verify(destinataireService, times(1)).createDestinataire(any(DestinataireDto.class));
    }

    @Test
    void createDestinataire_ShouldReturn400BadRequest_WhenValidationFails() throws Exception {
        // GIVEN: Crée un DTO invalide (Téléphone manquant)
        DestinataireDto invalidDto = new DestinataireDto(
                null, "Nom", "Prenom", "test@test.com", "", "Adresse" // Téléphone vide = @NotBlank échoue
        );

        // WHEN & THEN
        mockMvc.perform(post("/api/destinataires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verify(destinataireService, never()).createDestinataire(any());
    }

    // =================================================================
    // 2. TESTS GET (Lecture)
    // =================================================================

    @Test
    void getDestinataireById_ShouldReturn200AndDestinataire_WhenFound() throws Exception {
        // GIVEN
        when(destinataireService.getDestinataireById(testId)).thenReturn(mockDto);

        // WHEN & THEN
        mockMvc.perform(get("/api/destinataires/{id}", testId))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.id").value(testId));
    }

    @Test
    void getDestinataireById_ShouldReturn404NotFound_WhenNotFound() throws Exception {
        // GIVEN: Le service lance l'exception 404
        when(destinataireService.getDestinataireById(testId)).thenThrow(new EntityNotFoundException("Destinataire non trouvé"));

        // WHEN & THEN
        mockMvc.perform(get("/api/destinataires/{id}", testId))
                .andExpect(status().isNotFound()) // 404 Not Found
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    void getAllDestinataires_ShouldReturn200AndList() throws Exception {
        // GIVEN
        List<DestinataireDto> list = Arrays.asList(mockDto, updateDto);
        when(destinataireService.getAllDestinataires()).thenReturn(list);

        // WHEN & THEN
        mockMvc.perform(get("/api/destinataires"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.length()").value(2));
    }

    // =================================================================
    // 3. TESTS PUT (Mise à Jour)
    // =================================================================

    @Test
    void updateDestinataire_ShouldReturn200Ok_WhenValid() throws Exception {
        // GIVEN: Le service retourne le DTO mis à jour
        when(destinataireService.updateDestinataire(eq(testId), any(DestinataireDto.class))).thenReturn(updateDto);

        // WHEN & THEN
        mockMvc.perform(put("/api/destinataires/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.nom").value("Alaoui"));
    }

    @Test
    void updateDestinataire_ShouldReturn404NotFound_WhenDestinataireNotFound() throws Exception {
        // GIVEN
        when(destinataireService.updateDestinataire(eq(testId), any())).thenThrow(new EntityNotFoundException("Destinataire non trouvé"));

        // WHEN & THEN
        mockMvc.perform(put("/api/destinataires/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound()); // 404 Not Found
    }

    // =================================================================
    // 4. TESTS DELETE (Suppression)
    // =================================================================

    @Test
    void deleteDestinataire_ShouldReturn204NoContent_WhenSucceeds() throws Exception {
        // GIVEN: Le service ne renvoie rien (void)
        doNothing().when(destinataireService).deleteDestinataire(testId);

        // WHEN & THEN
        mockMvc.perform(delete("/api/destinataires/{id}", testId))
                .andExpect(status().isNoContent()); // 204 No Content

        verify(destinataireService, times(1)).deleteDestinataire(testId);
    }

    @Test
    void deleteDestinataire_ShouldReturn404NotFound_WhenNotFound() throws Exception {
        // GIVEN: Le service lance l'exception 404
        doThrow(new EntityNotFoundException("Destinataire non trouvé")).when(destinataireService).deleteDestinataire(testId);

        // WHEN & THEN
        mockMvc.perform(delete("/api/destinataires/{id}", testId))
                .andExpect(status().isNotFound()); // 404 Not Found
    }
}