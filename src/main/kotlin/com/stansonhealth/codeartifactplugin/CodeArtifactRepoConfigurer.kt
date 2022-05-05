package com.stansonhealth.codeartifactplugin

import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class CodeArtifactRepoConfigurer: BuildService<CodeArtifactRepoConfigurer.Params> {

    interface Params : BuildServiceParameters {
        fun getTokenFactory() : Property<CodeArtifactTokenFactory>
    }

    companion object {
        const val AWS_USER = "AWS"
        val CODEARTIFACT_URL = Regex("https://([^.^-]+)-([^.]+)\\.[^.]+\\.codeartifact\\.([^.]+)\\..*")
    }

    private val codeArtifactTokenFactory = parameters
        .getTokenFactory()
        .getOrElse(AwsCodeArtifactFactory())

    open fun configureRepo(repo: MavenArtifactRepository) {
        CODEARTIFACT_URL.matchEntire(repo.url.toASCIIString())?.let {
            val (domain, accountId, region) = it.destructured
            repo.credentials {
                username = AWS_USER
                password = codeArtifactTokenFactory.createToken(domain, accountId, region)
            }
        }
    }
}