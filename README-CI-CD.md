<# 🚀 SmartLogi SDMS v0.3.0 - Pipeline CI/CD Complet

## 📋 Sommaire Rapide

| Élément | Statut | Détails |
|---------|--------|---------|
| **Pipeline Jenkins** | ✅ Configuré | Jenkinsfile complet avec 10 étapes |
| **Tests Automatisés** | ✅ Intégré | JUnit 5, Mockito, H2 Database |
| **Analyse Qualité** | ✅ SonarQube | Quality Gate avec seuils définis |
| **Conteneurisation** | ✅ Docker | Multi-stage build optimisé |
| **Orchestration** | ✅ Docker Compose | All-in-one (PostgreSQL, SonarQube, Jenkins) |
| **Sécurité** | ✅ JWT + CORS | Spring Security 6, RBAC |
| **Documentation** | ✅ Complète | Architecture, setup, dépannage |

---

## 🏗️ Architecture du Pipeline

```
Git Push → GitHub Webhook → Jenkins → Compilation → Tests → SonarQube → Quality Gate → Docker Build → Push Registry
```

### 10 Étapes du Pipeline

```groovy
1. 📋 Préparation       (Clone, nettoyage)
2. 🔨 Compilation        (Maven compile)
3. 🧪 Tests Unitaires    (JUnit 5)
4. 📊 Couverture Code    (JaCoCo)
5. 🔍 Analyse SonarQube  (Scan)
6. ⚖️  Quality Gate      (Blocage si échoué)
7. 📦 Build Package      (JAR exécutable)
8. 🐳 Build Docker       (Image)
9. 📤 Push Docker        (Registry)
10. 📚 Archivage         (Artefacts)
```

---

## 🚀 Démarrage Rapide

### Option 1 : Automatique (Recommandé)

#### Windows (PowerShell)
```powershell
# Exécuter le script de setup
.\setup-ci-cd.bat

# Le script va :
# - Vérifier les prérequis
# - Builder l'application
# - Lancer tous les services Docker
# - Afficher les URLs d'accès
```

#### Linux/Mac
```bash
# Donner les permissions d'exécution
chmod +x setup-ci-cd.sh

# Exécuter le script
./setup-ci-cd.sh
```

### Option 2 : Manuel

```bash
# 1. Compiler
mvn clean package -DskipTests

# 2. Lancer les services
docker-compose up -d

# 3. Vérifier
docker-compose ps
curl http://localhost:8080/api/actuator/health
```

---

## 📱 Accès aux Services

Une fois démarré, accédez à :

| Service | URL | Identifiants |
|---------|-----|-------------|
| **SmartLogi SDMS** | http://localhost:8080/api | - |
| **Swagger UI** | http://localhost:8080/api/swagger-ui.html | - |
| **PgAdmin** | http://localhost:5050 | admin@smartlogi.com / admin123 |
| **SonarQube** | http://127.0.0.1:9000 | admin / admin |
| **Jenkins** | http://localhost:8081 | À configurer |

---

## 📂 Structure des Fichiers

```
SmartLogiSdms/
├── Jenkinsfile                      # Pipeline déclaratif Jenkins
├── Dockerfile                       # Multi-stage build Docker
├── docker-compose.yml               # Orchestration complète
├── CI-CD-DOCUMENTATION.md           # Documentation détaillée
├── setup-ci-cd.sh                   # Setup automatique (Linux/Mac)
├── setup-ci-cd.bat                  # Setup automatique (Windows)
├── .env.example                     # Variables d'environnement
├── .gitignore                       # Exclusions Git
├── pom.xml                          # Maven (JaCoCo, SonarQube, tests)
├── src/main/resources/
│   ├── application.yml              # Configuration par défaut
│   ├── application-docker.yml       # Profil Docker
│   ├── application-ci.yml           # Profil CI
│   └── db/changelog/                # Scripts Liquibase
└── src/test/java/                   # Tests unitaires & d'intégration
```

---

## 🔧 Configuration Jenkins

### 1. Installer les Plugins Requis

**Manage Jenkins → Manage Plugins**

```
- Git Plugin
- Pipeline Plugin
- Docker Pipeline
- SonarQube Scanner
- JaCoCo Plugin
- Email Extension Plugin
```

### 2. Ajouter les Credentials

**Manage Jenkins → Manage Credentials → Global**

```groovy
// GitHub Token
ID: github-token
Type: Secret text
Secret: ghp_xxxxxxxxxxxx

// SonarQube Token
ID: sonar-token
Type: Secret text
Secret: squ_xxxxxxxxxxxx

// Docker Hub
ID: docker-hub-credentials
Type: Username with password
Username: <votre_username>
Password: <votre_token>
```

### 3. Créer le Pipeline Job

**New Item → Pipeline**

```groovy
Name: SmartLogiSDMS-CI
Pipeline → Pipeline script from SCM
SCM: Git
Repository URL: https://github.com/HananeOubaha/SmartLogiSdms-v1.git
Branch: */feat/jwt_security
Script Path: Jenkinsfile
Build Triggers: GitHub hook trigger for GITscm polling
```

### 4. Configurer SonarQube

**Manage Jenkins → Configure System → SonarQube**

```
Server name: SonarQube
Server URL: http://127.0.0.1:9000
Server authentication token: sonar-token
```

---

## ✅ Quality Gate SonarQube

Le projet doit respecter ces critères :

```
✅ Couverture de code        ≥ 80%
✅ Duplication de code       ≤ 5%
✅ Code smells              < 30
✅ Bugs                      = 0
✅ Vulnérabilités            = 0
✅ Security hotspots reviewed 100%
```

**Le pipeline est bloqué si ces critères ne sont pas respectés.**

---

## 🐳 Docker & Docker Compose

### Lancer les services

```bash
# Démarrer tous les services
docker-compose up -d

# Voir l'état
docker-compose ps

# Voir les logs
docker-compose logs -f sdms
docker-compose logs -f postgres
docker-compose logs -f sonarqube

# Arrêter
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

### Services disponibles

```yaml
Services:
  postgres      # PostgreSQL 15 (port 5432)
  pgadmin       # Interface PgAdmin (port 5050)
  sonarqube     # SonarQube (port 9000)
  sdms          # Application SDMS (port 8080)
  jenkins       # Jenkins (port 8081)

Networks:
  smartlogi-network (bridge)

Volumes:
  postgres_data, pgadmin_data, sonarqube_data, etc.
```

---

## 🧪 Tester Localement

### Compilation

```bash
mvn clean compile
```

### Tests Unitaires

```bash
# Tous les tests
mvn test

# Avec profil CI
mvn test -Dspring.profiles.active=ci

# Un test spécifique
mvn test -Dtest=AuthControllerTest
```

### Analyse SonarQube

```bash
mvn sonar:sonar \
  -Dsonar.host.url=http://127.0.0.1:9000 \
  -Dsonar.login=admin
```

### Build complet

```bash
mvn clean package -DskipTests
```

---

## 📊 Rapports et Métriques

### Couverture JaCoCo

```
Rapport HTML: target/site/jacoco/index.html
Rapport XML: target/site/jacoco/jacoco.xml
```

### SonarQube

```
URL: http://127.0.0.1:9000
Dashboard: Projects → SmartLogiSdms
```

### Jenkins

```
URL: http://localhost:8081
Build History: SmartLogiSDMS-CI → Build History
Console Output: SmartLogiSDMS-CI → #<number> → Console Output
```

---

## 🔐 Sécurité et Secrets

### Variables sensibles (Ne PAS commiter)

```
.env                    # Fichier local (ignoré)
.env.*.local           # Fichiers locaux (ignorés)
application-secrets.yml # Fichier local (ignoré)
```

### Utiliser les variables d'environnement

```bash
# Local
export JWT_SECRET="votre_secret"
export SPRING_DATASOURCE_PASSWORD="password"

# Docker
docker run -e JWT_SECRET="secret" smartlogi/sdms

# Docker Compose
# Utiliser le fichier .env
```

### Credentials Jenkins

- Stocker dans Jenkins Credentials
- Ne jamais les mettre dans le Jenkinsfile
- Utiliser `credentials()` pour les référencer

---

## 🐛 Dépannage

### Les services Docker ne démarrent pas

```bash
# Vérifier les erreurs
docker-compose logs

# Supprimer les anciens conteneurs
docker-compose down -v

# Relancer
docker-compose up -d
```

### Jenkins ne trouve pas Maven

```groovy
// Ajouter dans Jenkinsfile :
environment {
  MAVEN_HOME = "${tool 'Maven 3.9.0'}"
  PATH = "${MAVEN_HOME}/bin:${PATH}"
}
```

### SonarQube timeout

```groovy
// Augmenter le timeout :
timeout(time: 10, unit: 'MINUTES') {
  waitForQualityGate abortPipeline: true
}
```

### Tests échouent

```bash
# Exécuter les tests avec logs
mvn test -X

# Voir les rapports
cat target/surefire-reports/*.txt
```

---

## 📚 Documentation Complète

Pour la documentation détaillée et complète :

👉 **[CI-CD-DOCUMENTATION.md](CI-CD-DOCUMENTATION.md)**

Contient :
- Architecture CI/CD détaillée
- Installation étape par étape
- Configuration Jenkins complète
- Métriques et KPIs
- Procédures de déploiement
- FAQ et dépannage avancé

---

## 🎯 Prochaines Étapes

1. **Créer les branches Git**
   ```bash
   git checkout -b develop
   git push -u origin develop
   ```

2. **Configurer Jenkins**
   - Installer les plugins
   - Ajouter les credentials
   - Créer le pipeline

3. **Configurer SonarQube**
   - Créer un projet
   - Générer un token
   - Ajouter le token à Jenkins

4. **Faire un push**
   ```bash
   git add .
   git commit -m "feat: Mise en place CI/CD complète"
   git push origin feat/jwt_security
   ```

5. **Déclencher le pipeline**
   - Jenkins détecte le push
   - Exécute le pipeline
   - Génère les rapports

---

## 📞 Support

Pour des questions sur le CI/CD :

📧 Email : nafia@smartlogi.com
📚 Documentation : CI-CD-DOCUMENTATION.md
🐛 Issues : GitHub Issues

---

## 📄 Licence

SmartLogi SDMS v0.3.0 © 2025 - Tous droits réservés

---

**Auteur** : Nafia Akdi  
**Date** : 2025-01-07  
**Version** : 0.3.0  
**Statut** : Production Ready ✅

