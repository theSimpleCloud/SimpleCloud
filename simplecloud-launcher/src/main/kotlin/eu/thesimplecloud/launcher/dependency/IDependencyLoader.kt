package eu.thesimplecloud.launcher.dependency

import eu.thesimplecloud.lib.depedency.Dependency

interface IDependencyLoader {


    /**
     * Adds all specified dependencies to the pool of repositories.
     */
    fun addRepositories(vararg repositories: String)

    /**
     * Downloads all specified dependencies.
     */
    fun downloadDependencies(dependencies: List<Dependency>)

    /**
     * Downloads the dependencies if necessary and adds all dependencies to the class loader.
     */
    fun installDependencies()

}