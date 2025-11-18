package com.smartlogi.sdms.DTO.auth;

public record JwtResponse(String accessToken, String tokenType) {
    public JwtResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}
