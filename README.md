# codeartifact-plugin

Configures credentials for all Maven [AWS CodeArtifact](https://aws.amazon.com/codeartifact/) Repositories defined within the gradle settings and project files.

**Note:** Because Gradle does not provide hooks for configuring Repositories within a `buildScript` block, this plugin
is unable to configure the credentials for `buildScript` repositories.

## Advantages/Features

Along with setting credentials for all repositories, this plugin provides the following advantages/features:

- Supports JDK 1.8 and greater
- Dynamically determines whether the publishing plugin is present and configures credentials accordingly.  It does
not require a separate plugin to support publishing.
- Doesn't require a separate configuration for the AWS Codeartifact repository. The plugin parses the URL specified in the repository definition to determine configuration values.
- Doesn't use a proxy, so there are no networking issues
- Doesn't require the AWS CLI to be installed

## Installation/Usage

All that is required to use the plug is to add the following into `settings.gradle`:
```
plugins {
    id("com.stansonhealth.codeartifact") version "1.0.0"
}
```

**Note:** The AWS credentials produced are derived from your AWS `default` profile.

