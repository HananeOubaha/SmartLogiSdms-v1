package com.smartlogi.sdms.security.jwt;

import com.smartlogi.sdms.security.core.SecurityUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    // Clé secrète pour signer les tokens (devrait être dans application.yml en prod)
    // Doit être assez longue pour HS512 (min 512 bits / 64 caractères)
    private final String jwtSecret = "SmartLogiSecretKeyForJwtTokenGenerationMustBeVeryLongAndSecureToWorkWithHS512Algorithm";

    // Durée de validité du token (ex: 24 heures en millisecondes)
    private final long jwtExpirationInMs = 86400000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Génère un token JWT à partir de l'authentification réussie.
     */
    public String generateToken(Authentication authentication) {
        SecurityUser userPrincipal = (SecurityUser) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Récupérer le rôle
        String role = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Email comme sujet
                .claim("userId", userPrincipal.getUserId()) // ID métier
                .claim("role", role) // Rôle
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrait l'email (username) du token JWT.
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Valide le token JWT.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            System.err.println("Signature JWT invalide");
        } catch (MalformedJwtException ex) {
            System.err.println("Token JWT malformé");
        } catch (ExpiredJwtException ex) {
            System.err.println("Token JWT expiré");
        } catch (UnsupportedJwtException ex) {
            System.err.println("Token JWT non supporté");
        } catch (IllegalArgumentException ex) {
            System.err.println("Chaîne claims JWT vide");
        }
        return false;
    }
}