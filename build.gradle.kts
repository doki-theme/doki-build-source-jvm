import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.7.22"
  id("java-library")
  id("maven-publish")
  // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
  id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

group = "io.unthrottled.doki.build.jvm"
version = "88.0.5"

java {
  withSourcesJar()
}

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  testImplementation(kotlin("test"))
  api("com.google.code.gson:gson:2.10")
}

tasks.test {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

publishing {
  publications {
    create<MavenPublication>("myLibrary") {
      from(components["java"])
    }
  }

  repositories {
    mavenLocal()
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/doki-theme/doki-build-source-jvm")
      credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }
}
