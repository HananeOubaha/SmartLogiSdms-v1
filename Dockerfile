# ============================================================================
# DOCKERFILE - Conteneurisation de SmartLogi SDMS v0.3.0
# ============================================================================
# Multi-stage build pour optimiser la taille de l'image
# Stage 1 : Build avec Maven
# Stage 2 : Runtime avec OpenJDK 17
# ============================================================================

# ========== STAGE 1 : BUILD ==========
FROM maven:3.9.0-eclipse-temurin-17-alpine AS builder

LABEL maintainer="Nafia Akdi <nafia@smartlogi.com>"
LABEL description="SmartLogi SDMS v0.3.0 - Build Stage"

WORKDIR /build

# Copier les fichiers POM pour mettre en cache les dépendances
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Télécharger les dépendances Maven
RUN mvn dependency:go-offline -B 2>/dev/null || true

# Copier le code source
COPY src ./src

# Compiler et packager l'application
RUN mvn clean package -DskipTests -Dspring.profiles.active=docker

# ========== STAGE 2 : RUNTIME ==========
FROM eclipse-temurin:17-jdk-alpine

LABEL maintainer="Nafia Akdi <nafia@smartlogi.com>"
LABEL description="SmartLogi SDMS v0.3.0 - Runtime Stage"

# Arguments pour les variables
ARG JAR_FILE=target/sdms-0.3.0-SNAPSHOT.jar
ARG APP_HOME=/app

# Variables d'environnement
ENV APP_HOME=${APP_HOME}
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE=docker
ENV TZ=Europe/Paris

# Créer l'utilisateur non-root
RUN addgroup -g 1000 sdms && \
    adduser -D -u 1000 -G sdms sdms

WORKDIR ${APP_HOME}

# Copier le JAR depuis le stage de build
COPY --from=builder /build/${JAR_FILE} ./sdms.jar

# Changer les permissions
RUN chown -R sdms:sdms ${APP_HOME}

# Utiliser l'utilisateur non-root
USER sdms

# Exposer le port
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar sdms.jar"]

# ============================================================================
# Instructions de build et run :
#
# Build :
#   docker build --build-arg JAR_FILE=target/sdms-0.3.0-SNAPSHOT.jar \
#     -t smartlogi/sdms:latest .
#
# Run :
#   docker run -d \
#     --name sdms \
#     -p 8080:8080 \
#     -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/smartlogi_db \
#     -e SPRING_DATASOURCE_USERNAME=postgres \
#     -e SPRING_DATASOURCE_PASSWORD=root \
#     -e JWT_SECRET=<votre_secret> \
#     smartlogi/sdms:latest
# ============================================================================

