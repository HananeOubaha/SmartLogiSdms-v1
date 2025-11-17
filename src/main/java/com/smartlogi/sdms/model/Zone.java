package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
// Suppression de l'import org.hibernate.annotations.GenericGenerator
// Suppression de l'import java.util.UUID
import java.util.List;

@Entity
@Table(name = "zone")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id; // <-- CORRECTION: Changé de UUID à String

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "code_postal")
    private String codePostal;

    // Relation inverse : Une zone peut contenir plusieurs colis (pour la gestion logistique)
    @OneToMany(mappedBy = "zone")
    private List<Colis> colisDansZone;

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