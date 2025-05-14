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

//    val jooqVersion = "3.20.3" // jooq {} 블록에 설정된 버전과 일치시킵니다.
//    jooqGenerator("org.jooq:jooq:$jooqVersion")
//    jooqGenerator("org.jooq:jooq-meta:$jooqVersion")
//    jooqGenerator("org.jooq:jooq-meta-extensions:$jooqVersion")
//
//    jooqGenerator(libs.bundles.persistence)
//    jooqGenerator(libs.h2)
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
//val jooqGenerated = file("build/generated-src/jooq/main")
sourceSets {
    main {
        kotlin.srcDirs += generated
//        kotlin.srcDirs += jooqGenerated
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
//jooq {
//    version.set("3.20.3")  // default (can be omitted), libs.versions jooq 과 버전 통일
//    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)  // default (can be omitted)
//
//    configurations {
//        create("main") {
//            generateSchemaSourceOnCompilation.set(false)
//
//            jooqConfiguration.apply {
//                logging = org.jooq.meta.jaxb.Logging.WARN
//
//                jdbc = null // JDBC 연결을 사용하지 않음 (JPA 엔티티로부터 생성)
//
//                generator.apply {
//                    name = "org.jooq.codegen.KotlinGenerator" // Kotlin 코드 생성
//                    database.apply {
//                        name = "org.jooq.meta.extensions.jpa.JPADatabase"
//                        properties = listOf(
//                            org.jooq.meta.jaxb.Property().apply {
//                                key = "packages"
//                                value = "io.olkkani.lfr.entity.jpa" // JPA 엔티티 패키지 경로
//                            },
//                            org.jooq.meta.jaxb.Property().apply {
//                                key = "useAttributeConverters"
//                                value = "true"
//                            },
//                            org.jooq.meta.jaxb.Property().apply {
//                                key = "dialect"
//                                value = "POSTGRES" // PostgreSQL 방언 사용
//                            }
//                        )
//                    }
//
//                    generate.apply {
//                        isDeprecated = false
//                        isRecords = true
//                        isImmutablePojos = true
//                        isFluentSetters = true
//                        isJavaTimeTypes = true
//                        isPojosEqualsAndHashCode = true
//                    }
//
//                    target.apply {
//                        packageName = "io.olkkani.generated.jooq" // 생성된 코드 패키지 경로
//                        directory = "build/generated-src/jooq/main" // 생성된 코드 저장 위치
//                    }
//
//                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
//                }
//            }
//        }
//    }
//}