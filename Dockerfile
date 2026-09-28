# Stage 1: build the provider fat JAR with the project's own Gradle wrapper
FROM eclipse-temurin:21-jdk AS provider-build
WORKDIR /provider
COPY provider/ .
RUN chmod +x gradlew && ./gradlew shadowJar --no-daemon

# Stage 2: Keycloak build with the provider installed
FROM quay.io/keycloak/keycloak:26.7.3 AS builder
ENV KC_DB=postgres
ENV KC_HEALTH_ENABLED=true
COPY --from=provider-build /provider/build/libs/volunteer-user-provider.jar /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh build

# Stage 3: runtime
FROM quay.io/keycloak/keycloak:26.7.3
COPY --from=builder /opt/keycloak/ /opt/keycloak/
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--optimized"]
