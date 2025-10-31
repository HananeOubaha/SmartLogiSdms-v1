package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "client_expediteur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientExpéditeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}