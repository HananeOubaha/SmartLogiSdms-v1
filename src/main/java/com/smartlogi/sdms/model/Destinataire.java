package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "destinataire")
@Data
public class Destinataire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
}