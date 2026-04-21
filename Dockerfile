FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Scarica le dipendenze in un layer separato: viene rieseguito solo se pom.xml cambia
COPY pom.xml ./
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

# Installa curl per healthcheck (richiede root, prima di switchare utente)
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Esegui come utente non-root (container hardening)
RUN useradd --create-home --uid 10001 appuser
USER appuser

# --chown garantisce che appuser sia proprietario del jar
COPY --from=build --chown=appuser:appuser /workspace/target/*.jar /app/app.jar

EXPOSE 8080

# Health check tramite Spring Actuator (più affidabile del TCP trick)
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]