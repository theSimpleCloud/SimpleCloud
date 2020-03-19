package eu.thesimplecloud.launcher.dependency

import eu.thesimplecloud.api.depedency.Dependency

class LauncherDependencyLoader {

    fun loadLauncherDependencies() {
        val dependencyLoader = DependencyLoader.INSTANCE
        dependencyLoader.addRepositories("https://repo.maven.apache.org/maven2/", "https://repo.thesimplecloud.eu/artifactory/gradle-dev-local/")
        dependencyLoader.addDependencies(
                Dependency("org.jline", "jline", "3.13.2"),
                Dependency("org.litote.kmongo", "kmongo", "3.11.2"),
                Dependency("io.netty", "netty-all", "4.1.4.Final"),
                Dependency("eu.thesimplecloud.clientserverapi", "clientserverapi", "2.2.1-SNAPSHOT"),
                Dependency("org.reflections", "reflections", "0.9.10"),
                Dependency("com.google.code.gson", "gson", "2.8.6"),
                Dependency("commons-io", "commons-io", "2.6"),
                Dependency("org.slf4j", "slf4j-simple", "1.7.10"),
                Dependency("com.github.ajalt", "clikt", "2.2.0"))
        dependencyLoader.installDependencies()
    }

}