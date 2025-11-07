package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor; // Ajouté pour JPA
import lombok.AllArgsConstructor; // Ajouté pour Lombok
import org.hibernate.annotations.GenericGenerator; // Import nécessaire
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "zone")
@Data
@NoArgsConstructor // Ajouté pour JPA
@AllArgsConstructor // Ajouté pour Lombok
public class Zone {

    @Id
    // Génère l'ID en utilisant la stratégie UUID2 d'Hibernate
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    // Mappe l'objet Java UUID vers la colonne VARCHAR(36) de la base de données
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id; // <-- CORRECTION: Changé de Long à UUID

    @Column(name = "nom", nullable = false) // Assure la non-nullité et gère la longueur par défaut ou via @Size dans le DTO
    private String nom;

    @Column(name = "code_postal")
    private String codePostal;

    // Relation inverse : Une zone peut contenir plusieurs colis (pour la gestion logistique)
    @OneToMany(mappedBy = "zone")
    private List<Colis> colisDansZone;
}