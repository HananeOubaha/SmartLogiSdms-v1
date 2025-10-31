package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "colis_produit")
@Data
@NoArgsConstructor
public class ColisProduit {

    // Utilisation de la clé primaire composée
    @EmbeddedId
    private ColisProduitId id;

    // Mappe l'ID de la clé composée avec l'entité Colis
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("colisId")
    @JoinColumn(name = "id_colis")
    private Colis colis;

    // Mappe l'ID de la clé composée avec l'entité Produit
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produitId")
    @JoinColumn(name = "id_produit")
    private Produit produit;

    private Integer quantite;

    @Column(name = "prix_unitaire")
    private Double prixUnitaire;

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout;

    // Constructeur utilitaire pour la création
    public ColisProduit(Colis colis, Produit produit, Integer quantite) {
        this.colis = colis;
        this.produit = produit;
        this.quantite = quantite;
        this.dateAjout = LocalDateTime.now();
    }
}