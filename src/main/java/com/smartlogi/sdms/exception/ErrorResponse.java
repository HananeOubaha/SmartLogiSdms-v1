package com.smartlogi.sdms.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error; // Description générale de l'erreur (ex: Resource Not Found)
    private String message; // Message détaillé (ex: Zone non trouvée avec l'ID: 10)
    private String path; // Chemin de la requête (URI)
}