#!/bin/bash
# ============================================================================
# SETUP-CI-CD.sh - Script d'installation automatique du pipeline CI/CD
# ============================================================================
# Déploie et configure Jenkins, SonarQube et Docker Compose
# Usage: ./setup-ci-cd.sh
# ============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ========== CONFIGURATION ==========
PROJECT_NAME="SmartLogiSdms"
PROJECT_VERSION="0.3.0"
GITHUB_REPO="https://github.com/HananeOubaha/SmartLogiSdms-v1.git"
JENKINS_PORT=8081
SONAR_PORT=9000
SDMS_PORT=8080

# ========== FONCTIONS UTILITAIRES ==========
print_header() {
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║  $1${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# ========== VÉRIFICATIONS PRÉALABLES ==========
check_prerequisites() {
    print_header "VÉRIFICATION DES PRÉREQUIS"

    # Vérifier Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker n'est pas installé"
        echo "Installer depuis : https://www.docker.com/products/docker-desktop"
        exit 1
    fi
    print_success "Docker trouvé : $(docker --version)"

    # Vérifier Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose n'est pas installé"
        echo "Installer depuis : https://docs.docker.com/compose/install/"
        exit 1
    fi
    print_success "Docker Compose trouvé : $(docker-compose --version)"

    # Vérifier Java
    if ! command -v java &> /dev/null; then
        print_error "Java n'est pas installé"
        echo "Installer Java 17 ou supérieur"
        exit 1
    fi
    java_version=$(java -version 2>&1 | grep version)
    print_success "Java trouvé : $java_version"

    # Vérifier Maven
    if ! command -v mvn &> /dev/null; then
        print_error "Maven n'est pas installé"
        echo "Installer depuis : https://maven.apache.org/download.cgi"
        exit 1
    fi
    print_success "Maven trouvé : $(mvn --version | head -1)"

    # Vérifier Git
    if ! command -v git &> /dev/null; then
        print_error "Git n'est pas installé"
        exit 1
    fi
    print_success "Git trouvé : $(git --version)"

    echo ""
}

# ========== CONFIGURATION ENVIRONNEMENT ==========
setup_environment() {
    print_header "CONFIGURATION DE L'ENVIRONNEMENT"

    if [ -f .env ]; then
        print_warning ".env existe déjà"
    else
        print_info "Création du fichier .env..."
        cp .env.example .env
        print_success ".env créé à partir de .env.example"
    fi

    echo ""
}

# ========== BUILD MAVEN ==========
build_application() {
    print_header "BUILD DE L'APPLICATION"

    print_info "Compilation Maven..."
    mvn clean package -DskipTests -Dspring.profiles.active=ci

    if [ $? -eq 0 ]; then
        print_success "Build Maven réussi"
    else
        print_error "Build Maven échoué"
        exit 1
    fi

    echo ""
}

# ========== LANCER SERVICES DOCKER ==========
start_services() {
    print_header "DÉMARRAGE DES SERVICES DOCKER"

    # Vérifier si les services tournent déjà
    if docker ps --format '{{.Names}}' | grep -q "smartlogi-"; then
        print_warning "Des services SmartLogi tournent déjà"
        read -p "Arrêter et redémarrer ? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            print_info "Arrêt des services..."
            docker-compose down
        else
            print_warning "Services non redémarrés"
            return
        fi
    fi

    print_info "Démarrage des services..."
    docker-compose up -d

    # Attendre que les services soient prêts
    print_info "Attente du démarrage des services (60s)..."
    sleep 60

    # Vérifier les services
    print_info "Vérification des services..."

    # PostgreSQL
    if docker exec smartlogi-postgres pg_isready -U postgres > /dev/null 2>&1; then
        print_success "PostgreSQL démarré"
    else
        print_error "PostgreSQL ne répond pas"
    fi

    # SonarQube
    if curl -s http://127.0.0.1:${SONAR_PORT}/api/system/status | grep -q "UP"; then
        print_success "SonarQube démarré"
    else
        print_warning "SonarQube en cours de démarrage..."
        sleep 30
    fi

    # SDMS
    if curl -s http://localhost:${SDMS_PORT}/api/actuator/health > /dev/null 2>&1; then
        print_success "SmartLogi SDMS démarré"
    else
        print_warning "SmartLogi SDMS en cours de démarrage..."
    fi

    echo ""
}

# ========== AFFICHER LES INFORMATIONS D'ACCÈS ==========
print_access_info() {
    print_header "INFORMATIONS D'ACCÈS"

    echo ""
    echo "📱 Services disponibles :"
    echo ""
    echo -e "  ${BLUE}SDMS Application${NC}"
    echo "  URL: http://localhost:${SDMS_PORT}/api"
    echo "  Swagger: http://localhost:${SDMS_PORT}/api/swagger-ui.html"
    echo ""

    echo -e "  ${BLUE}PostgreSQL (PgAdmin)${NC}"
    echo "  URL: http://localhost:5050"
    echo "  Email: admin@smartlogi.com"
    echo "  Password: admin123"
    echo ""

    echo -e "  ${BLUE}SonarQube${NC}"
    echo "  URL: http://127.0.0.1:${SONAR_PORT}"
    echo "  Username: admin"
    echo "  Password: admin"
    echo ""

    echo -e "  ${BLUE}Jenkins${NC}"
    echo "  URL: http://localhost:${JENKINS_PORT}"
    echo "  Configuration requise (voir CI-CD-DOCUMENTATION.md)"
    echo ""
}

# ========== COMMANDES UTILES ==========
print_commands() {
    print_header "COMMANDES UTILES"

    echo ""
    echo "📝 Voir les logs :"
    echo "  docker-compose logs -f sdms"
    echo "  docker-compose logs -f postgres"
    echo "  docker-compose logs -f sonarqube"
    echo ""

    echo "🔨 Builder et tester :"
    echo "  mvn clean package -DskipTests"
    echo "  mvn test"
    echo ""

    echo "🐳 Commandes Docker :"
    echo "  docker-compose up -d        # Démarrer"
    echo "  docker-compose down         # Arrêter"
    echo "  docker-compose restart      # Redémarrer"
    echo "  docker-compose down -v      # Arrêter et supprimer volumes"
    echo ""

    echo "🔍 Analyse SonarQube :"
    echo "  mvn sonar:sonar -Dsonar.host.url=http://127.0.0.1:${SONAR_PORT} -Dsonar.login=admin"
    echo ""
}

# ========== PROGRAMME PRINCIPAL ==========
main() {
    echo ""
    print_header "🚀 SETUP CI/CD - SmartLogi SDMS v${PROJECT_VERSION}"
    echo ""

    # Exécuter les étapes
    check_prerequisites
    setup_environment
    build_application
    start_services
    print_access_info
    print_commands

    echo ""
    print_header "✅ SETUP TERMINÉ"
    echo ""
    echo "Les prochaines étapes :"
    echo "1. Accéder à SonarQube et créer un projet"
    echo "2. Configurer Jenkins avec les credentials"
    echo "3. Créer un pipeline Jenkins"
    echo "4. Pusher du code pour déclencher le pipeline"
    echo ""
    echo "Documentation : CI-CD-DOCUMENTATION.md"
    echo ""
}

# Exécuter le programme principal
main "$@"

