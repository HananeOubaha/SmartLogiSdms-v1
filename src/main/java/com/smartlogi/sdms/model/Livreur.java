package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "livreur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livreur {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", length = 100)
    private String prenom;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "vehicule", length = 50)
    private String vehicule;

    // Relation ManyToOne vers Zone (un livreur est assigné à une seule zone)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", referencedColumnName = "id")
    private Zone zone;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    // 🔐 Champs sécurité
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "ROLE_DELIVERYMAN";

    // Relation: Un livreur peut être assigné à plusieurs colis.
    @OneToMany(mappedBy = "livreur")
    private List<Colis> colisAssignes;

    @PrePersist
    protected void onPrePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}