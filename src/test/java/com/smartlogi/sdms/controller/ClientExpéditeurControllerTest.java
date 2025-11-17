package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.DTO.ClientExpéditeurDto;
import com.smartlogi.sdms.service.ClientExpéditeurService;
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
 * Tests d'Unité pour ClientExpéditeurController en utilisant MockMvc.
 * Se concentre sur les requêtes HTTP, la validation et les statuts.
 */
@WebMvcTest(ClientExpéditeurController.class) // Isole le test au contrôleur
public class ClientExpéditeurControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simule le dispatching des requêtes HTTP

    @Autowired
    private ObjectMapper objectMapper; // Convertit les objets Java en JSON

    @MockBean
    private ClientExpéditeurService clientExpéditeurService; // Mock de la couche Service

    private String testId;
    private ClientExpéditeurDto mockDto;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID().toString();
        // DTO de base pour les tests
        mockDto = new ClientExpéditeurDto(
                testId, "Dupont", "Jean", "jean.dupont@test.com", "0600000000", "15 Rue Paris"
        );
    }

    // =================================================================
    // 1. TESTS POST (Création)
    // =================================================================

    @Test
    void createClient_ShouldReturn201Created_WhenValid() throws Exception {
        // GIVEN: Le service retourne le DTO créé
        when(clientExpéditeurService.createClient(any(ClientExpéditeurDto.class))).thenReturn(mockDto);

        // WHEN & THEN: Simulation de la requête POST
        mockMvc.perform(post("/api/clients-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.id").value(testId))
                .andExpect(jsonPath("$.nom").value("Dupont"));

        // Vérifie que la méthode du service a été appelée
        verify(clientExpéditeurService, times(1)).createClient(any(ClientExpéditeurDto.class));
    }

    @Test
    void createClient_ShouldReturn400BadRequest_WhenValidationFails() throws Exception {
        // GIVEN: Crée un DTO invalide (Email manquant et Téléphone vide)
        ClientExpéditeurDto invalidDto = new ClientExpéditeurDto(
                null, "Nom", "Prenom", null, "", "Adresse"
        );

        // WHEN & THEN
        mockMvc.perform(post("/api/clients-expediteurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        // Vérifie que la méthode du service n'a pas été appelée
        verify(clientExpéditeurService, never()).createClient(any());
    }

    // =================================================================
    // 2. TESTS GET (Lecture)
    // =================================================================

    @Test
    void getClientById_ShouldReturn200AndClient_WhenFound() throws Exception {
        // GIVEN
        when(clientExpéditeurService.getClientById(testId)).thenReturn(mockDto);

        // WHEN & THEN
        mockMvc.perform(get("/api/clients-expediteurs/{id}", testId))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.id").value(testId));
    }

    @Test
    void getClientById_ShouldReturn404NotFound_WhenNotFound() throws Exception {
        // GIVEN: Le service lance l'exception 404
        when(clientExpéditeurService.getClientById(testId)).thenThrow(new EntityNotFoundException("Client non trouvé"));

        // WHEN & THEN
        mockMvc.perform(get("/api/clients-expediteurs/{id}", testId))
                .andExpect(status().isNotFound()) // 404 Not Found
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    void getAllClients_ShouldReturn200AndList() throws Exception {
        // GIVEN
        List<ClientExpéditeurDto> list = Arrays.asList(mockDto);
        when(clientExpéditeurService.getAllClients()).thenReturn(list);

        // WHEN & THEN
        mockMvc.perform(get("/api/clients-expediteurs"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$[0].id").value(testId))
                .andExpect(jsonPath("$.length()").value(1));
    }

    // =================================================================
    // 3. TESTS PUT (Mise à Jour)
    // =================================================================

    @Test
    void updateClient_ShouldReturn200Ok_WhenValid() throws Exception {
        // GIVEN: Le service retourne le DTO mis à jour
        when(clientExpéditeurService.updateClient(eq(testId), any(ClientExpéditeurDto.class))).thenReturn(mockDto);

        // WHEN & THEN
        mockMvc.perform(put("/api/clients-expediteurs/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.nom").value("Dupont"));
    }

    @Test
    void updateClient_ShouldReturn404NotFound_WhenClientNotFound() throws Exception {
        // GIVEN
        when(clientExpéditeurService.updateClient(eq(testId), any())).thenThrow(new EntityNotFoundException("Client non trouvé"));

        // WHEN & THEN
        mockMvc.perform(put("/api/clients-expediteurs/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isNotFound()); // 404 Not Found
    }

    // =================================================================
    // 4. TESTS DELETE (Suppression)
    // =================================================================

    @Test
    void deleteClient_ShouldReturn204NoContent_WhenSucceeds() throws Exception {
        // GIVEN: Le service ne renvoie rien (void)
        doNothing().when(clientExpéditeurService).deleteClient(testId);

        // WHEN & THEN
        mockMvc.perform(delete("/api/clients-expediteurs/{id}", testId))
                .andExpect(status().isNoContent()); // 204 No Content

        verify(clientExpéditeurService, times(1)).deleteClient(testId);
    }
}