package com.stansonhealth.codeartifactplugin

import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.codeartifact.CodeartifactClient

abstract class CodeArtifactRepoConfigurer: BuildService<BuildServiceParameters.None> {

    private val codeArtifactClients = mutableMapOf<String, String>()

    companion object {
        val AWS_USER = "AWS"
        val CODEARTIFACT_URL = Regex("^http[s]?://([^\\.^-]+)-([^\\.]+)\\.[^\\.]+\\.codeartifact\\.([^\\.]+)\\..*\$")
    }

    fun configureRepo(repo: MavenArtifactRepository) {
        CODEARTIFACT_URL.matchEntire(repo.url.toASCIIString())?.let {
            val (domain, accountId, region) = it.destructured
            val codeArtifactToken = getToken(domain, accountId, region)
            repo.credentials {
                username = AWS_USER
                password = codeArtifactToken
            }
        }
    }

    fun getToken(domain: String, accountId: String, region: String): String =
        codeArtifactClients.getOrPut("$domain.$accountId.$region") {
            CodeartifactClient
                .builder()
                .region(Region.of(region))
                .build()
                .getAuthorizationToken {
                    it.domain(domain).domainOwner(accountId)
                }
                .authorizationToken()
       }
}