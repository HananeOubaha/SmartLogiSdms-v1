package com.smartlogi.sdms.security;

import com.smartlogi.sdms.enums.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class SecurityUser implements UserDetails {
    private String UserID;

    private String username;

    private String password;

    private Role role;

    public SecurityUser(String userID, String username, String password, Role role) {
        this.UserID = userID;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
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
