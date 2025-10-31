package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "zone")
@Data
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(name = "code_postal")
    private String codePostal;
}