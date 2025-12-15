package com.smartlogi.sdms.features.auth;

import com.smartlogi.sdms.features.auth.dto.AuthResponse;
import com.smartlogi.sdms.features.auth.dto.LoginRequest;
import com.smartlogi.sdms.security.core.SecurityUser;
import com.smartlogi.sdms.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Z. Authentification", description = "Endpoints pour la gestion de l'authentification (Login).")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    @Operation(summary = "Authentification utilisateur", description = "Permet à un Gestionnaire, Livreur ou Client de se connecter avec email et mot de passe pour obtenir un JWT.")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. Tenter l'authentification via le Manager (appelle CustomUserDetailsService)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. Si succès, placer l'objet Authentication dans le contexte de sécurité
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Générer le Token JWT
        String jwt = tokenProvider.generateToken(authentication);

        // 4. Récupérer les détails de l'utilisateur pour la réponse
        SecurityUser userDetails = (SecurityUser) authentication.getPrincipal();

        // 5. Renvoyer la réponse avec le token et les infos utiles
        return ResponseEntity.ok(new AuthResponse(
                jwt,
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getRole().name()
        ));
    }
}