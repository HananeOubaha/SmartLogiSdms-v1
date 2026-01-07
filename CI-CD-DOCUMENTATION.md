# ============================================================================
# CI/CD PIPELINE DOCUMENTATION - SmartLogi SDMS v0.3.0
# ============================================================================

## 📋 Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture CI/CD](#architecture-cicd)
3. [Prérequis](#prérequis)
4. [Installation](#installation)
5. [Configuration](#configuration)
6. [Pipeline Jenkins](#pipeline-jenkins)
7. [Analyse SonarQube](#analyse-sonarqube)
8. [Conteneurisation Docker](#conteneurisation-docker)
9. [Déploiement](#déploiement)
10. [Dépannage](#dépannage)

---

## 🎯 Vue d'ensemble

SmartLogi SDMS v0.3.0 implémente une **chaîne CI/CD complète et automatisée** permettant :

✅ **Intégration Continue (CI)** :
- Compilation automatique à chaque push
- Tests unitaires et d'intégration
- Analyse de qualité du code
- Rapport de couverture

✅ **Livraison Continue (CD)** :
- Construction d'image Docker
- Push vers registre Docker
- Préparation au déploiement

✅ **Contrôle Qualité** :
- Quality Gate SonarQube
- Détection des bugs et failles
- Métriques de performance

---

## 🏗️ Architecture CI/CD

```
┌─────────────────────────────────────────────────────────────┐
│                     DÉVELOPPEUR                             │
│              (Git push vers GitHub)                          │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                    GITHUB                                   │
│            (Dépôt centralisé)                               │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │ (Webhook)                   │
        ▼                             ▼
┌───────────────────┐        ┌──────────────────┐
│     JENKINS       │        │ GitHub Actions   │
│  (CI/CD Server)   │        │  (CI Pipeline)   │
└────────┬──────────┘        └──────────────────┘
         │
    ┌────┴─────────────────────────────┐
    │                                   │
    ▼                                   ▼
┌──────────────────┐          ┌─────────────────┐
│ 1. COMPILATION   │          │ 2. TESTS        │
│   (Maven)        │          │   (JUnit 5)     │
└──────────────────┘          └─────────────────┘
    │                             │
    └────────────┬────────────────┘
                 ▼
        ┌─────────────────────┐
        │ 3. COUVERTURE CODE  │
        │   (JaCoCo)          │
        └──────────┬──────────┘
                   ▼
        ┌──────────────────────┐
        │ 4. ANALYSE SonarQube │
        │   (Quality)          │
        └──────────┬───────────┘
                   │
              ✓ / ✗
              │   │
         PASS│   │FAIL
         ┌────┘   └─────┐
         │               │
         ▼               ▼
    ┌─────────┐    ┌──────────────┐
    │ QUALITY │    │   ALERTE &   │
    │  GATE   │    │  NOTIFICATIONS│
    │   OK    │    │   EMAIL/SLACK│
    └────┬────┘    └──────────────┘
         │
         ▼
    ┌──────────────────┐
    │ 5. BUILD ARTEFACT│
    │    (JAR)         │
    └────────┬─────────┘
             │
             ▼
    ┌──────────────────┐
    │ 6. BUILD DOCKER  │
    │    (Image)       │
    └────────┬─────────┘
             │
             ▼
    ┌──────────────────┐
    │ 7. PUSH DOCKER   │
    │   (Registry)     │
    └────────┬─────────┘
             │
             ▼
    ┌──────────────────┐
    │ 8. DÉPLOIEMENT   │
    │  (Staging/Prod)  │
    └──────────────────┘
```

---

## ⚙️ Prérequis

### Environnement Local
- **Java 17+**
- **Maven 3.9.0+**
- **Docker & Docker Compose**
- **Git**

### Infrastructure Jenkins
- **Jenkins 2.x LTS**
- **Maven 3.9.0+**
- **Docker (Plugin Jenkins)**
- **Git (Plugin Jenkins)**
- **SonarQube Scanner (Plugin Jenkins)**

### Services externes
- **GitHub** (https://github.com)
- **SonarQube 10.x+** (http://127.0.0.1:9000)
- **Docker Registry** (Docker Hub ou registry privé)

---

## 📦 Installation

### 1. Cloner le dépôt

```bash
git clone https://github.com/HananeOubaha/SmartLogiSdms-v1.git
cd SmartLogiSdms-v1
```

### 2. Créer les branches Git requises

```bash
# Branch develop (intégration continue)
git checkout -b develop
git push -u origin develop

# Branch feature (développement)
git checkout -b feat/feature-name
```

### 3. Installer Jenkins

#### Option A : Docker
```bash
docker run -d \
  --name jenkins \
  -p 8081:8080 \
  -v jenkins_data:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts-jdk17
```

#### Option B : Bare Metal (Windows)
```powershell
# Télécharger depuis https://www.jenkins.io/download/
# Installer et lancer Jenkins

# Jenkins sera accessible sur http://localhost:8080
```

### 4. Installer SonarQube

```bash
docker run -d \
  --name sonarqube \
  -e SONAR_JDBC_URL=jdbc:h2:tcp://127.0.0.1:9092/sonar \
  -p 9000:9000 \
  sonarqube:lts-community
```

---

## ⚙️ Configuration

### Configuration Jenkins

#### 1. Plugins requis
Aller à **Manage Jenkins → Manage Plugins** et installer :
- `Git Plugin`
- `Pipeline Plugin`
- `Docker Pipeline`
- `SonarQube Scanner`
- `JaCoCo Plugin`
- `Email Extension Plugin`

#### 2. Credentials

**Global Credentials → Add Credentials**

```
1. GitHub Token
   ID: github-token
   Type: Secret text
   Secret: <votre_token_GitHub>

2. SonarQube Token
   ID: sonar-token
   Type: Secret text
   Secret: <votre_token_SonarQube>

3. Docker Hub Credentials
   ID: docker-hub-credentials
   Type: Username with password
   Username: <votre_username>
   Password: <votre_token>
```

#### 3. Configuration SonarQube

**Manage Jenkins → Configure System → SonarQube**

```
Server name: SonarQube
Server URL: http://127.0.0.1:9000
Server authentication token: <credentials-reference>
```

#### 4. Pipeline Job

**New Item → Pipeline**

```
Nom: SmartLogiSDMS-CI
Pipeline → Pipeline script from SCM
SCM: Git
Repository URL: https://github.com/HananeOubaha/SmartLogiSdms-v1.git
Branch: */feat/jwt_security
Script Path: Jenkinsfile
```

---

## 🔄 Pipeline Jenkins

### Étapes du pipeline (Jenkinsfile)

```groovy
1. 📋 PRÉPARATION
   ├─ Nettoyage workspace
   ├─ Clone dépôt Git
   └─ Vérification environnement

2. 🔨 COMPILATION
   ├─ Maven clean compile
   └─ Vérification des dépendances

3. 🧪 TESTS UNITAIRES
   ├─ Maven test
   ├─ JUnit 5
   └─ Mockito

4. 📊 COUVERTURE CODE
   ├─ JaCoCo report
   ├─ Métrique de couverture
   └─ Rapport HTML

5. 🔍 ANALYSE SONARQUBE
   ├─ Scan du code
   ├─ Détection bugs/failles
   └─ Calcul dette technique

6. ⚖️ QUALITY GATE
   ├─ Vérification seuils
   ├─ Blocage si échoué
   └─ Rapport détaillé

7. 📦 BUILD PACKAGE
   ├─ Maven package
   ├─ JAR exécutable
   └─ Signature artefact

8. 🐳 BUILD DOCKER
   ├─ Multi-stage build
   ├─ Optimisation taille
   └─ Image compressée

9. 📤 PUSH DOCKER
   ├─ Login registre
   ├─ Push image
   └─ Cleanup credentials

10. 📚 ARCHIVAGE
    ├─ Archiver JAR/WAR
    ├─ Publier rapports
    └─ Historique builds
```

### Déclencheurs

```groovy
triggers {
  // Webhook GitHub Push
  githubPush()
  
  // Poll SCM toutes les 6h
  pollSCM('H/6 * * * *')
}
```

### Variables d'environnement

```groovy
environment {
  PROJECT_NAME = 'SmartLogiSdms'
  PROJECT_VERSION = '0.3.0'
  ARTIFACT_ID = 'sdms'
  
  SONAR_HOST_URL = 'http://127.0.0.1:9000'
  SONAR_LOGIN = credentials('sonar-token')
  
  DOCKER_REGISTRY = 'docker.io'
  DOCKER_IMAGE_NAME = 'smartlogi/sdms'
}
```

---

## 🔍 Analyse SonarQube

### Configuration du projet

1. **Login SonarQube** (http://localhost:9000)
   - Utilisateur : `admin`
   - Mot de passe : `admin`

2. **Créer un projet**
   - Projects → Create Project
   - Name: `SmartLogiSdms`
   - Key: `SmartLogiSdms`

3. **Générer un token**
   - Admin → Security → Users → Generate Token
   - Copier le token pour Jenkins

### Métriques clés

| Métrique | Seuil | Status |
|----------|-------|--------|
| Couverture | ≥ 80% | ✅ |
| Duplication | ≤ 5% | ✅ |
| Maintenabilité | A | ✅ |
| Fiabilité | A | ✅ |
| Sécurité | A | ✅ |

### Quality Gate

```
Règles :
├─ Couverture ≥ 80%
├─ Duplications ≤ 5%
├─ Code smells < 30
├─ Bugs = 0
├─ Vulnerabilities = 0
└─ Security hotspots reviewed
```

---

## 🐳 Conteneurisation Docker

### Structure Dockerfile

```dockerfile
# Stage 1 : Builder (Maven)
# - Compile et package l'application
# - Génère sdms.jar

# Stage 2 : Runtime (OpenJDK)
# - Crée l'image runtime
# - Copie le JAR
# - Optimise la taille
```

### Build local

```bash
# Compiler d'abord
mvn clean package -DskipTests

# Build image
docker build -t smartlogi/sdms:0.3.0 .

# Vérifier
docker images | grep smartlogi
```

### Run image

```bash
docker run -d \
  --name sdms \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/smartlogi1_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=root \
  -e JWT_SECRET=<votre_secret> \
  smartlogi/sdms:0.3.0
```

### Docker Compose

```bash
# Lancer tous les services
docker-compose up -d

# Voir logs
docker-compose logs -f sdms

# Arrêter
docker-compose down
```

---

## 🚀 Déploiement

### Environnements

#### Développement
- Branch : `develop`
- Trigger : Push automatique
- Déploiement : Sandbox/Dev

#### Staging
- Branch : `release/*`
- Trigger : Création PR
- Déploiement : Pre-production

#### Production
- Branch : `main`
- Trigger : Merge PR
- Déploiement : Production (manuel)

### Procédure de déploiement

```bash
# 1. Préparer release
git flow release start 0.3.0

# 2. Commits et tests
git add .
git commit -m "release: v0.3.0"

# 3. Fusionner dans main
git flow release finish 0.3.0

# 4. Push
git push origin main develop

# 5. Jenkins construit automatiquement
# 6. Image Docker disponible
# 7. Prêt pour production
```

---

## 🐛 Dépannage

### Problème : Jenkins ne trouve pas Maven

```groovy
// Solution : Configurer le chemin Maven
environment {
  MAVEN_HOME = "${tool 'Maven 3.9.0'}"
  PATH = "${MAVEN_HOME}/bin:${PATH}"
}
```

### Problème : SonarQube timeout

```bash
# Augmenter le timeout
timeout(time: 10, unit: 'MINUTES') {
  waitForQualityGate abortPipeline: true
}
```

### Problème : Docker daemon inaccessible

```bash
# Vérifier que Docker daemon tourne
docker ps

# Donner les permissions à Jenkins
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

### Problème : Tests échouent

```bash
# Exécuter tests localement
mvn test

# Voir logs détaillés
mvn test -X

# Exécuter avec profil test
mvn test -Dspring.profiles.active=test
```

### Problème : Quality Gate échoue

```bash
# Vérifier les rapports SonarQube
# 1. http://localhost:9000
# 2. Projects → SmartLogiSdms
# 3. Analyse les résultats
# 4. Corriger les issues
# 5. Re-trigger le build
```

---

## 📊 Monitoring et Logs

### Logs Jenkins

```bash
# Via interface web
Jenkins → Builds → Console Output

# Via Docker
docker logs -f smartlogi-jenkins

# Via système fichiers
/var/jenkins_home/jobs/SmartLogiSDMS-CI/builds/*/log
```

### Logs Application

```bash
# Via Docker
docker logs -f smartlogi-sdms

# Via fichier
tail -f ./logs/application.log
```

### Logs SonarQube

```bash
# Via Docker
docker logs -f smartlogi-sonarqube

# Via interface
http://localhost:9000 → Administration → System Logs
```

---

## 🔐 Sécurité

### Bonnes pratiques

✅ **Credentials**
- Utiliser les secrets Jenkins
- Ne pas commiter les credentials
- Rotation régulière des tokens

✅ **Code**
- Quality Gate obligatoire
- Analyse des vulnérabilités
- Dépendances à jour

✅ **Infrastructure**
- HTTPS pour Jenkins
- Authentification forcée
- RBAC sur dépôt

✅ **Artefacts**
- Signature des images Docker
- Scan des dépendances
- Registry privé

---

## 📈 Métriques et KPIs

| Métrique | Cible | Actuel |
|----------|-------|--------|
| Build Time | < 10 min | - |
| Success Rate | > 95% | - |
| Code Coverage | ≥ 80% | - |
| Bugs | 0 | - |
| Vulnerabilities | 0 | - |
| Deployment Freq | Daily | - |
| Lead Time | < 1 day | - |

---

## 📞 Support et Contact

Pour toute question concernant le CI/CD :

📧 **Email** : nafia@smartlogi.com
📱 **Slack** : #smartlogi-devops
📚 **Documentation** : https://smartlogi-docs.io

---

## 📝 Changelog

### v0.3.0 (2025-01)
- ✨ Pipeline CI/CD complet
- 🐳 Conteneurisation Docker
- 🔍 Intégration SonarQube
- 📊 Rapports de couverture

### v0.2.1 (2024-12)
- 🔒 Authentification JWT
- 🔄 Déploiement Docker

### v0.2.0 (2024-11)
- 🧪 Tests automatisés
- 📚 Documentation API

### v0.1.0 (2024-10)
- 🚀 Fonctionnalités métier

---

**Document rédigé par** : Nafia Akdi  
**Date** : 2025-01-07  
**Version** : v0.3.0  
**Statut** : Production

