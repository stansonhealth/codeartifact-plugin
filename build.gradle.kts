import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.20.0"
    kotlin("jvm") version "1.6.20"
}

group = "com.stansonhealth"
version = "1.0.0"

java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21")
    implementation("software.amazon.awssdk:codeartifact:2.17.181")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("io.kotest:kotest-assertions-core:5.3.0")
    testImplementation("io.mockk:mockk:1.12.3")
    testImplementation(gradleTestKit())
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
            jvmTarget = "1.8"
        }
    }

    withType<AbstractPublishToMaven> {
        dependsOn("check")
    }
}

pluginBundle {
    website = "https://github.com/stansonhealth/codeartifact-plugin"
    vcsUrl = "https://github.com/stansonhealth/codeartifact-plugin"
    description = "AWS Authentication Plugins"
    (plugins) {
        "com.stansonhealth.codeartifact" {
            displayName = "Plugin to set credentials for AWS Codeartifact repositories"
            description = "Configures credentials for all AWS Codeartifact repositories defined in the settings and project files"
            tags = mutableListOf("aws", "codeartifact", "publishing", "repositories")
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}