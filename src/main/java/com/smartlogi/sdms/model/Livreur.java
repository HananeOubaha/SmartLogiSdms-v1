package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "livreur")
@Data
public class Livreur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String telephone;
    private String vehicule;

    // Vous pouvez simplifier ou ajouter d'autres champs de votre diagramme ici
}