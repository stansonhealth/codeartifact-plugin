
plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    groovy
    id("com.gradle.plugin-publish") version "0.20.0"
    kotlin("jvm") version "1.6.20"
}

group = "com.stansonhealth"
version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_1_8

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
    testImplementation("io.kotest:kotest-assertions-core:5.2.3")
    testImplementation("io.kotest:kotest-property:5.2.3")
    testImplementation("io.mockk:mockk:1.12.3")
    testImplementation(gradleTestKit())
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
//            jvmTarget = "11"
        }
    }
}

//afterEvaluate {
//    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//        kotlinOptions {
//            jvmTarget = "11"
//            apiVersion = "1.6"
//            languageVersion = "1.6"
//        }
//    }
//}
//
gradlePlugin {
//    plugins {
//        create("codeartifactPlugin") {
//            id = "com.stansonhealth.codeartifact"
//            implementationClass = ""
//        }
//    }
}

publishing {
    repositories {
        mavenLocal()
    }
}