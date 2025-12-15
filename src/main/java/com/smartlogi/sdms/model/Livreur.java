package com.smartlogi.sdms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.smartlogi.sdms.enums.Role;
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

    @Column(name = "zone_assignee", length = 100)
    private String zoneAssignee;

    // 🔥 Nouveau champ email
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false)
    private String password; // Sera stocké encrypté (BCrypt)

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.ROLE_LIVREUR;

    @OneToMany(mappedBy = "livreur")
    private List<Colis> colisAssignes;

    @PrePersist
    protected void onPrePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.role == null) {
            this.role = Role.ROLE_LIVREUR;
        }
    }
}
