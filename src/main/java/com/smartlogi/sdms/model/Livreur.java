package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
// Suppression de l'import org.hibernate.annotations.GenericGenerator
import java.util.List;
// Suppression de l'import java.util.UUID

@Entity
@Table(name = "livreur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livreur {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id; // <-- CORRECTION: Changé de UUID à String

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", length = 100)
    private String prenom;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "vehicule", length = 50)
    private String vehicule;

    // NOTE: Si Zone est une Entité complète (ce qui est le cas),
    // ce champ devrait être remplacé par une relation ManyToOne vers Zone.
    @Column(name = "zone_assignee", length = 100)
    private String zoneAssignee;

    // Relation: Un livreur peut être assigné à plusieurs colis.
    // MappedBy pointe vers le champ 'livreur' dans l'entité Colis.
    @OneToMany(mappedBy = "livreur")
    private List<Colis> colisAssignes;

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