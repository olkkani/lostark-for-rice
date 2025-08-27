plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spring.boot)
}

description = "integration-service"

//            allOpen {
//                annotation("jakarta.persistence.Entity")
//                annotation("jakarta.persistence.MappedSuperclass")
//                annotation("jakarta.persistence.Embeddable")



dependencies {
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    developmentOnly(libs.spring.devtools)
    implementation(libs.bundles.kotlin.coroutines)
    testImplementation(libs.bundles.kotlin.coroutines.test)
    implementation(libs.bundles.spring.security)
    runtimeOnly(libs.bundles.jjwt)

}


tasks.register("prepareKotlinBuildScriptModel")