# codeartifact-plugin

[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/co/bound/plugins/maven-metadata.xml.svg?colorB=007ec6&label=Plugin%20Portal)](https://plugins.gradle.org/plugin/co.bound.codeartifact)

Configures credentials for all Maven [AWS CodeArtifact](https://aws.amazon.com/codeartifact/) Repositories defined within settings and project files.

**Note:** Because Gradle does not provide hooks for configuring Repositories within a BuildScript block, this plugin
is unable to configure the credentials

## Why another CodeArtifact plugin?

Besides setting credentials for all repositories, this plugin provides the following advantages:

- Supports JDK 1.8 and greater
- Doesn't require a separate configuration for the AWS Codeartifact repository. The plugin parses the URL specified in the repository definiton to determine configuration values.
- Doesn't standup a proxy, so there is no network jankiness
- Doesn't require the AWS CLI to be installed

## How to use

Add the following into `settings.gradle`:
```
plugins {
    id("com.stansonhealth.codeartifact").version("1.0.0")
}
```

AWS credentials from the `default` profile.

