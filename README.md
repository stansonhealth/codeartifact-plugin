# codeartifact-plugin

[comment]: <> ([![Gradle Plugin Portal]&#40;https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/co/bound/plugins/maven-metadata.xml.svg?colorB=007ec6&label=Plugin%20Portal&#41;]&#40;https://plugins.gradle.org/plugin/co.bound.codeartifact&#41;)

Configures credentials for all Maven [AWS CodeArtifact](https://aws.amazon.com/codeartifact/) Repositories defined within the settings and project files.

**Note:** Because Gradle does not provide hooks for configuring Repositories within a BuildScript block, this plugin
is unable to configure the credentials

## Advanates/Features

Besides setting credentials for all repositories, this plugin provides the following advantages:

- Supports JDK 1.8 and greater
- Dynamically determines whether the publishing plugin is present and configures credentials accordingly.  It does
not require a separate plugin to specifically support publishing.
- Doesn't require a separate configuration for the AWS Codeartifact repository. The plugin parses the URL specified in the repository definiton to determine configuration values.
- Doesn't standup a proxy, so there is no networking issues
- Doesn't require the AWS CLI to be installed

## Installation/Usage

All is required is to add the following into `settings.gradle`:
```
plugins {
    id("com.stansonhealth.codeartifact").version("1.0.0")
}
```

The AWS credentials produced are derived from your AWS `default` profile.

