plugins {
    java
}

group = "com.portocale.volunteer.kc"
version = "1.0.0"

val keycloakVersion = "26.7.3"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Provided by the Keycloak server at runtime - never bundled into the jar
    compileOnly("org.keycloak:keycloak-core:$keycloakVersion")
    compileOnly("org.keycloak:keycloak-server-spi:$keycloakVersion")
    compileOnly("org.keycloak:keycloak-server-spi-private:$keycloakVersion")
    compileOnly("org.keycloak:keycloak-services:$keycloakVersion")
    compileOnly("org.keycloak:keycloak-model-storage-private:$keycloakVersion")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.17.2")
}

tasks.jar {
    archiveFileName = "volunteer-user-provider.jar"
}
