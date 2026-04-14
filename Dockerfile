FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Scarica le dipendenze in un layer separato: viene rieseguito solo se pom.xml cambia
COPY pom.xml ./
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

# Esegui come utente non-root (container hardening)
RUN useradd --create-home --uid 10001 appuser
USER appuser

# --chown garantisce che appuser sia proprietario del jar
COPY --from=build --chown=appuser:appuser /workspace/target/*.jar /app/app.jar

EXPOSE 8080

# Health check: verifica che la porta 8080 sia raggiungibile
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD bash -c 'echo > /dev/tcp/localhost/8080' || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

