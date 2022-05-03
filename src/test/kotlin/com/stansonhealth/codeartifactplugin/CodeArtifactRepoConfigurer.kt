package com.stansonhealth.codeartifactplugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText

class CodeArtifactRepoConfigurer {

    @Test
    fun `can we drive things?`() {
        val projectDir = System.getProperty("user.dir")
        val tempDir = createTempDir("codeartifacttest")
        File("${tempDir.absolutePath}/settings.gradle").writeText("""
            plugins {
              id("com.stansonhealth.codeartifactplugin") version "0.0.1-SNAPSHOT"
            }

        """.trimIndent())
        File("${tempDir.absolutePath}/build.gradle").writeText("""
            repositories {
            	maven {
            		name = "code-artifact"
            		url = new java.net.URI("https://stansonhealth-862916455355.d.codeartifact.us-east-1.amazonaws.com/maven/stansonhealth/")
            	}
            }
        """.trimIndent())
        val gradleRunner = GradleRunner.create()
            .withProjectDir(tempDir)
            .withDebug(true)
            .withPluginClasspath()
        gradleRunner
            .withArguments("build", "--stacktrace")
            .build()
    }
}