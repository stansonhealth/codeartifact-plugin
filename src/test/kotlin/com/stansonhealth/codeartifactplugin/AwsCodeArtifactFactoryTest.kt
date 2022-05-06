package com.stansonhealth.codeartifactplugin

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.codeartifact.CodeartifactClient
import software.amazon.awssdk.services.codeartifact.CodeartifactClientBuilder
import software.amazon.awssdk.services.codeartifact.model.GetAuthorizationTokenRequest
import software.amazon.awssdk.services.codeartifact.model.GetAuthorizationTokenResponse
import java.util.function.Consumer

class AwsCodeArtifactFactoryTest {

    @Test
    fun `should build a token using the domain, accountId, and region`() {
        val codeArtifactClientBuilderFactory = mockk<AwsCodeArtifactFactory.CodeArtifactClientBuilderFactory>()
        val codeartifactClientBuilder = mockk<CodeartifactClientBuilder>()
        val codeartifactClient = mockk<CodeartifactClient>()
        val tokenRequestBuilderLambda = slot<Consumer<GetAuthorizationTokenRequest.Builder>>()
        val tokenRequestBuilder = mockk<GetAuthorizationTokenRequest.Builder>()
        val getAuthorizationTokenResponse = mockk<GetAuthorizationTokenResponse>()
        val success = "success"

        every { codeArtifactClientBuilderFactory.buildClientBuilder() } returns codeartifactClientBuilder
        every { codeartifactClientBuilder.region(Region.of("region")) } returns codeartifactClientBuilder
        every { codeartifactClientBuilder.build() } returns codeartifactClient
        every { codeartifactClient.getAuthorizationToken(capture(tokenRequestBuilderLambda)) } answers { getAuthorizationTokenResponse }
        every { getAuthorizationTokenResponse.authorizationToken() } returns success
        every { tokenRequestBuilder.domain("domain") } returns tokenRequestBuilder
        every { tokenRequestBuilder.domainOwner("accountId") } returns tokenRequestBuilder

        val awsCodeArtifactFactory = AwsCodeArtifactFactory(codeArtifactClientBuilderFactory)
        val result = awsCodeArtifactFactory.createToken("domain", "accountId", "region")
        tokenRequestBuilderLambda.captured.accept(tokenRequestBuilder)

        result.shouldBe(success)
    }

    @Test
    fun `should build a CodeArtifactClientBuilder`() {
        val awsCodeArtifactFactory = AwsCodeArtifactFactory()
        awsCodeArtifactFactory
            .codeArtifactClientFactory
            .buildClientBuilder()
            .shouldBeInstanceOf<CodeartifactClientBuilder>()
    }

    @Test
    fun `should correctly build cache key to include domain, accountId, and region`() {
        val awsCodeArtifactFactory = AwsCodeArtifactFactory()
        val result = awsCodeArtifactFactory.buildKey("domain", "accountId", "region")
        result.shouldBe("domain.accountId.region")
    }
}