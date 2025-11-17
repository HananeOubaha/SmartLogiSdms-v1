package com.smartlogi.sdms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.sdms.DTO.LivreurDto;
import com.smartlogi.sdms.service.LivreurService;
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
 * Tests d'Unité pour LivreurController en utilisant MockMvc.
 */
@WebMvcTest(LivreurController.class) // Isole le test au contrôleur
public class LivreurControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simule le dispatching des requêtes HTTP

    @Autowired
    private ObjectMapper objectMapper; // Convertit les objets Java en JSON

    @MockBean // Simule la couche Service
    private LivreurService livreurService;

    private String testId;
    private LivreurDto mockDto;
    private LivreurDto updateDto;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID().toString();
        // DTO de base pour les tests
        mockDto = new LivreurDto(
                testId, "Idrissi", "Rachid", "0701020304", "Camionnette", "Marrakech Gueliz"
        );
        // DTO pour la mise à jour
        updateDto = new LivreurDto(
                testId, "Cherkaoui", "Fouad", "0699887766", "Moto", "Rabat Centre"
        );
    }

    // =================================================================
    // 1. TESTS POST (Création)
    // =================================================================

    @Test
    void createLivreur_ShouldReturn201Created_WhenValid() throws Exception {
        // GIVEN: Le service retourne le DTO créé
        when(livreurService.createLivreur(any(LivreurDto.class))).thenReturn(mockDto);

        // WHEN & THEN: Simulation de la requête POST
        mockMvc.perform(post("/api/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.id").value(testId))
                .andExpect(jsonPath("$.nom").value("Idrissi"));

        verify(livreurService, times(1)).createLivreur(any(LivreurDto.class));
    }

    @Test
    void createLivreur_ShouldReturn400BadRequest_WhenValidationFails() throws Exception {
        // GIVEN: Crée un DTO invalide (Téléphone manquant, Véhicule vide)
        LivreurDto invalidDto = new LivreurDto(
                null, "Nom", "Prenom", "", "","Zone"
        );

        // WHEN & THEN
        mockMvc.perform(post("/api/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.error").value("Validation Failed"));

        verify(livreurService, never()).createLivreur(any());
    }

    // =================================================================
    // 2. TESTS GET (Lecture)
    // =================================================================

    @Test
    void getLivreurById_ShouldReturn200AndLivreur_WhenFound() throws Exception {
        // GIVEN
        when(livreurService.getLivreurById(testId)).thenReturn(mockDto);

        // WHEN & THEN
        mockMvc.perform(get("/api/livreurs/{id}", testId))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.id").value(testId));
    }

    @Test
    void getLivreurById_ShouldReturn404NotFound_WhenNotFound() throws Exception {
        // GIVEN: Le service lance l'exception 404
        when(livreurService.getLivreurById(testId)).thenThrow(new EntityNotFoundException("Livreur non trouvé"));

        // WHEN & THEN
        mockMvc.perform(get("/api/livreurs/{id}", testId))
                .andExpect(status().isNotFound()) // 404 Not Found
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    void getAllLivreurs_ShouldReturn200AndList() throws Exception {
        // GIVEN
        List<LivreurDto> list = Arrays.asList(mockDto, updateDto);
        when(livreurService.getAllLivreurs()).thenReturn(list);

        // WHEN & THEN
        mockMvc.perform(get("/api/livreurs"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.length()").value(2));
    }

    // =================================================================
    // 3. TESTS PUT (Mise à Jour)
    // =================================================================

    @Test
    void updateLivreur_ShouldReturn200Ok_WhenValid() throws Exception {
        // GIVEN: Le service retourne le DTO mis à jour
        when(livreurService.updateLivreur(eq(testId), any(LivreurDto.class))).thenReturn(updateDto);

        // WHEN & THEN
        mockMvc.perform(put("/api/livreurs/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.vehicule").value("Moto")); // Vérifie que la mise à jour est effective
    }

    @Test
    void updateLivreur_ShouldReturn404NotFound_WhenLivreurNotFound() throws Exception {
        // GIVEN
        when(livreurService.updateLivreur(eq(testId), any())).thenThrow(new EntityNotFoundException("Livreur non trouvé"));

        // WHEN & THEN
        mockMvc.perform(put("/api/livreurs/{id}", testId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound()); // 404 Not Found
    }

    // =================================================================
    // 4. TESTS DELETE (Suppression)
    // =================================================================

    @Test
    void deleteLivreur_ShouldReturn204NoContent_WhenSucceeds() throws Exception {
        // GIVEN: Le service ne renvoie rien (void)
        doNothing().when(livreurService).deleteLivreur(testId);

        // WHEN & THEN
        mockMvc.perform(delete("/api/livreurs/{id}", testId))
                .andExpect(status().isNoContent()); // 204 No Content

        verify(livreurService, times(1)).deleteLivreur(testId);
    }

    @Test
    void deleteLivreur_ShouldReturn404NotFound_WhenNotFound() throws Exception {
        // GIVEN: Le service lance l'exception 404
        doThrow(new EntityNotFoundException("Livreur non trouvé")).when(livreurService).deleteLivreur(testId);

        // WHEN & THEN
        mockMvc.perform(delete("/api/livreurs/{id}", testId))
                .andExpect(status().isNotFound()); // 404 Not Found
    }
}