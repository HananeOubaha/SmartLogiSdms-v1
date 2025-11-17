package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
// Suppression de l'import org.hibernate.annotations.GenericGenerator
import java.util.List;
// Suppression de l'import java.util.UUID

@Entity
@Table(name = "destinataire")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Destinataire {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id; // <-- CORRECTION: Changé de UUID à String

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", length = 100)
    private String prenom;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "adresse", length = 255)
    private String adresse;

    // Relation: Un destinataire peut recevoir plusieurs colis.
    // MappedBy doit pointer vers le champ Destinataire dans l'entité Colis.
    @OneToMany(mappedBy = "destinataire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Colis> colisReçus;

    /**
     * Logique pour générer l'ID UUID sous forme de String AVANT l'insertion.
     */
    @PrePersist
    protected void onPrePersist() {
        if (this.id == null) {
            // Génère un UUID et le stocke comme String
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}