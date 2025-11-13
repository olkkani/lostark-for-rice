plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spring.boot)
}

description = "integration-service"

dependencies {
    implementation(project(":common"))
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation(libs.tsid.creator)
    implementation(libs.bundles.kotlin.coroutines)
    implementation(libs.bundles.spring.security)
    implementation(libs.spring.oauth2)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.bundles.jjwt)



    developmentOnly(libs.spring.devtools)
    testImplementation(libs.bundles.kotlin.coroutines.test)

}


tasks.register("prepareKotlinBuildScriptModel")