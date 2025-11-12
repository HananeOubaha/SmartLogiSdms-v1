
-----

# 📦 Smart Delivery Management System (SDMS) – V 0.1.0

## 📝 Description du Projet

Le **Smart Delivery Management System (SDMS)** est une solution web backend conçue pour moderniser et automatiser la gestion des opérations logistiques de la société **SmartLogi** (livraison de colis au Maroc).

Ce système remplace les processus manuels (Excel et registres papier) par une plateforme centralisée et traçable, couvrant le cycle de vie complet du colis, de la création de la demande à la livraison finale.

-----

## 🚀 Fonctionnalités Implémentées (V0.1.0)

Cette version couvre le **CRUD complet** des entités de base et l'implémentation du **flux logistique central**.

### I. Gestion des Entités de Base (CRUD Complet)

| Entité | Endpoint Principal | Rôle |
| :--- | :--- | :--- |
| **Zone** | `/api/zones` | Gestion des zones géographiques pour la planification. |
| **Client Expéditeur** | `/api/clients-expediteurs` | Gestion des clients émetteurs (avec validation d'unicité Email). |
| **Destinataire** | `/api/destinataires` | Gestion des bénéficiaires des colis. |
| **Livreur** | `/api/livreurs` | Gestion du personnel de livraison. |

### II. Flux Logistique de Base

| Fonctionnalité | Description du Déclencheur | Statuts Clés |
| :--- | :--- | :--- |
| **Création Colis** | Soumission de la demande par le Client Expéditeur (Validation des IDs UUID des acteurs). | **`CRÉÉ`** |
| **Affectation Livreur** | Le Gestionnaire affecte un Livreur au colis. | **`EN_TRANSIT`** |
| **Mise à Jour du Statut**| Le Livreur met à jour l'état du colis (Collecte, Livraison, Échec...). | **`COLLECTÉ`, `LIVRÉ`, etc.** |
| **Traçabilité** | **Enregistrement automatique** de tous les changements de statut dans la table `historique_livraison`. | Traçabilité complète |

-----

## 🛠️ Exigences Techniques & Architecture

### Technologies Utilisées

* **Backend :** **Spring Boot** (Java 17+)
* **Base de Données :** **PostgreSQL**
* **Migrations :** **Liquibase** (Schéma géré)
* **ORM :** Spring Data JPA / Hibernate
* **Mapping :** **MapStruct** (Entité ↔ DTO)
* **Documentation :** **Swagger / OpenAPI** (Via Springdoc)
* **Logs :** SLF4J

### Architecture en Couches

Le projet suit une architecture stricte de type **Controller → Service → Repository**.

### Stratégie d'ID (UUID)

Tous les identifiants sont gérés par des UUID (Universally Unique Identifiers) :

* **Java (Code/Logique) :** Utilise l'objet **`java.util.UUID`**.
* **Base de Données (PostgreSQL) :** Stocké en **`VARCHAR(36)`**.

-----

## 🖼️ Diagramme de Classes UML

![SmartLogi SDMS v1.png](src/SmartLogi%20SDMS%20v1.png)
`![Diagramme de Classes UML de l'application](diagramme_classes.png)`


```

```

-----

## ⚙️ Installation et Lancement

### Prérequis

1.  Java 17+
2.  PostgreSQL (avec les identifiants configurés dans `application.yml`)

### Étapes de Lancement

1.  **Cloner le dépôt :**
    ```bash
    git clone https://github.com/votre_utilisateur/SmartLogiSdms.git
    cd SmartLogiSdms
    ```
2.  **Démarrage :** Exécutez l'application via la méthode `main` dans **`SmartLogiSdmsApplication.java`**.
    *(Au démarrage, **Liquibase** créera automatiquement toutes les tables UUID dans PostgreSQL.)*

### Accès à l'API

* **URL de Base :** `http://localhost:8080/api`
* **Documentation Swagger :** `http://localhost:8080/swagger-ui.html`

-----

## ⏭️ Prochaines Fonctionnalités

1.  **Pagination et Filtres Colis :** (Début de la prochaine étape) Implémentation de la recherche avancée par statut, zone, date, etc.
2.  **Requêtes Agrégées :** Calcul du poids total et nombre de colis par Livreur/Zone.
3.  **Notifications :** Implémentation du module SMTP pour les alertes email (Bonus).