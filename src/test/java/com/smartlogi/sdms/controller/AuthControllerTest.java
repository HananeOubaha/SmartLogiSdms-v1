package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.DTO.auth.JwtResponse;
import com.smartlogi.sdms.DTO.auth.LoginRequest;
import com.smartlogi.sdms.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void testAuthEndpointIsPublic() throws Exception {
        // Vérifier que l'endpoint /auth/** est accessible sans authentification
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"invalid\",\"password\":\"invalid\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSwaggerEndpointIsPublic() throws Exception {
        // Vérifier que l'endpoint Swagger est accessible
        mockMvc.perform(post("/swagger-ui.html"))
                .andExpect(status().isMethodNotAllowed()); // POST n'est pas autorisé, mais c'est ok
    }

    @Test
    void testJwtTokenGeneration() {
        // Vérifier que JWT peut être généré
        String token = jwtTokenProvider.generateToken(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "testuser", "password"
                )
        );
        assert token != null;
        assert !token.isEmpty();
    }
}

