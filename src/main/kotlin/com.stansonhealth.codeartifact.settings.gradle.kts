import com.stansonhealth.codeartifactplugin.CodeArtifactRepoConfigurer

gradle.settingsEvaluated {
    pluginManagement {
        configureRepositories(repositories)
    }
    dependencyResolutionManagement {
        configureRepositories(repositories)
    }
}

gradle.allprojects {
    afterEvaluate {
        configureRepositories(repositories)
        extensions.findByType(PublishingExtension::class.java)?.let { publishingExtension ->
            configureRepositories(publishingExtension.repositories)
        }
    }
}

fun configureRepositories(
    repositoryHandler: RepositoryHandler
) {
    val serviceProvider = getCodeArtifactRepoConfigurer()
    repositoryHandler.forEach { repo ->
        if (repo is MavenArtifactRepository) {
            serviceProvider.configureRepo(repo)
        }
    }
}

fun getCodeArtifactRepoConfigurer() = gradle.sharedServices
    .registerIfAbsent(
        "codeArtifactRepoConfigurer",
        CodeArtifactRepoConfigurer::class.java
    ) {}
    .get()