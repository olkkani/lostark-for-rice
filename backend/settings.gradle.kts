rootProject.name = "lostark-for-rice"
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
include(":common")
include(":integration-service")
include(":processor-service")
