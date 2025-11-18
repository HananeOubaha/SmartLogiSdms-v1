package com.smartlogi.sdms.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    // Clé de signature JWT
    private Key key() {
        // Utilise la clé secrète du fichier de configuration pour créer une clé de sécurité.
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
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

    // 3. Valider le Token JWT
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            System.err.println("Token JWT Invalide");
        } catch (ExpiredJwtException ex) {
            System.err.println("Token JWT Expiré");
        } catch (UnsupportedJwtException ex) {
            System.err.println("Token JWT Non Supporté");
        } catch (IllegalArgumentException ex) {
            System.err.println("Chaîne JWT vide ou nulle");
        }
        return false;
    }
}