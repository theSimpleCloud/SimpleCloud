package eu.thesimplecloud.launcher.dependency

import eu.thesimplecloud.api.depedency.Dependency

class LauncherDependencyLoader {

    fun loadLauncherDependencies() {
        val dependencyLoader = DependencyLoader.INSTANCE
        dependencyLoader.addRepositories("https://repo.maven.apache.org/maven2/", "https://repo.thesimplecloud.eu/artifactory/gradle-dev-local/")
        dependencyLoader.addDependencies(
                //Dependency("org.jline", "jline", "3.14.0"),
                Dependency("org.jline", "jline", "3.14.0"),
                Dependency("org.litote.kmongo", "kmongo", "3.11.2"),
                Dependency("eu.thesimplecloud.clientserverapi", "clientserverapi", "2.4.2-SNAPSHOT"),
                Dependency("org.slf4j", "slf4j-simple", "1.7.10"),
                Dependency("com.github.ajalt", "clikt", "2.2.0"))
        dependencyLoader.installDependencies()
    }

}