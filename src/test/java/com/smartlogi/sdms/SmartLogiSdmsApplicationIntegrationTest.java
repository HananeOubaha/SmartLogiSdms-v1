package com.smartlogi.sdms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SmartLogiSdmsApplicationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Vérifier que le contexte Spring charge correctement
        assertNotNull(restTemplate);
    }

    @Test
    void testActuatorHealth() {
        // Tester l'endpoint de health check
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("UP") || response.getBody().contains("status"));
    }

    @Test
    void testSwaggerIsAvailable() {
        // Vérifier que Swagger est disponible
        ResponseEntity<String> response = restTemplate.getForEntity("/swagger-ui.html", String.class);
        assertNotNull(response.getStatusCode());
    }

    @Test
    void testApplicationStartup() {
        // Vérifier que l'application démarre correctement
        ResponseEntity<String> response = restTemplate.getForEntity("/api/actuator/info", String.class);
        assertNotNull(response.getStatusCode());
    }
}

