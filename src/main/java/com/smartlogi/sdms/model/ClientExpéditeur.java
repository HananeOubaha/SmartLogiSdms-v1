package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "client_expediteur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientExpéditeur {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

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

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    // Relation: Un expéditeur peut envoyer plusieurs colis.
    @OneToMany(mappedBy = "clientExpediteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Colis> colisEnvoyes;

    @PrePersist
    protected void onPrePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}