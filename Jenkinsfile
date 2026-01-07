// ============================================================================
// JENKINSFILE - Pipeline CI/CD pour SmartLogi SDMS v0.3.0
// ============================================================================
// Pipeline déclaratif Jenkins pour l'automatisation complète :
// - Compilation Maven
// - Tests unitaires & d'intégration
// - Analyse SonarQube
// - Quality Gate
// - Génération d'artefacts Docker
// ============================================================================

pipeline {
    agent any

    // ========== VARIABLES D'ENVIRONNEMENT ==========
    environment {
        // Projet
        PROJECT_NAME = 'SmartLogiSdms'
        PROJECT_VERSION = '0.3.0'
        ARTIFACT_ID = 'sdms'

        // Chemins
        WORKSPACE = "${WORKSPACE}"
        BUILD_DIR = "${WORKSPACE}/target"

        // Maven
        MAVEN_HOME = "${tool 'Maven 3.9.0'}"
        PATH = "${MAVEN_HOME}/bin:${PATH}"

        // SonarQube
        SONAR_HOST_URL = 'http://127.0.0.1:9000'
        SONAR_LOGIN = credentials('sonar-token')
        SONAR_PROJECT_KEY = 'SmartLogiSdms'
        SONAR_SOURCES = 'src/main/java'
        SONAR_TESTS = 'src/test/java'
        SONAR_COVERAGE_REPORT = "${BUILD_DIR}/site/jacoco/index.html"

        // Docker
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_IMAGE_NAME = 'smartlogi/sdms'
        DOCKER_IMAGE_TAG = "${BUILD_NUMBER}-${BRANCH_NAME}"
        DOCKER_CREDENTIALS = credentials('docker-hub-credentials')

        // Git
        GIT_REPO = 'https://github.com/HananeOubaha/SmartLogiSdms-v1.git'
        GIT_BRANCH = "${BRANCH_NAME}"
    }

    // ========== PARAMÈTRES ==========
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'prod'],
            description: 'Environnement cible'
        )
        booleanParam(
            name: 'PUSH_DOCKER',
            defaultValue: false,
            description: 'Pusher l\'image Docker vers le registre'
        )
    }

    // ========== OPTIONS JENKINS ==========
    options {
        buildDiscarder(logRotator(numToKeepStr: '15'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
        ansiColor('xterm')
    }

    // ========== DÉCLENCHEURS ==========
    triggers {
        // Déclencher lors d'un push sur GitHub
        githubPush()
        // Déclencher toutes les 6h
        pollSCM('H/6 * * * *')
    }

    // ========== ÉTAPES DU PIPELINE ==========
    stages {
        // ========== ÉTAPE 1 : PRÉPARATION ==========
        stage('📋 Préparation') {
            steps {
                script {
                    echo "═══════════════════════════════════════════════════════════"
                    echo "🚀 Démarrage du Pipeline CI/CD - SmartLogi SDMS v${PROJECT_VERSION}"
                    echo "═══════════════════════════════════════════════════════════"
                    echo "📌 Branch: ${GIT_BRANCH}"
                    echo "📌 Build: ${BUILD_NUMBER}"
                    echo "📌 Workspace: ${WORKSPACE}"
                    echo "═══════════════════════════════════════════════════════════"
                }

                // Nettoyer le workspace
                deleteDir()

                // Cloner le dépôt
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "refs/heads/${GIT_BRANCH}"]],
                    userRemoteConfigs: [[url: GIT_REPO]]
                ])

                sh 'echo "✅ Dépôt cloné avec succès"'
            }
        }

        // ========== ÉTAPE 2 : COMPILATION ==========
        stage('🔨 Compilation') {
            steps {
                script {
                    echo "═══════════════════════════════════════════════════════════"
                    echo "🔨 Compilation du projet Maven..."
                    echo "═══════════════════════════════════════════════════════════"
                }

                sh '''
                    mvn clean compile \
                        -DskipTests \
                        -Dspring.profiles.active=ci \
                        -X
                '''

                sh 'echo "✅ Compilation réussie"'
            }
        }

        // ========== ÉTAPE 3 : TESTS UNITAIRES ==========
        stage('🧪 Tests Unitaires') {
            steps {
                script {
                    echo "═══════════════════════════════════════════════════════════"
                    echo "🧪 Exécution des tests unitaires..."
                    echo "═══════════════════════════════════════════════════════════"
                }

                sh '''
                    mvn test \
                        -Dspring.profiles.active=test \
                        -Dspring.datasource.url=jdbc:h2:mem:testdb \
                        -Dspring.h2.console.enabled=true
                '''

                sh 'echo "✅ Tests unitaires réussis"'
            }
        }

        // ========== ÉTAPE 4 : RAPPORT DE COUVERTURE ==========
        stage('📊 Couverture de Code') {
            steps {
                script {
                    echo "═══════════════════════════════════════════════════════════"
                    echo "📊 Génération du rapport JaCoCo..."
                    echo "═══════════════════════════════════════════════════════════"
                }

                sh '''
                    mvn jacoco:report
                '''

                // Publier le rapport JaCoCo
                publishHTML([
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage Report'
                ])

                sh 'echo "✅ Rapport de couverture généré"'
            }
        }

        // ========== ÉTAPE 5 : ANALYSE SONARQUBE ==========
        stage('🔍 Analyse SonarQube') {
            steps {
                script {
                    echo "═══════════════════════════════════════════════════════════"
                    echo "🔍 Analyse du code avec SonarQube..."
                    echo "═══════════════════════════════════════════════════════════"
                }

                sh '''
                    mvn sonar:sonar \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_LOGIN} \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.projectName="${PROJECT_NAME}" \
                        -Dsonar.projectVersion=${PROJECT_VERSION} \
                        -Dsonar.sources=${SONAR_SOURCES} \
                        -Dsonar.tests=${SONAR_TESTS} \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                        -Dsonar.java.binaries=target/classes
                '''

                sh 'echo "✅ Analyse SonarQube complétée"'
            }
        }

        // ========== ÉTAPE 6 : QUALITY GATE ==========
        stage('⚖️ Quality Gate') {
            steps {
                script {
                    echo "═══════════════════════════════════════════════════════════"
                    echo "⚖️ Vérification du Quality Gate SonarQube..."
                    echo "═══════════════════════════════════════════════════════════"
                }

                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }

                sh 'echo "✅ Quality Gate approuvé"'
            }
        }

        // ========== ÉTAPE 7 : BUILD ==========
        stage('📦 Build Package') {
            steps {
                script {
                    echo "═══════════════════════════════════════════════════════════"
                    echo "📦 Création du JAR executable..."
                    echo "═══════════════════════════════════════════════════════════"
                }

                sh '''
                    mvn package \
                        -DskipTests \
                        -Dspring.profiles.active=ci
                '''

                sh 'ls -lah ${BUILD_DIR}/*.jar'
                sh 'echo "✅ JAR créé avec succès"'
            }
        }

        // ========== ÉTAPE 8 : CONSTRUCTION IMAGE DOCKER ==========
        stage('🐳 Build Docker') {
            steps {
                script {
                    echo "═══════════════════════════════════════════════════════════"
                    echo "🐳 Construction de l'image Docker..."
                    echo "═══════════════════════════════════════════════════════════"
                }

                sh '''
                    docker build \
                        --build-arg JAR_FILE=target/${ARTIFACT_ID}-${PROJECT_VERSION}-SNAPSHOT.jar \
                        -t ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG} \
                        -t ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest \
                        -f Dockerfile .
                '''

                sh 'docker images | grep smartlogi'
                sh 'echo "✅ Image Docker construite"'
            }
        }

        // ========== ÉTAPE 9 : PUSH IMAGE DOCKER ==========
        stage('📤 Push Docker') {
            when {
                expression { params.PUSH_DOCKER == true && GIT_BRANCH == 'main' }
            }
            steps {
                script {
                    echo "═══════════════════════════════════════════════════════════"
                    echo "📤 Push de l'image Docker vers le registre..."
                    echo "═══════════════════════════════════════════════════════════"
                }

                sh '''
                    echo "${DOCKER_CREDENTIALS_PSW}" | docker login -u "${DOCKER_CREDENTIALS_USR}" --password-stdin
                    docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}
                    docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE_NAME}:latest
                    docker logout
                '''

                sh 'echo "✅ Image Docker pushée"'
            }
        }

        // ========== ÉTAPE 10 : ARCHIVAGE DES ARTEFACTS ==========
        stage('📚 Archivage') {
            steps {
                script {
                    echo "═══════════════════════════════════════════════════════════"
                    echo "📚 Archivage des artefacts..."
                    echo "═══════════════════════════════════════════════════════════"
                }

                archiveArtifacts artifacts: 'target/*.jar,target/*.war', allowEmptyArchive: true

                // Publier les rapports de test
                junit 'target/surefire-reports/*.xml', allowEmptyResults: true

                sh 'echo "✅ Artefacts archivés"'
            }
        }
    }

    // ========== POST BUILD ==========
    post {
        always {
            script {
                echo "═══════════════════════════════════════════════════════════"
                echo "📊 Résumé du Build"
                echo "═══════════════════════════════════════════════════════════"
                echo "Status: ${currentBuild.result}"
                echo "Build: ${BUILD_NUMBER}"
                echo "Duration: ${currentBuild.durationString}"
                echo "═══════════════════════════════════════════════════════════"
            }

            // Nettoyage Docker
            sh 'docker image prune -f --filter "dangling=true" || true'
        }

        success {
            script {
                echo "✅ PIPELINE SUCCÈS - SmartLogi SDMS Build ${BUILD_NUMBER}"
                // Ajouter une notification (Slack, Email, etc.)
            }
        }

        failure {
            script {
                echo "❌ PIPELINE ÉCHOUÉ - SmartLogi SDMS Build ${BUILD_NUMBER}"
                // Ajouter une notification (Slack, Email, etc.)
            }
        }

        unstable {
            script {
                echo "⚠️ PIPELINE INSTABLE - SmartLogi SDMS Build ${BUILD_NUMBER}"
            }
        }
    }
}

