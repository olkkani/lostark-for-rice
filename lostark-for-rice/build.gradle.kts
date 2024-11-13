plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"

	kotlin("plugin.serialization") version "1.9.25"
	application
}

group = "io.oikkani"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

val ktorVersion = "3.0.1"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("io.ktor:ktor-client-cio-jvm:3.0.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")


//	implementation("io.ktor:ktor-client-core:$ktorVersion")
//	implementation("io.ktor:ktor-client-cio:$ktorVersion")
//	implementation("io.ktor:ktor-client-content-negotiation::$ktorVersion")
//	implementation("io.ktor:ktor-serialization-kotlinx-json::$ktorVersion")
//	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
//	implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktorVersion")
//	implementation("io.ktor:ktor-client-logging:$ktorVersion")
	runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.104.Final:osx-aarch_64")
	runtimeOnly("io.ktor:ktor-client-logging-jvm:$ktorVersion")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
