package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.GenericGenerator; // Import nécessaire pour GenericGenerator

import java.util.List;
import java.util.UUID; // Import nécessaire pour UUID

@Entity
@Table(name = "client_expediteur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientExpéditeur {
//testing
    @Id
    // Génère l'ID en utilisant la stratégie UUID2 d'Hibernate
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    // Mappe l'objet Java UUID vers la colonne VARCHAR(36) de la base de données
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id; // Changé de Long à UUID

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
    // Note: Assurez-vous que l'entité Colis utilise aussi UUID pour ses clés étrangères.
    @OneToMany(mappedBy = "clientExpediteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Colis> colisEnvoyes;

    //hello
}