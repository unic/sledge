import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "io.sledge"
description = "Easy deployment tool for AEM applications"

plugins {
    `maven-publish`
    kotlin("jvm") version "1.3.61"
    id ("kotlinx-serialization") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("net.researchgate.release") version "2.8.1"
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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.ajalt:clikt:2.3.0")
    implementation("com.charleskorn.kaml:kaml:0.14.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.10")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.10")
    implementation("com.squareup.okhttp3:okhttp:4.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
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

tasks.named("afterReleaseBuild") { dependsOn("shadowJar") }
