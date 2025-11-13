package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
// Suppression de l'import org.hibernate.annotations.GenericGenerator
import java.util.List;
// Suppression de l'import java.util.UUID

@Entity
@Table(name = "client_expediteur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientExpéditeur {
    //testing
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // Mappe l'objet Java String vers la colonne VARCHAR(36) de la base de données
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id; // <-- CORRECTION: Changé de UUID à String

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", length = 100)
    private String prenom;

    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "adresse", length = 255)
    private String adresse;

    // Relation: Un expéditeur peut envoyer plusieurs colis.
    @OneToMany(mappedBy = "clientExpediteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Colis> colisEnvoyes;

    /**
     * Ajout de la logique de génération UUID (en tant que String)
     * AVANT la première insertion dans la base de données.
     */
    @PrePersist
    protected void onPrePersist() {
        if (this.id == null) {
            // Génère un UUID et le stocke comme String
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
//hello
}