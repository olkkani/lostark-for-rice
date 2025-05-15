plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.dependencyManagement)
    alias(libs.plugins.osDetector)
    alias(libs.plugins.kapt)
    alias(libs.plugins.spring)
    alias(libs.plugins.jpa)
    alias(libs.plugins.jooq.monosoul)
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
    implementation(libs.bundles.spring) {
        exclude(
            group = "org.springframework.boot",
            module = "spring-boot-starter-tomcat"
        )
    }
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    developmentOnly(libs.spring.devtools)
    // DB
    implementation(libs.bundles.persistence)
    implementation(libs.bundles.persistence.database)
    implementation(libs.jooq)
    jooqCodegen(libs.postgresql)
    jooqCodegen(libs.jooq.codgen)

    // Test and Logging
    implementation(libs.kotlin.logging)
    implementation(libs.p6spy)
    testImplementation(libs.bundles.test)
    testImplementation(libs.bundles.spring.test)
    developmentOnly(libs.bundles.persistence.database.embedded)
    testImplementation(libs.bundles.persistence.database.embedded)
    testImplementation(libs.bundles.testcontainer)
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
val jooqGeneratedOutput = project.layout.buildDirectory.dir("generated-jooq") // 경로 변수화

sourceSets {
    main {
        kotlin.srcDirs(generated)
        kotlin.srcDirs(jooqGeneratedOutput)
    }
}

tasks.named("clean") {
    doLast {
        generated.deleteRecursively()
        jooqGeneratedOutput.get().asFile.deleteRecursively()
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
tasks {
    generateJooqClasses {
        schemas.set(listOf("public"))
        basePackageName.set("org.jooq.generated")
        migrationLocations.setFromFilesystem("src/main/resources/db/migration")
        outputDirectory.set(project.layout.buildDirectory.dir("generated-jooq"))
        flywayProperties.put("flyway.placeholderReplacement", "false")
        includeFlywayTable.set(true)
        outputSchemaToDefault.add("public")
        schemaToPackageMapping.put("public", "model")

        usingJavaConfig {
            /* "this" here is the org.jooq.meta.jaxb.Generator configure it as you please */
        }
    }
}