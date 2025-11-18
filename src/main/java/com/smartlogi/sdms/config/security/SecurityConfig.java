package com.smartlogi.sdms.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource; // <-- Nouvel import !
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// L'import de CorsFilter devient facultatif si on ne l'utilise plus directement

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // --- 1. PasswordEncoder inchangé ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- 2. Configuration CORS stricte (CORRIGÉE : Renvoie CorsConfigurationSource) ---
    // Renommer la méthode et changer le type de retour
    @Bean
    public CorsConfigurationSource corsConfigurationSource() { // <--- Changement ici
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // N'autoriser que les frontends internes
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:3000", "http://localhost:8080"));

        // Autoriser les méthodes REST classiques
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Autoriser les headers
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Important
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return source; // <--- Retourne l'objet source directement
    }

    // --- 3. Configuration principale de la Sécurité (CORRIGÉE) ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver le CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Ajouter le filtre CORS en premier
                // Spring Security détectera automatiquement le Bean CorsConfigurationSource
                // et l'utilisera si la lambda est vide ou si on appelle .withDefaults()
                .cors(cors -> {}) // Ou .cors(withDefaults()) ou .cors(Customizer.withDefaults())
                // Laisser la lambda vide suffit.

                // Activer le mode stateless (très important pour JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Définition des règles d'autorisation
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}