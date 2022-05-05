import com.stansonhealth.codeartifactplugin.CodeArtifactRepoConfigurer

gradle.settingsEvaluated {
    pluginManagement {
        configureRepositories(repositories)
    }
    dependencyResolutionManagement {
        configureRepositories(repositories)
    }
    extensions.findByType(PublishingExtension::class.java)?.let { publishingExtension ->
        configureRepositories(publishingExtension.repositories)
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
    repositoryHandler.forEach {
        if (it is MavenArtifactRepository) {
            serviceProvider.configureRepo(it)
        }
    }
}

fun getCodeArtifactRepoConfigurer() = gradle.sharedServices
    .registerIfAbsent(
        "codeArtifactRepoConfigurer",
        CodeArtifactRepoConfigurer::class.java
    ) {}
    .get()