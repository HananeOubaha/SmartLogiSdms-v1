package com.smartlogi.sdms.repository;

import com.smartlogi.sdms.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, String> {

    // Pour la recherche par nom, si le gestionnaire en a besoin
    Optional<Produit> findByNom(String nom);
}