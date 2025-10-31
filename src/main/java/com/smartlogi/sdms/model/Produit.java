package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "produit")
@Data
@NoArgsConstructor
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "categorie", length = 50)
    private String categorie;

    // CORRECTION ICI : Pour les types Double (flottants), nous retirons 'scale'.
    // Nous laissons la précision si nécessaire, mais le poids est généralement juste un Double.
    @Column(name = "poids")
    private Double poids; // Poids unitaire

    // Pour le prix (monnaie), le type NUMERIC est préférable.
    // Le mapping JPA par défaut de Double sans annotations est FLOAT ou DOUBLE PRECISION.
    // SI vous utilisez @Column(precision=10, scale=2) sur un Double, retirez 'scale'.
    @Column(name = "prix")
    private Double prix; // Prix unitaire

    // Relation inverse pour la table de jointure (Colis_Produit)
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColisProduit> colisContenant;
}