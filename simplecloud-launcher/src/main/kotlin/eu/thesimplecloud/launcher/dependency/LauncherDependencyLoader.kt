package eu.thesimplecloud.launcher.dependency

class LauncherDependencyLoader {


    fun loadLauncherDependencies() {
        val dependencyLoader = DependencyLoader(listOf("https://repo.maven.apache.org/maven2/", "https://repo.thesimplecloud.eu/artifactory/gradle-dev/"))
        dependencyLoader.installDependencies(listOf(
                Dependency("io.netty", "netty-all", "4.1.4.Final"),
                Dependency("eu.thesimplecloud.clientserverapi", "clientserverapi", "1.1.15-SNAPSHOT"),
                Dependency("org.mongodb", "mongo-java-driver", "3.11.0"),
                Dependency("de.flapdoodle.embed", "de.flapdoodle.embed.mongo", "2.1.2"),
                Dependency("org.reflections", "reflections", "0.9.10"),
                Dependency("com.google.code.gson", "gson", "2.8.5"),
                Dependency("com.google.guava", "guava", "18.0"),
                Dependency("org.javassist", "javassist", "3.18.2-GA"),
                Dependency("com.google.code.findbugs", "annotations", "2.0.1"),
                Dependency("commons-io", "commons-io", "2.6"),
                Dependency("com.github.ajalt", "clikt", "2.2.0")))
    }

}