package eu.thesimplecloud.launcher

import eu.thesimplecloud.launcher.dependency.Dependency
import eu.thesimplecloud.launcher.dependency.DependencyLoader

fun main() {
    //Launcher().start()

    val dependencyLoader = DependencyLoader(listOf("https://repo.maven.apache.org/maven2/", "https://repo.thesimplecloud.eu/artifactory/gradle-dev/"))
    dependencyLoader.installDependencies(listOf(Dependency("io.netty", "netty-all", "4.1.4.Final"),
            Dependency("commons-io", "commons-io", "2.6"),
            Dependency("eu.thesimplecloud.clientserverapi", "clientserverapi", "1.0.8-SNAPSHOT")))

}