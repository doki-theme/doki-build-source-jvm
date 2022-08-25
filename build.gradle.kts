import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.7.10"
  id("java-library")
  id("maven-publish")
  // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
  id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

group = "io.unthrottled.doki.build.jvm"
version = "88.0.1"

java {
  withSourcesJar()
}

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  testImplementation(kotlin("test"))
  implementation("com.google.code.gson:gson:2.9.0")
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
  }
}
