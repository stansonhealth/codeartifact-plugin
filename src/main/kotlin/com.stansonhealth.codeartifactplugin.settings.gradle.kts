val serviceProvider = gradle.sharedServices.registerIfAbsent("codeArtifactRepoConfigurer", com.stansonhealth.codeartifactplugin.CodeArtifactRepoConfigurer::class.java) {

}

gradle.allprojects {
    afterEvaluate {
         repositories.forEach {
            if (it is MavenArtifactRepository) {
                serviceProvider.get().configureRepo(it)
            }
        }
    }
}
