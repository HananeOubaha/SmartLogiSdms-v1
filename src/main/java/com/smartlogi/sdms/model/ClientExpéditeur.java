package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "client_expediteur")
@Data
public class ClientExpéditeur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    @Column(unique = true)
    private String email;
    private String telephone;
    private String adresse;

    // Relation : Un client peut avoir plusieurs colis
    @OneToMany(mappedBy = "clientExpediteur", cascade = CascadeType.ALL)
    private List<Colis> colisEnvoyes;
}