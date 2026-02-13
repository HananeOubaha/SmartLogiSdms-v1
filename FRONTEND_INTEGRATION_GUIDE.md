# 🚚 Guide d'Intégration Frontend - SmartLogiSdms

Ce document contient toutes les informations techniques nécessaires pour connecter votre application Frontend (Angular, React, Vue, etc.) au Backend **SmartLogiSdms**.

---

## 🔐 Architecture de Sécurité (JWT)

Le backend utilise **Spring Security** avec des tokens **JWT (JSON Web Token)**. L'authentification est **Stateless**.

### Flux d'Authentification
1.  **Login** : Envoyer une requête `POST` à `/api/auth/login`.
2.  **Stockage** : Récupérer le `accessToken` et le stocker (ex: `localStorage` ou `sessionStorage`).
3.  **Requêtes Authentifiées** : Pour chaque requête protégée, ajouter le Header HTTP suivant :
    `Authorization: Bearer <votre_token>`

### Rôles Utilisateurs
Trois rôles principaux définissent les permissions :
- `ROLE_MANAGER` : Accès complet à la gestion des livreurs, clients, zones et colis.
- `ROLE_CLIENT` : Peut créer des colis, voir ses propres colis, et créer des destinataires.
- `ROLE_DELIVERYMAN` : Peut mettre à jour le statut des colis qui lui sont assignés.

---

## 📡 Endpoints API (Référence)

### 1. Authentification (`/api/auth`)
| Méthode | Endpoint | Accès | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Public | Authentifie l'utilisateur et retourne un JWT. |

### 2. Gestion des Colis (`/api/colis`)
| Méthode | Endpoint | Accès | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/colis` | `ROLE_CLIENT` | Créer une nouvelle demande de colis. |
| `GET` | `/api/colis/mes-colis` | `ROLE_CLIENT` | Liste les colis du client connecté. |
| `GET` | `/api/colis` | `ROLE_MANAGER` | Liste tous les colis du système. |
| `GET` | `/api/colis/{id}` | Public | Suivi d'un colis spécifique par son ID. |
| `PUT` | `/api/colis/assigner/{colisId}` | `ROLE_MANAGER` | Assigner un livreur à un colis (`?livreurId=...`). |
| `PUT` | `/api/colis/statut/{colisId}` | `ROLE_DELIVERYMAN` | Mettre à jour le statut et ajouter un commentaire. |
| `DELETE` | `/api/colis/{id}` | `ROLE_MANAGER` | Supprimer un colis. |

### 3. Gestion des Clients & Livreurs
| Méthode | Endpoint | Accès | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/clients-expediteurs` | Public | Inscription d'un nouveau client. |
| `GET` | `/api/clients-expediteurs` | `ROLE_MANAGER` | Liste tous les clients. |
| `GET` | `/api/livreurs` | `ROLE_MANAGER` | Liste tous les livreurs. |
| `POST` | `/api/livreurs` | `ROLE_MANAGER` | Créer un compte livreur. |

---

## 📦 Structures de Données (DTO)

### Login Request
```json
{
  "username": "user123",
  "password": "password123"
}
```

### Login Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

### Création de Colis (`ColisCreationDto`)
```json
{
  "description": "Fragile - Matériel informatique",
  "poids": 2.5,
  "villeDestination": "Casablanca",
  "priorite": "HAUTE",
  "clientExpediteurId": "uuid-client-123",
  "destinataireId": "uuid-dest-456",
  "zoneId": "uuid-zone-789"
}
```

---

## 💡 Enums à connaître

### Statut du Colis (`StatutColis`)
- `CREE` : Demande créée par le client.
- `COLLECTE` : Colis récupéré chez l'expéditeur.
- `EN_STOCK` : Arrivé au dépôt.
- `EN_TRANSIT` : En voyage vers la ville de destination.
- `EN_TOURNEE` : En cours de livraison finale.
- `LIVRE` : Livré avec succès.
- `ANNULE` : Annulé.
- `ECHEC_LIVRAISON` : Tentative de livraison échouée.

### Priorité (`PrioriteColis`)
- `BASSE`, `NORMALE`, `HAUTE`, `URGENTE`

---

## 🛠️ Configuration Utile

- **URL de Base** : `http://localhost:8080` (par défaut)
- **CORS** : Le backend est configuré pour accepter les requêtes venant de :
  - `http://localhost:4200` (Angular)
  - `http://localhost:3000` (React/Next)
- **Documentation Swagger** : Une interface interactive est disponible sur :
  `http://localhost:8080/swagger-ui/index.html` (très utile pour tester les endpoints en direct).
