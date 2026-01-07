@echo off
REM ============================================================================
REM SETUP-CI-CD.bat - Script d'installation automatique du pipeline CI/CD
REM ============================================================================
REM Version Windows PowerShell du script setup-ci-cd.sh
REM Usage: .\setup-ci-cd.bat
REM ============================================================================

setlocal enabledelayedexpansion

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║  SmartLogi SDMS CI/CD Setup - v0.3.0                          ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM ========== CONFIGURATION ==========
set PROJECT_NAME=SmartLogiSdms
set PROJECT_VERSION=0.3.0
set JENKINS_PORT=8081
set SONAR_PORT=9000
set SDMS_PORT=8080

REM ========== VÉRIFICATIONS PRÉALABLES ==========
echo [1/6] Vérification des prérequis...
echo.

REM Vérifier Docker
where docker >nul 2>nul
if errorlevel 1 (
    echo ❌ Docker n'est pas installé
    echo    Installer depuis : https://www.docker.com/products/docker-desktop
    exit /b 1
)
echo ✅ Docker trouvé
for /f "tokens=*" %%i in ('docker --version') do echo    %%i

REM Vérifier Docker Compose
where docker-compose >nul 2>nul
if errorlevel 1 (
    echo ❌ Docker Compose n'est pas installé
    echo    Installer depuis : https://docs.docker.com/compose/install/
    exit /b 1
)
echo ✅ Docker Compose trouvé

REM Vérifier Java
where java >nul 2>nul
if errorlevel 1 (
    echo ❌ Java n'est pas installé
    echo    Installer Java 17 ou supérieur
    exit /b 1
)
echo ✅ Java trouvé

REM Vérifier Maven
where mvn >nul 2>nul
if errorlevel 1 (
    echo ❌ Maven n'est pas installé
    echo    Installer depuis : https://maven.apache.org/download.cgi
    exit /b 1
)
echo ✅ Maven trouvé

REM Vérifier Git
where git >nul 2>nul
if errorlevel 1 (
    echo ❌ Git n'est pas installé
    exit /b 1
)
echo ✅ Git trouvé
echo.

REM ========== CONFIGURATION ENVIRONNEMENT ==========
echo [2/6] Configuration de l'environnement...
if exist .env (
    echo ⚠️  .env existe déjà
) else (
    echo ℹ️  Création du fichier .env...
    copy .env.example .env
    echo ✅ .env créé à partir de .env.example
)
echo.

REM ========== BUILD MAVEN ==========
echo [3/6] Build de l'application...
call mvn clean package -DskipTests -Dspring.profiles.active=ci
if errorlevel 1 (
    echo ❌ Build Maven échoué
    exit /b 1
)
echo ✅ Build Maven réussi
echo.

REM ========== LANCER SERVICES DOCKER ==========
echo [4/6] Démarrage des services Docker...

REM Vérifier si les services tournent
docker ps --format "{{.Names}}" | findstr smartlogi- >nul
if not errorlevel 1 (
    echo ⚠️  Des services SmartLogi tournent déjà
    set /p RESTART="Arrêter et redémarrer ? (y/n): "
    if /i "!RESTART!"=="y" (
        echo ℹ️  Arrêt des services...
        call docker-compose down
    ) else (
        echo ⚠️  Services non redémarrés
        goto :skip_docker
    )
)

echo ℹ️  Démarrage des services...
call docker-compose up -d

echo ℹ️  Attente du démarrage des services (60 secondes)...
timeout /t 60 /nobreak

echo.
echo [5/6] Vérification des services...

REM Vérifier PostgreSQL
docker exec smartlogi-postgres pg_isready -U postgres >nul 2>nul
if errorlevel 0 (
    echo ✅ PostgreSQL démarré
) else (
    echo ⚠️  PostgreSQL en cours de démarrage...
)

REM Vérifier SDMS
curl -s http://localhost:%SDMS_PORT%/api/actuator/health >nul 2>nul
if errorlevel 0 (
    echo ✅ SmartLogi SDMS démarré
) else (
    echo ⚠️  SmartLogi SDMS en cours de démarrage...
)

:skip_docker
echo.

REM ========== AFFICHER LES INFORMATIONS D'ACCÈS ==========
echo [6/6] Affichage des informations d'accès...
echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║  INFORMATIONS D'ACCÈS                                          ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

echo 📱 Services disponibles :
echo.
echo   SmartLogi SDMS
echo   URL: http://localhost:%SDMS_PORT%/api
echo   Swagger: http://localhost:%SDMS_PORT%/api/swagger-ui.html
echo.
echo   PostgreSQL (PgAdmin)
echo   URL: http://localhost:5050
echo   Email: admin@smartlogi.com
echo   Password: admin123
echo.
echo   SonarQube
echo   URL: http://127.0.0.1:%SONAR_PORT%
echo   Username: admin
echo   Password: admin
echo.
echo   Jenkins
echo   URL: http://localhost:%JENKINS_PORT%
echo   (Configuration requise)
echo.

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║  COMMANDES UTILES                                              ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

echo 📝 Voir les logs :
echo   docker-compose logs -f sdms
echo   docker-compose logs -f postgres
echo   docker-compose logs -f sonarqube
echo.

echo 🔨 Builder et tester :
echo   mvn clean package -DskipTests
echo   mvn test
echo.

echo 🐳 Commandes Docker :
echo   docker-compose up -d        # Démarrer
echo   docker-compose down         # Arrêter
echo   docker-compose restart      # Redémarrer
echo   docker-compose down -v      # Arrêter et supprimer volumes
echo.

echo 🔍 Analyse SonarQube :
echo   mvn sonar:sonar -Dsonar.host.url=http://127.0.0.1:%SONAR_PORT% -Dsonar.login=admin
echo.

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║  ✅ SETUP TERMINÉ                                             ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.
echo Les prochaines étapes :
echo 1. Accéder à SonarQube et créer un projet
echo 2. Configurer Jenkins avec les credentials
echo 3. Créer un pipeline Jenkins
echo 4. Pusher du code pour déclencher le pipeline
echo.
echo Documentation : CI-CD-DOCUMENTATION.md
echo.

pause

endlocal

