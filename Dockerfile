# ── Build Stage ──────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle.kts settings.gradle.kts gradle.properties ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# ── Runtime Stage ───────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S marketplace && adduser -S marketplace -G marketplace

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown marketplace:marketplace app.jar

USER marketplace

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java"]
CMD ["-XX:+UseZGC", "-XX:+ZGenerational", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-jar", "app.jar"]
