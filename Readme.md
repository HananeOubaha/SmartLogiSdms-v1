# 📦 SmartLogi SDMS - Smart Delivery Management System

**Version :** 0.2.1  
**Statut :** En développement actif  
**Auteur :** Nafia Akdi

---

## 📖 Description du Projet

**SmartLogi SDMS** est une solution backend robuste de gestion logistique dédiée au suivi complet du cycle de livraison. Elle permet de gérer efficacement la collecte, le stockage, la livraison et le suivi des colis, tout en assurant une gestion fine des acteurs (clients expéditeurs, livreurs, gestionnaires).

Cette version **v0.2.1** met l'accent sur la **sécurité avancée** (Authentification Stateless JWT) et prépare l'intégration d'une authentification hybride (OAuth2 + JWT).

---

## 🚀 Fonctionnalités Clés

### 📦 Gestion Logistique
- **Gestion des Colis** : Création, modification, suppression et suivi détaillé.
- **Workflow de Livraison** : Suivi des statuts (CRÉÉ, EN_TRANSIT, LIVRÉ, etc.) avec historique complet.
- **Gestion des Zones** : Définition de zones géographiques pour l'assignation des livraisons.
- **Gestion des Produits** : Catalogue de produits pouvant être inclus dans les colis.

### 👥 Gestion des Acteurs
- **Livreurs** : Gestion des profils, assignation de zones et de véhicules.
- **Clients Expéditeurs** : Gestion des comptes clients et de leurs expéditions.
- **Destinataires** : Gestion des informations de livraison.

### 🔒 Sécurité & Accès (Nouveau v0.2.0)
- **Authentification Stateless** : Basée sur **JWT (JSON Web Tokens)**.
- **Contrôle d'Accès (RBAC)** :
  - `ROLE_MANAGER` : Accès complet (Admin).
  - `ROLE_DELIVERYMAN` : Accès limité à ses livraisons.
  - `ROLE_CLIENT` : Accès limité à ses créations et suivis.
- **Protection des Mots de Passe** : Encodage BCrypt.
- **CORS** : Configuration stricte pour les frontends autorisés.

---

## 🛠️ Stack Technique

- **Langage** : Java 17
- **Framework** : Spring Boot 3.5.7
- **Base de Données** : PostgreSQL 15+
- **Migration de Données** : Liquibase
- **ORM** : Spring Data JPA / Hibernate
- **Mapping** : MapStruct
- **Sécurité** : Spring Security 6, JJWT
- **Documentation API** : SpringDoc OpenAPI (Swagger UI)
- **Outils de Build** : Maven

---

## 🏗️ Architecture et Modèle de Données

Le projet suit une architecture en couches classique :
`Controller` → `Service` → `Repository` → `Database`

### Entités Principales
- **User / Role** : Gestion des identités et permissions.
- **Colis** : Entité centrale liée à un Expéditeur, un Destinataire, un Livreur et une Zone.
- **HistoriqueLivraison** : Trace chaque changement d'état d'un colis.
- **Livreur** : Lié à une Zone spécifique.

---

## ⚙️ Installation et Démarrage

### Prérequis
- Java JDK 17+
- Maven 3.8+
- PostgreSQL (local ou Docker)

### 1. Configuration de la Base de Données
Créez une base de données PostgreSQL nommée `smartlogi_db`.
Assurez-vous que les identifiants dans `src/main/resources/application.yml` correspondent à votre installation :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/smartlogi_db
    username: postgres
    password: votre_mot_de_passe
```

### 2. Compilation et Installation
```bash
mvn clean install
```

### 3. Lancement de l'Application
```bash
mvn spring-boot:run
```
L'application démarrera sur `http://localhost:8080`.

---

## 📚 Documentation API (Swagger)

Une fois l'application lancée, la documentation interactive de l'API est disponible à l'adresse :

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### Endpoints Principaux

| Méthode | Endpoint | Description | Rôle Requis |
|---------|----------|-------------|-------------|
| `POST` | `/auth/login` | Connexion et obtention du JWT | Public |
| `POST` | `/api/colis` | Créer un nouveau colis | CLIENT, MANAGER |
| `GET` | `/api/colis` | Lister les colis | MANAGER |
| `PATCH` | `/api/colis/{id}/statut` | Mettre à jour le statut | LIVREUR, MANAGER |
| `POST` | `/api/livreurs` | Créer un livreur | MANAGER |

---

## 🧪 Tests

Le projet inclut des tests unitaires et d'intégration (JUnit 5, Mockito).

Pour lancer les tests :
```bash
mvn test
```

---

## 🐳 Docker (À venir)

Un fichier `Dockerfile` et `docker-compose.yml` seront ajoutés pour faciliter le déploiement conteneurisé de l'application et de la base de données.

---

## 📝 Licence

Ce projet est développé dans un cadre pédagogique pour **Simplon**.
Tous droits réservés.
