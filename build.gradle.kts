plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    kotlin("plugin.jpa") version "2.0.21"
    kotlin("kapt") version "2.0.21"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "io.oikkani"
//version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val kotestVersion="5.9.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")
    implementation("io.projectreactor:reactor-core:3.7.0")
    implementation("org.springframework.data:spring-data-rest-hal-explorer")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("com.querydsl:querydsl-jpa"){artifact{classifier="jakarta"}} // QueryDSL 의존성
    implementation("com.querydsl:querydsl-apt")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("com.github.f4b6a3:tsid-creator:5.2.6")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("io.hypersistence:hypersistence-utils-hibernate-60:3.9.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    kapt("com.querydsl:querydsl-kotlin-codegen")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("com.querydsl:querydsl-apt"){artifact{classifier="jakarta"}}
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.25")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.3")
}

// QueryDSL Setting
val generated = file("build/generated/kapt/main/kotlin")
sourceSets {
    main {
        kotlin.srcDirs += generated
    }
}


tasks.named("clean") {
    doLast {
        generated.deleteRecursively()
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
