package com.smartlogi.sdms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String prenom;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telephone;

    @Column(nullable = false, length = 255)
    private String adresse;

    // 🔐 Champs sécurité
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "ROLE_CLIENT";

    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "clientExpediteur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Colis> colisEnvoyes;

    @PrePersist
    protected void onPrePersist() {
        dateCreation = LocalDateTime.now();
    }
}
