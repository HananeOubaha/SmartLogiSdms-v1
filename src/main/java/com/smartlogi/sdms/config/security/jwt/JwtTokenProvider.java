package com.smartlogi.sdms.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    private Key cachedKey;

    // Clé de signature JWT - cachée pour éviter les recalculs
    private Key key() {
        if (cachedKey == null) {
            cachedKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
        return cachedKey;
    }

    // 1. Générer le Token JWT
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        // Extraire les rôles et les convertir en une chaîne séparée par des virgules
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username) // Le sujet du token (l'utilisateur)
                .claim("roles", roles) // Ajouter les rôles comme claim personnalisé
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key(), SignatureAlgorithm.HS512) // Utiliser HS512 et notre clé secrète
                .compact();
    }

    // 2. Extraire le nom d'utilisateur (Subject) du Token
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 2.1. Extraire les rôles du Token
    public List<String> getRolesFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String roles = claims.get("roles", String.class);
            if (roles != null && !roles.isEmpty()) {
                return Arrays.asList(roles.split(","));
            }
            return List.of();
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("Erreur lors de l'extraction des rôles du token", ex);
            return List.of();
        }
    }

    // 3. Valider le Token JWT
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT Invalide: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT Expiré: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT Non Supporté: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("Chaîne JWT vide ou nulle: {}", ex.getMessage());
        }
        return false;
    }
}