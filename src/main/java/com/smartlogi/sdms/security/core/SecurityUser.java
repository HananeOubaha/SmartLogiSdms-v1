package com.smartlogi.sdms.security.core;

import com.smartlogi.sdms.enums.Role;
import lombok.Data; // Génère getters, setters, etc.
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class SecurityUser implements UserDetails {

    // CORRECTION : 'userId' en minuscule (camelCase)
    private String userId;

    private String username;

    private String password;

    private Role role;

    // Constructeur corrigé pour utiliser le champ 'userId'
    public SecurityUser(String userId, String username, String password, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Transforme l'Enum Role en une autorité Spring Security
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    // --- Méthodes UserDetails ---

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Le compte n'expire jamais (pour la v0.2.0)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Le compte n'est jamais verrouillé (pour la v0.2.0)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Les crédentiels n'expirent jamais (pour la v0.2.0)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Le compte est toujours activé (pour la v0.2.0)
    @Override
    public boolean isEnabled() {
        return true;
    }
}