# Stage 1: build the provider fat JAR with the project's Gradle wrapper (project is in the repo root)
FROM eclipse-temurin:21-jdk AS provider-build
WORKDIR /build
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY gradle/ gradle/
COPY src/ src/
RUN chmod +x gradlew && ./gradlew shadowJar --no-daemon

# Stage 2: Keycloak build with the provider and theme installed
FROM quay.io/keycloak/keycloak:26.7.3 AS builder
ENV KC_DB=postgres
ENV KC_HEALTH_ENABLED=true
COPY --from=provider-build /build/build/libs/volunteer-user-provider.jar /opt/keycloak/providers/
COPY themes/ /opt/keycloak/themes/
RUN /opt/keycloak/bin/kc.sh build

# Stage 3: runtime
FROM quay.io/keycloak/keycloak:26.7.3
COPY --from=builder /opt/keycloak/ /opt/keycloak/
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--optimized"]
