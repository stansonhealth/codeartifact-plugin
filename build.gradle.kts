plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.20.0"
    kotlin("jvm") version "1.6.20"
}

group = "com.stansonhealth"
version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(platform("software.amazon.awssdk:bom:2.17.181"))
    implementation("software.amazon.awssdk:codeartifact")
    implementation("software.amazon.awssdk:sts")
    implementation("software.amazon.awssdk:sso")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("io.kotest:kotest-assertions-core:4.6.4")
    testImplementation("io.kotest:kotest-property:4.6.4")
    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation(gradleTestKit())
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

afterEvaluate {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            apiVersion = "1.6"
            languageVersion = "1.6"
        }
    }
}

gradlePlugin {
    plugins {
        create("codeartifactPlugin") {
            id = "com.stansonhealth.codeartifactplugin"
            implementationClass = ""
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}