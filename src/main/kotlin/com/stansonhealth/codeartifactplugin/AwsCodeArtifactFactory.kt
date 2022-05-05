package com.stansonhealth.codeartifactplugin

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.codeartifact.CodeartifactClient
import software.amazon.awssdk.services.codeartifact.CodeartifactClientBuilder

class AwsCodeArtifactFactory(
    val codeArtifactClientFactory: CodeArtifactClientBuilderFactory =
        CodeArtifactClientBuilderFactory { CodeartifactClient.builder() }
) : CodeArtifactTokenFactory {

    fun interface CodeArtifactClientBuilderFactory {
        fun buildClientBuilder(): CodeartifactClientBuilder
    }

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