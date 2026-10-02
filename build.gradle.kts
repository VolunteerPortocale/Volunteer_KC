plugins {
  kotlin("jvm") version "2.2.21"
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
  implementation("com.squareup.okhttp3:okhttp:5.3.2")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")
}

tasks.jar {
  enabled = false
}

tasks.shadowJar {
  archiveFileName = "volunteer-user-provider.jar"

  mergeServiceFiles()
}

tasks.build {
  dependsOn(tasks.shadowJar)
}

