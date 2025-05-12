plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.dependencyManagement)
    alias(libs.plugins.osDetector)
    alias(libs.plugins.kapt)
    alias(libs.plugins.spring)
    alias(libs.plugins.jpa)
}

group = "io.oikkani"
//version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring
    implementation(libs.bundles.spring) { exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat") }
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    developmentOnly(libs.spring.devtools)
    // DB
    implementation(libs.bundles.persistence)
    // Test and Logging
    implementation(libs.kotlin.logging)
    implementation(libs.p6spy)
    testImplementation(libs.bundles.test)
    testImplementation(libs.bundles.spring.test)
    developmentOnly(libs.bundles.persistence.database.embedded)
    testImplementation(libs.bundles.persistence.database.embedded)
    // the other
    implementation(libs.jackson.kotlin)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.commons.text)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.reactor)
    implementation(libs.reflect.kotlin)

    if (osdetector.arch.equals("aarch_64")) {
        implementation(libs.dns.native.mac) {
            artifact {
                classifier = "osx-aarch_64"
            }
        }
    }
}
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
    jvmArgs("-Xshare:off")
}