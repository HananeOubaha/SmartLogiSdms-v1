package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator; // Import nécessaire
import java.util.List;
import java.util.UUID; // Import nécessaire

@Entity
@Table(name = "livreur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livreur {

    @Id
    // Génère l'ID en utilisant la stratégie UUID2 d'Hibernate
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    // Mappe l'objet Java UUID vers la colonne VARCHAR(36) de la base de données
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id; // <-- CORRECTION: Changé de Long à UUID

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
}