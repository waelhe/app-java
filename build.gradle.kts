import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springmodulith.modulith") version "2.0.5"
}

group = "com.marketplace"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

extra["springModulithVersion"] = "2.0.5"
extra["testcontainersVersion"] = "2.0.4"
extra["resilience4jVersion"] = "2.3.0"
extra["bucket4jVersion"] = "8.17.0"
extra["springdocVersion"] = "3.0.2"
extra["mapstructVersion"] = "1.6.3"
extra["lombokVersion"] = "1.18.44"
extra["jacocoVersion"] = "0.8.14"

dependencies {
    // ── Spring Boot Starters ──────────────────────────────────────────
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    // ── Spring Modulith ───────────────────────────────────────────────
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-jpa")
    implementation("org.springframework.modulith:spring-modulith-starter-jdbc")
    implementation("org.springframework.modulith:spring-modulith-events-api")

    runtimeOnly("org.springframework.modulith:spring-modulith-runtime")

    // ── Database ──────────────────────────────────────────────────────
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // ── Security ──────────────────────────────────────────────────────
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // ── Resilience ────────────────────────────────────────────────────
    implementation("io.github.resilience4j:resilience4j-spring-boot3")
    implementation("io.github.resilience4j:resilience4j-circuitbreaker")
    implementation("io.github.resilience4j:resilience4j-ratelimiter")
    implementation("io.github.resilience4j:resilience4j-retry")
    implementation("io.github.resilience4j:resilience4j-timelimiter")

    // ── Rate Limiting ─────────────────────────────────────────────────
    implementation("com.bucket4j:bucket4j-core:${property("bucket4jVersion")}")
    implementation("com.bucket4j:bucket4j-redis:${property("bucket4jVersion")}")

    // ── Observability ─────────────────────────────────────────────────
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")

    // ── API Documentation ─────────────────────────────────────────────
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springdocVersion")}")

    // ── Utilities ─────────────────────────────────────────────────────
    compileOnly("org.projectlombok:lombok:${property("lombokVersion")}")
    annotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // ── Testing ───────────────────────────────────────────────────────
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
        mavenBom("io.github.resilience4j:resilience4j-bom:${property("resilience4jVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "-parameters",
        "-Amapstruct.defaultComponentModel=spring",
        "-Amapstruct.unmappedTargetPolicy=IGNORE"
    ))
}
