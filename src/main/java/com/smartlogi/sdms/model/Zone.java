package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
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
    private String id;

    @Column(name = "nom", nullable = false, unique = true)
    private String nom;

    @Column(name = "code_postal")
    private String codePostal;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    // Relation inverse : Une zone peut contenir plusieurs colis
    @OneToMany(mappedBy = "zone")
    private List<Colis> colisDansZone;

    // Relation inverse : Une zone peut avoir plusieurs livreurs
    @OneToMany(mappedBy = "zone")
    private List<Livreur> livreurs;

    @PrePersist
    protected void onPrePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}