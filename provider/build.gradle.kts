plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "9.4.3"
}

group = "com.portocale.volunteer.kc"
version = "1.0.0"

val keycloakVersion = "26.7.3"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    // Bundled into the jar: Keycloak ships no Kotlin runtime
    implementation(kotlin("stdlib"))

    // Provided by the Keycloak server at runtime - never bundled
    compileOnly("org.keycloak:keycloak-core:$keycloakVersion")
    compileOnly("org.keycloak:keycloak-server-spi:$keycloakVersion")
    compileOnly("org.keycloak:keycloak-server-spi-private:$keycloakVersion")
    compileOnly("org.keycloak:keycloak-services:$keycloakVersion")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.17.2")
}

tasks.shadowJar {
    archiveFileName = "volunteer-user-provider.jar"
    // Keycloak shares one classloader across providers, so the Kotlin runtime
    // is relocated to avoid clashing with anything Keycloak may ship later
    relocate("kotlin", "com.portocale.volunteer.kc.shaded.kotlin")
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}