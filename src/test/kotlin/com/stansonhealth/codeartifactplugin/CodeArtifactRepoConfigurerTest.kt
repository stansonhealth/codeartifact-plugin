package com.stansonhealth.codeartifactplugin

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.invoke
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URI

class CodeArtifactRepoConfigurerTest {


    companion object {
        val tempDir = createTempDir("codeartifacttest")
    }

    @Test
    fun `should set credentials of a Repository to the AWS User and expected token`() {
        val tokenFactory = mockk<CodeArtifactTokenFactory>()
        val tokenFactoryProperty = mockk<Property<CodeArtifactTokenFactory>>()
        val expectedToken = "expectedToken"
        val mavenArtifactRepository = mockk<MavenArtifactRepository>()
        val passwordCredentials = spyk<PasswordCredentials>()
        val credentialsLambda = slot<Action<in PasswordCredentials>>()

        every { tokenFactory.createToken("domain", "accountId", "region") } returns expectedToken
        every { mavenArtifactRepository.url } returns URI("https://domain-accountId.d.codeartifact.region.amazonaws.com/repo/")
        every { mavenArtifactRepository.credentials(capture(credentialsLambda)) } answers {}
        every { tokenFactoryProperty.getOrElse(any()) } returns tokenFactory

        val codeArtifactRepoConfigurer = object : CodeArtifactRepoConfigurer() {
            override fun getParameters() =
                object : Params {
                    override fun getTokenFactory() = tokenFactoryProperty
                }
        }
        codeArtifactRepoConfigurer.configureRepo(mavenArtifactRepository)
        credentialsLambda.captured.invoke(passwordCredentials)

        verify { passwordCredentials.username = CodeArtifactRepoConfigurer.AWS_USER }
        verify { passwordCredentials.password = expectedToken }
    }

    @Test
    fun `should set credentials for repositories in the settings and project build files`() {
        buildSettingsFile("""
            gradle.settingsEvaluated {
                println("Settings Results:")
                verifyRepositories(pluginManagement.repositories)
                verifyRepositories(dependencyResolutionManagement.repositories)
                println("")
            }
            
            gradle.allprojects {
                afterEvaluate {
                    println("Project Build File Results:")
                    verifyRepositories(repositories)
                }
            }
        """.trimIndent())
        buildBuildFile("""
                repositories {
                    maven {
                        name = "project repository"
                        url = java.net.URI("https://domain-accountId.d.codeartifact.region.amazonaws.com/repo/")
                    }
                }
            """.trimIndent()
        )
        runBuild()
    }

    @Test
    fun `should not set credentials for non-codeartifact repositories`() {
        buildSettingsFile("""
            gradle.allprojects {
                afterEvaluate {
                    repositories.size.shouldBe(3)
                    repositories.forEach { repo ->
                        if (repo is MavenArtifactRepository) {
                            repo.credentials.username.shouldBe(null)
                            repo.credentials.password.shouldBe(null)
                        }
                    }
                    println("None of the repositories have credentials set")
                }
            }
        """.trimIndent())
        buildBuildFile("""
            repositories {
                mavenLocal()
                mavenCentral()
                maven {
                    url = java.net.URI("https://foo.com/repo/")
                }
            }
        """.trimIndent())
        runBuild()
    }

    @Test
    fun `should set credentials on the publishing repositories`() {
        buildSettingsFile("""
            gradle.allprojects {
                afterEvaluate {
                    println("Project Build File Publishing Results:")
                    extensions.findByType(PublishingExtension::class.java)?.let { publishingExtension ->
                        verifyRepositories(publishingExtension.repositories)
                    }
                }
            }
        """.trimIndent())
        buildBuildFile("""
            plugins {
                `maven-publish`
            }
            
            publishing {
                repositories {
                    maven {
                        name = "publishing-codeartifact-repo"
                        url = java.net.URI("https://domain-accountId.d.codeartifact.region.amazonaws.com/repo")
                    }
                }
            }
        """.trimIndent())
        runBuild()
    }

    private fun buildSettingsFile(verification: String) {
        val classpath = System.getProperty("java.class.path").replace(":", "\",\"")
        val workingDir = System.getProperty("user.dir")
        val cp = "\"$workingDir/build/classes/kotlin/main\",\"$classpath\""
        File("${tempDir.absolutePath}/settings.gradle.kts").writeText("""
            import com.stansonhealth.codeartifactplugin.CodeArtifactRepoConfigurer
            import com.stansonhealth.codeartifactplugin.CodeArtifactTokenFactory
            import java.io.Serializable
            import java.net.URI
            import io.kotest.matchers.shouldBe
            
            pluginManagement {
                repositories {
                    maven {
                        name = "settings plugin management repositories"
                        url = java.net.URI("https://domain-accountId.d.codeartifact.region.amazonaws.com/repo/")
                    }
                }
            }

            buildscript {
                dependencies {
                    classpath(files($cp))
                }
            }
                       
            class MockFactory : CodeArtifactTokenFactory, Serializable {
                override fun createToken(domain: String, accountId: String, region: String) = "success"
            }

            gradle.sharedServices.registerIfAbsent("codeArtifactRepoConfigurer", CodeArtifactRepoConfigurer::class.java) {
                parameters.getTokenFactory().set(MockFactory())
            }
            
            dependencyResolutionManagement {
                repositories {
                    maven {
                        name = "settings dependency resolution management"
                        url = URI("https://domain-accountId.d.codeartifact.region.amazonaws.com/repo/")
                    }
                }
            }
            plugins {
              id("com.stansonhealth.codeartifact")
            }
            
            $verification

            fun verifyRepositories(
                repositoryHandler: RepositoryHandler
            ) {
                repositoryHandler.forEach {
                    if (it is MavenArtifactRepository) {
                        println("\t" + it.name + ":")
                        println("\t\tusername:" + it.credentials.username)
                        it.credentials.username.shouldBe("AWS")
                        println("\t\tpassword:" + it.credentials.password)
                        it.credentials.password.shouldBe("success")
                    }
                }
            }
        """.trimIndent()
        )
    }

    private fun buildBuildFile(content: String) {
        File("${tempDir.absolutePath}/build.gradle.kts").writeText(content)
    }

    private fun runBuild() {
        val results = GradleRunner.create()
            .withProjectDir(tempDir)
            .withDebug(true)
            .withPluginClasspath()
            .withArguments("build", "--stacktrace")
            .build()
        println(results.output)
    }
}