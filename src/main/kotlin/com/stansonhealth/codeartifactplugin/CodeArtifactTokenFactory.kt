package com.stansonhealth.codeartifactplugin

fun interface CodeArtifactTokenFactory {

    fun createToken(domain: String, accountId: String, region: String): String

}