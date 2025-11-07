package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Ajouté pour cohérence
import org.hibernate.annotations.GenericGenerator; // Import nécessaire
import java.util.List;
import java.util.UUID; // <-- Import nécessaire

@Entity
@Table(name = "produit")
@Data
@NoArgsConstructor
@AllArgsConstructor // Ajouté pour cohérence
public class Produit {

    @Id
    // Génère l'ID en utilisant la stratégie UUID2 d'Hibernate
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    // Mappe l'objet Java UUID vers la colonne VARCHAR(36) de la base de données
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id; // <-- CORRECTION: Changé de Long à UUID

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "categorie", length = 50)
    private String categorie;

    // Pour le poids, nous utilisons Double sans 'scale' pour éviter l'erreur d'Hibernate
    @Column(name = "poids")
    private Double poids; // Poids unitaire

    // Pour le prix, nous utilisons Double. Le mapping BDD dépendra du dialecte.
    @Column(name = "prix")
    private Double prix; // Prix unitaire

    // Relation inverse pour la table de jointure (Colis_Produit)
    // MappedBy doit pointer vers le champ 'produit' dans l'entité ColisProduit.
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColisProduit> colisContenant;
}