plugins {
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.jooq.monosoul)
}

description = "processor-service"

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}


dependencies {
    implementation(project(":common"))
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    developmentOnly(libs.spring.devtools)
    implementation(libs.bundles.kotlin.coroutines)
    testImplementation(libs.bundles.kotlin.coroutines.test)
    implementation(libs.bundles.spring.security)
    //DB
    implementation(libs.bundles.persistence)
    implementation(libs.bundles.persistence.database)
    implementation(libs.p6spy)
    developmentOnly(libs.bundles.persistence.database.embedded)
    testImplementation(libs.bundles.persistence.test.testcontainer)
    jooqCodegen(libs.postgresql)

}

val jooqGeneratedOutput = project.layout.buildDirectory.dir("generated-jooq") // 경로 변수화
sourceSets {
    main {
        kotlin.srcDirs(jooqGeneratedOutput)
    }
}
tasks.named("clean") {
    doLast {
        jooqGeneratedOutput.get().asFile.deleteRecursively()
    }
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



tasks.register("prepareKotlinBuildScriptModel")