plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.dependency.management) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.jooq.monosoul) apply false
}

repositories {
    mavenCentral()
}

allprojects {
    group = "io.olkkani"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}
subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "java")
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
    kotlin {
        jvmToolchain(21)
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    configurations.all {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }

    dependencies {
        implementation(rootProject.libs.bundles.kotlin)
        implementation(rootProject.libs.bundles.common)
        testImplementation(rootProject.libs.bundles.test)

        val osName = System.getProperty("os.name").lowercase()
        val osArch = System.getProperty("os.arch")
        if (osName.contains("mac") && osArch == "aarch64") {
            implementation(rootProject.libs.dns.native.mac) {
                artifact {
                    classifier = "osx-aarch_64"
                }
            }
        }
    }



    if (name in listOf("integration-service", "processor-service")) {
        apply(plugin = "org.springframework.boot")
        apply(plugin = "org.jetbrains.kotlin.plugin.spring")
        apply(plugin = "io.spring.dependency-management")
        apply(plugin = "org.jetbrains.kotlin.kapt")


//        val mockitoAgent = configurations.create("mockitoAgent")
        dependencies {
//            mockitoAgent(rootProject.libs.mockk) { isTransitive = false }
            implementation(rootProject.libs.bundles.spring) {
                exclude(
                    group = "org.springframework.boot",
                    module = "spring-boot-starter-tomcat"
                )
            }
            testImplementation(rootProject.libs.bundles.spring.test)
        }
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
//            jvmArgs("-javaagent:${mockitoAgent.asPath}", "-Xshare:off")
            jvmArgs(
                "-Xshare:off",
                "-XX:+EnableDynamicAgentLoading" // JDK 21+ Mock config. hide warning message

            )
        }
        tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
            archiveVersion.set("")
        }
    }
    if (name in listOf("processor-service")) {
        apply(plugin = "dev.monosoul.jooq-docker")
    }

}


