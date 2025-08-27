//plugins {
//
//}
//
//version = "unspecified"
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//
//
//}
//
//tasks.test {
//    useJUnitPlatform()
//}
//
//kotlin {
//    jvmToolchain(21)
//}

plugins {
//    kotlin("jvm")
//    alias(libs.plugins.kotlinjvm)
}
//java {
//    toolchain {
//        languageVersion = JavaLanguageVersion.of(21)
//    }
//}
//kotlin {
//    jvmToolchain(21)
//}

dependencies {
//    implementation(libs.kotlin.stdlib)

//    val osName = System.getProperty("os.name").lowercase()
//    val osArch = System.getProperty("os.arch")

//    if (osName.contains("mac") && osArch == "aarch64") {  // 👈 aarch64 (언더스코어 없음)
//        implementation(libs.dns.native.mac) {
//            artifact {
//                classifier = "osx-aarch_64"
//            }
//        }
//    }
}

tasks.register("prepareKotlinBuildScriptModel")