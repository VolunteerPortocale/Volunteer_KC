# Stage 1: build the user storage provider jar
FROM gradle:8.10-jdk21 AS provider-build
WORKDIR /provider
COPY provider/ .
RUN gradle jar --no-daemon

# Stage 2: Keycloak build with the provider installed
FROM quay.io/keycloak/keycloak:26.7.3 AS builder
ENV KC_DB=postgres
ENV KC_HEALTH_ENABLED=true
COPY --from=provider-build /provider/build/libs/volunteer-user-provider.jar /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh build

# Stage 3: runtime
FROM quay.io/keycloak/keycloak:26.7.3
COPY --from=builder /opt/keycloak/lib/quarkus /opt/keycloak/lib/quarkus
COPY --from=builder /opt/keycloak/providers /opt/keycloak/providers
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start", "--optimized"]
