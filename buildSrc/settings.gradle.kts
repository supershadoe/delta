pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  versionCatalogs {
    create("libs") { from(files("../gradle/libs.versions.toml")) }
  }
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "delta-build"
