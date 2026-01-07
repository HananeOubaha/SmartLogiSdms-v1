package com.smartlogi.sdms.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Suppression de l'import java.util.UUID

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pour l'expéditeur : création, consultation et mise à jour.")
public class ClientExpéditeurDto {

    private String id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String nom;

    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caractères")
    private String password;
}
