import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.sledge"
version = "2.0.0-SNAPSHOT"
description = "Easy deployment tool for AEM applications"

plugins {
    `build-scan`
    `maven-publish`
    kotlin("jvm") version "1.3.31"
    id ("kotlinx-serialization") version "1.3.40"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    // publishAlways()
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository")
        }
    }
}

repositories {
    jcenter()
}

dependencies {
    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0")
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.ajalt:clikt:2.0.0")
    implementation("com.charleskorn.kaml:kaml:0.11.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.8")
    implementation("com.squareup.okhttp3:okhttp:4.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "io.sledge.deployer.SledgeDeployerAppKt"
    }

}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// config JVM target to 1.8 for kotlin compilation tasks
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
