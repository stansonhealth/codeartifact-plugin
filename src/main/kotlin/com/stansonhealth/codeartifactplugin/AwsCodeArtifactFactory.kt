package com.stansonhealth.codeartifactplugin

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.codeartifact.CodeartifactClient
import software.amazon.awssdk.services.codeartifact.CodeartifactClientBuilder

class AwsCodeArtifactFactory(
    codeArtifactClientFactory: CodeArtifactClientBuilderFactory? = null
) : CodeArtifactTokenFactory {

    fun interface CodeArtifactClientBuilderFactory {
        fun buildClientBuilder(): CodeartifactClientBuilder
    }

    val codeArtifactClientFactory = codeArtifactClientFactory
        ?: CodeArtifactClientBuilderFactory { CodeartifactClient.builder() }

    private val codeArtifactClients = mutableMapOf<String, String>()

    override fun createToken(domain: String, accountId: String, region: String): String =
        codeArtifactClients.getOrPut(buildKey(domain, accountId, region)) {
            codeArtifactClientFactory.buildClientBuilder()
                .region(Region.of(region))
                .build()
                .getAuthorizationToken {
                    it.domain(domain).domainOwner(accountId)
                }
                .authorizationToken()
        }

    fun buildKey(domain: String, accountId: String, region: String) =
        "$domain.$accountId.$region"

}