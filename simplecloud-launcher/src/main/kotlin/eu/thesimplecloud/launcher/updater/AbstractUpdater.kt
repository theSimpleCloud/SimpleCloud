package eu.thesimplecloud.launcher.updater

import eu.thesimplecloud.api.depedency.Dependency
import eu.thesimplecloud.launcher.dependency.DependencyLoader
import java.io.File
import java.lang.RuntimeException

abstract class AbstractUpdater(
        private val groupId: String,
        private val artifactId: String,
        private val updateFile: File
) : IUpdater {

    private val latestVersionByLazy by lazy { DependencyLoader.INSTANCE.getLatestVersionOfDependencyFromWeb(groupId, artifactId, getRepositoryURL()) }

    override fun getLatestVersion(): String? = latestVersionByLazy

    override fun downloadJarsForUpdate() {
        val latestVersion = getLatestVersion() ?: throw RuntimeException("Cannot perform update. Is the server down? (repo: ${getRepositoryURL()})")
        val dependency = Dependency(groupId, artifactId, latestVersion)
        dependency.download(getRepositoryURL(), updateFile)
    }



}